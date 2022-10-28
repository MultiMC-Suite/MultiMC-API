package fr.multimc.api.spigot.managers.games.instances;

import fr.multimc.api.commons.tools.times.TimeParsing;
import fr.multimc.api.spigot.managers.games.GameType;
import fr.multimc.api.spigot.managers.teams.APIPlayer;
import fr.multimc.api.spigot.managers.teams.Team;
import fr.multimc.api.spigot.managers.worlds.APIWorld;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class InstancesManager implements Listener {

    private final JavaPlugin plugin;
    private final List<Instance> instances;
    private List<Team> teams = new ArrayList<>();
    private final HashMap<Integer, InstanceState> instancesState = new HashMap<>();
    private final GameType gameType;
    private final Class<? extends Instance> instanceClass;
    private final InstanceSettings settings;
    private final Logger logger;
    private boolean isStarted = false;


    private final APIWorld lobby;
    private final APIWorld game;

    public InstancesManager(@NotNull JavaPlugin plugin,
                            @NotNull Class<? extends Instance> instanceClass,
                            @NotNull InstanceSettings settings,
                            @NotNull APIWorld lobby,
                            @NotNull APIWorld game) {
        this.plugin = plugin;
        this.instances = new ArrayList<>();
        this.gameType = settings.getGameType();
        this.instanceClass = instanceClass;
        this.settings = settings;
        this.logger = plugin.getLogger();
        this.lobby = lobby;
        this.game = game;
        // Register local events handlers
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void start(@NotNull List<Team> teams){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                startAsync(teams);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void startAsync(@NotNull List<Team> teams) throws InvocationTargetException, InstantiationException, IllegalAccessException, InterruptedException {
        this.instances.clear();
        this.teams = new ArrayList<>(teams);
        List<List<Team>> gameTeams = new ArrayList<>();
        switch (this.gameType) {
            case SOLO -> {
                List<Team> localTeams1 = this.getOnePlayerTeams();
                gameTeams.add(localTeams1);
            }
            case ONLY_TEAM -> {
                List<Team> localTeams2 = new ArrayList<>(teams);
                gameTeams.add(localTeams2);
            }
            case TEAM_VS_TEAM -> gameTeams = this.getTeamsTuple(this.teams);
        }
        // Create instances
        for(int i = 0; i < this.getInstanceCount(); i++){
            this.logger.info(String.format("Creating instance %d/%d", i + 1, this.getInstanceCount()));
            Location location = new Location(this.game.getWorld(), i * 1000, 100, 0);
            switch(this.gameType) {
                case SOLO -> {
                    List<Team> instanceTeams = new ArrayList<>();
                    instanceTeams.add(gameTeams.get(0).get(i));
                    this.instances.add((Instance) this.instanceClass.getConstructors()[0].newInstance(this.plugin, this, i, this.settings, location, instanceTeams));
                }
                case ONLY_TEAM ->
                        this.instances.add((Instance) this.instanceClass.getConstructors()[0].newInstance(this.plugin, this, i, this.settings, location, List.of(gameTeams.get(0).get(i))));
                case TEAM_VS_TEAM ->
                        this.instances.add((Instance) this.instanceClass.getConstructors()[0].newInstance(this.plugin, this, i, this.settings, location, gameTeams.get(i)));
            }
        }
        this.awaitState(InstanceState.CREATE);
        // Init instances
        long dt;
        long dtAvg = 0;
        for(int i = 0; i < this.instances.size(); i++){
            dt = this.initInstance(this.instances.get(i));
            if(dtAvg == 0){
                dtAvg = dt;
            }
            dtAvg = (dtAvg + dt) / 2;
            this.sendTeamActionBar(
                    Component.text(
                            String.format("Instance %d/%d initialized (%s remaining)",
                                    i + 1,
                                    this.instances.size(),
                                    TimeParsing.format(dtAvg * (this.instances.size() - i - 1), "mm:ss"))));
        }
        this.awaitState(InstanceState.INIT);
        for(int i = 0; i < 5; i++){
            Thread.sleep(1000);
            this.sendTeamTitle(Component.text(String.format("Game starts in %d", 4 - i)), Component.text(""));
            this.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        }
        // Start instances
        this.instances.forEach(this::startInstance);
        this.awaitState(InstanceState.START);
        this.isStarted = true;
    }

    public void stopInstances(){
        instances.forEach(this::stopInstance);
        this.awaitState(InstanceState.STOP);
    }

    private long initInstance(@NotNull Instance instance) {
        long dt = System.currentTimeMillis();
        instance.init();
        return System.currentTimeMillis() - dt;
    }

    private void startInstance(@NotNull Instance instance){
        instance.start();
    }

    private void stopInstance(@NotNull Instance instance){
       instance.stop();
    }

    /**
     * Send message to all teams
     * @param message Message to send
     */
    private void sendTeamMessage(@NotNull Component message){
        for(Team team : this.teams){
            team.sendMessage(message);
        }
    }

    /**
     * Send title to all teams
     * @param title Title to send
     * @param subtitle Subtitle to send
     */
    private void sendTeamTitle(@NotNull Component title, @NotNull Component subtitle){
        for(Team team : this.teams){
            team.sendTitle(title, subtitle);
        }
    }

    /**
     * Send action bar to all teams
     * @param actionBar Action bar to send
     */
    private void sendTeamActionBar(@NotNull Component actionBar){
        for(Team team : this.teams){
            team.sendActionBar(actionBar);
        }
    }

    /**
     * Play sound to all teams
     * @param sound Sound to play
     */
    @SuppressWarnings("SameParameterValue")
    private void playSound(@NotNull Sound sound){
        for(Team team : this.teams){
            team.playSound(sound);
        }
    }

    private List<List<Team>> getTeamsTuple(@NotNull List<Team> teams){
        List<List<Team>> teamsTuple = new ArrayList<>();
        if(teams.size() % 2 == 0){
            for(int i = 0; i < teams.size(); i += 2){
                List<Team> teamTuple = new ArrayList<>();
                teamTuple.add(teams.get(i));
                teamTuple.add(teams.get(i+1));
                teamsTuple.add(teamTuple);
            }
        }
        return teamsTuple;
    }

    private int getInstanceCount(){
        switch (this.gameType){
            case SOLO:
                int count = 0;
                for(Team team : this.teams){
                    count += team.getTeamSize();
                }
                return count;
            case ONLY_TEAM:
                return this.teams.size();
            case TEAM_VS_TEAM:
                if(this.teams.size() % 2 == 0) {
                    return this.teams.size() / 2;
                }
            default:
                return -1;
        }
    }

    private List<Team> getOnePlayerTeams(){
        List<Team> teams = new ArrayList<>();
        for(Team team : this.teams){
            for(APIPlayer player: team.getPlayers()){
                Team onePlayerTeam = new Team(team.getName(), team.getTeamCode(), player);
                teams.add(onePlayerTeam);
            }
        }
        return teams;
    }

    public APIWorld getLobbyWorld(){
        return this.lobby;
    }

    public APIWorld getGameWorld(){
        return this.game;
    }

    public boolean isStarted() {
        return this.isStarted;
    }

    protected void updateInstanceState(int instanceId, @NotNull InstanceState state){
        if(instancesState.containsKey(instanceId)){
            this.logger.info(String.format("Instance %d update state from %s to %s", instanceId, instancesState.get(instanceId), state));
            instancesState.replace(instanceId, state);
        } else {
            this.logger.info(String.format("Instance %d set state to %s", instanceId, state));
            instancesState.put(instanceId, state);
        }
    }

    private void awaitState(@NotNull InstanceState state){
        boolean isStateReached = false;
        while(!isStateReached){
            for(int instanceId : this.instancesState.keySet()){
                if(this.instancesState.get(instanceId) != state){
                    isStateReached = false;
                    break;
                }else{
                    isStateReached = true;
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if(e.getPlayer().getWorld().equals(this.game.getWorld())){
            if(!this.isStarted){
                if(e.getFrom().distance(e.getTo()) > 0){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        APIPlayer apiPlayer = new APIPlayer(player);
        if(this.isStarted){
            for(Instance instance : this.instances){
                if(instance.isPlayerOnInstance(apiPlayer)){
                    if(instance.isRunning()){
                        this.logger.info(String.format("Reconnecting player %s to instance %d...", apiPlayer.getName(), instance.getInstanceId()));
                        instance.onPlayerReconnect(apiPlayer);
                        this.logger.info(String.format("Player %s reconnected to instance %d...", apiPlayer.getName(), instance.getInstanceId()));
                    }else{
                        apiPlayer.teleport(this.getLobbyWorld().getSpawnPoint());
                        player.getInventory().clear();
                    }
                }
            }
        }else{
            apiPlayer.teleport(this.getLobbyWorld().getSpawnPoint());
            player.getInventory().clear();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        APIPlayer apiPlayer = new APIPlayer(e.getPlayer());
        if(this.isStarted){
            for(Instance instance : this.instances){
                if(instance.isPlayerOnInstance(apiPlayer)){
                    if(instance.isRunning()){
                        this.logger.info(String.format("Disconnecting player %s to instance %d...", apiPlayer.getName(), instance.getInstanceId()));
                        instance.onPlayerDisconnect(apiPlayer);
                        this.logger.info(String.format("Player %s disconnected to instance %d...", apiPlayer.getName(), instance.getInstanceId()));
                    }
                }
            }
        }
    }
}
