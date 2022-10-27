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

    public InstancesManager(JavaPlugin plugin, Class<? extends Instance> instanceClass, InstanceSettings settings, APIWorld lobby, APIWorld game) {
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

    public void start(List<Team> teams){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                startAsync(teams);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void startAsync(List<Team> teams) throws InvocationTargetException, InstantiationException, IllegalAccessException, InterruptedException {
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
            System.out.println("----------------------");
            System.out.println(TimeParsing.format(dt, "mm:ss:ms"));
            System.out.println(TimeParsing.format(dtAvg, "mm:ss:ms"));
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
    }

    private long initInstance(Instance instance) {
        long dt = System.currentTimeMillis();
        instance.init();
        return System.currentTimeMillis() - dt;
    }

    private void startInstance(Instance instance){
        instance.start();
    }

    private void stopInstance(Instance instance){
       instance.stop();
    }

    private void sendTeamMessage(String message){
        for(Team team : this.teams){
            team.sendMessage(message);
        }
    }

    private void sendTeamTitle(Component title, Component subtitle){
        for(Team team : this.teams){
            team.sendTitle(title, subtitle);
        }
    }

    private void sendTeamActionBar(Component actionBar){
        for(Team team : this.teams){
            team.sendActionBar(actionBar);
        }
    }

    private void playSound(Sound sound){
        for(Team team : this.teams){
            team.playSound(sound);
        }
    }

    private List<List<Team>> getTeamsTuple(List<Team> teams){
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

    protected void updateInstanceState(int instanceId, InstanceState state){
        if(instancesState.containsKey(instanceId)){
            this.logger.info(String.format("Instance %d update state from %s to %s", instanceId, instancesState.get(instanceId), state));
            instancesState.replace(instanceId, state);
        } else {
            this.logger.info(String.format("Instance %d set state to %s", instanceId, state));
            instancesState.put(instanceId, state);
        }
    }

    private void awaitState(InstanceState state){
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
        if(this.isStarted){
            for(Instance instance : this.instances){
                if(instance.isPlayerOnInstance(player)){
                    if(instance.isRunning()){
                        this.logger.info(String.format("Reconnecting player %s to instance %d...", player.getName(), instance.getInstanceId()));
                        instance.onPlayerReconnect(new APIPlayer(player));
                        this.logger.info(String.format("Player %s reconnected to instance %d...", player.getName(), instance.getInstanceId()));
                    }else{
                        player.teleport(this.getLobbyWorld().getSpawnPoint());
                        player.getInventory().clear();
                    }
                }
            }
        }else{
            player.teleport(this.getLobbyWorld().getSpawnPoint());
            player.getInventory().clear();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        if(this.isStarted){
            for(Instance instance : this.instances){
                if(instance.isPlayerOnInstance(player)){
                    if(instance.isRunning()){
                        this.logger.info(String.format("Disconnecting player %s to instance %d...", player.getName(), instance.getInstanceId()));
                        instance.onPlayerDisconnect(new APIPlayer(player));
                        this.logger.info(String.format("Player %s disconnected to instance %d...", player.getName(), instance.getInstanceId()));
                    }
                }
            }
        }
    }
}
