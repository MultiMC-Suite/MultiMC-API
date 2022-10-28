package fr.multimc.api.spigot.managers.games.instances;

import fr.multimc.api.commons.tools.times.MmcTime;
import fr.multimc.api.spigot.managers.games.GameType;
import fr.multimc.api.spigot.tools.entities.player.MmcPlayer;
import fr.multimc.api.spigot.managers.teams.MmcTeam;
import fr.multimc.api.spigot.tools.worlds.MmcWorld;
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
    private List<MmcTeam> mmcTeams = new ArrayList<>();
    private final HashMap<Integer, InstanceState> instancesState = new HashMap<>();
    private final GameType gameType;
    private final Class<? extends Instance> instanceClass;
    private final InstanceSettings settings;
    private final Logger logger;
    private boolean isStarted = false;


    private final MmcWorld lobby;
    private final MmcWorld game;

    public InstancesManager(@NotNull JavaPlugin plugin,
                            @NotNull Class<? extends Instance> instanceClass,
                            @NotNull InstanceSettings settings,
                            @NotNull MmcWorld lobby,
                            @NotNull MmcWorld game) {
        this.plugin = plugin;
        this.instances = new ArrayList<>();
        this.gameType = settings.gameType();
        this.instanceClass = instanceClass;
        this.settings = settings;
        this.logger = plugin.getLogger();
        this.lobby = lobby;
        this.game = game;
        // Register local events handlers
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void start(@NotNull List<MmcTeam> mmcTeams){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                startAsync(mmcTeams);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void startAsync(@NotNull List<MmcTeam> mmcTeams) throws InvocationTargetException, InstantiationException, IllegalAccessException, InterruptedException {
        this.instances.clear();
        this.mmcTeams = new ArrayList<>(mmcTeams);
        List<List<MmcTeam>> gameTeams = new ArrayList<>();
        switch (this.gameType) {
            case SOLO -> {
                List<MmcTeam> localTeams1 = this.getOnePlayerTeams();
                gameTeams.add(localTeams1);
            }
            case ONLY_TEAM -> {
                List<MmcTeam> localTeams2 = new ArrayList<>(mmcTeams);
                gameTeams.add(localTeams2);
            }
            case TEAM_VS_TEAM -> gameTeams = this.getTeamsTuple(this.mmcTeams);
        }
        // Create instances
        for(int i = 0; i < this.getInstanceCount(); i++){
            this.logger.info(String.format("Creating instance %d/%d", i + 1, this.getInstanceCount()));
            Location location = new Location(this.game.getWorld(), i * 1000, 100, 0);
            switch(this.gameType) {
                case SOLO -> {
                    List<MmcTeam> instanceMmcTeams = new ArrayList<>();
                    instanceMmcTeams.add(gameTeams.get(0).get(i));
                    this.instances.add((Instance) this.instanceClass.getConstructors()[0].newInstance(this.plugin, this, i, this.settings, location, instanceMmcTeams));
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
                                    MmcTime.format(dtAvg * (this.instances.size() - i - 1), "mm:ss"))));
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
        for(MmcTeam mmcTeam : this.mmcTeams){
            mmcTeam.sendMessage(message);
        }
    }

    /**
     * Send title to all teams
     * @param title Title to send
     * @param subtitle Subtitle to send
     */
    private void sendTeamTitle(@NotNull Component title, @NotNull Component subtitle){
        for(MmcTeam mmcTeam : this.mmcTeams){
            mmcTeam.sendTitle(title, subtitle);
        }
    }

    /**
     * Send action bar to all teams
     * @param actionBar Action bar to send
     */
    private void sendTeamActionBar(@NotNull Component actionBar){
        for(MmcTeam mmcTeam : this.mmcTeams){
            mmcTeam.sendActionBar(actionBar);
        }
    }

    /**
     * Play sound to all teams
     * @param sound Sound to play
     */
    @SuppressWarnings("SameParameterValue")
    private void playSound(@NotNull Sound sound){
        for(MmcTeam mmcTeam : this.mmcTeams){
            mmcTeam.playSound(sound);
        }
    }

    private List<List<MmcTeam>> getTeamsTuple(@NotNull List<MmcTeam> mmcTeams){
        List<List<MmcTeam>> teamsTuple = new ArrayList<>();
        if(mmcTeams.size() % 2 == 0){
            for(int i = 0; i < mmcTeams.size(); i += 2){
                List<MmcTeam> mmcTeamTuple = new ArrayList<>();
                mmcTeamTuple.add(mmcTeams.get(i));
                mmcTeamTuple.add(mmcTeams.get(i+1));
                teamsTuple.add(mmcTeamTuple);
            }
        }
        return teamsTuple;
    }

    private int getInstanceCount(){
        switch (this.gameType){
            case SOLO:
                int count = 0;
                for(MmcTeam mmcTeam : this.mmcTeams){
                    count += mmcTeam.getTeamSize();
                }
                return count;
            case ONLY_TEAM:
                return this.mmcTeams.size();
            case TEAM_VS_TEAM:
                if(this.mmcTeams.size() % 2 == 0) {
                    return this.mmcTeams.size() / 2;
                }
            default:
                return -1;
        }
    }

    private List<MmcTeam> getOnePlayerTeams(){
        List<MmcTeam> mmcTeams = new ArrayList<>();
        for(MmcTeam mmcTeam : this.mmcTeams){
            for(MmcPlayer player: mmcTeam.getPlayers()){
                MmcTeam onePlayerMmcTeam = new MmcTeam(mmcTeam.getName(), mmcTeam.getTeamCode(), player);
                mmcTeams.add(onePlayerMmcTeam);
            }
        }
        return mmcTeams;
    }

    public MmcWorld getLobbyWorld(){
        return this.lobby;
    }

    public MmcWorld getGameWorld(){
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
        MmcPlayer mmcPlayer = new MmcPlayer(player);
        if(this.isStarted){
            for(Instance instance : this.instances){
                if(instance.isPlayerOnInstance(mmcPlayer)){
                    if(instance.isRunning()){
                        this.logger.info(String.format("Reconnecting player %s to instance %d...", mmcPlayer.getName(), instance.getInstanceId()));
                        instance.onPlayerReconnect(mmcPlayer);
                        this.logger.info(String.format("Player %s reconnected to instance %d...", mmcPlayer.getName(), instance.getInstanceId()));
                    }else{
                        mmcPlayer.teleport(this.getLobbyWorld().getSpawnPoint());
                        player.getInventory().clear();
                    }
                }
            }
        }else{
            mmcPlayer.teleport(this.getLobbyWorld().getSpawnPoint());
            player.getInventory().clear();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        MmcPlayer mmcPlayer = new MmcPlayer(e.getPlayer());
        if(this.isStarted){
            for(Instance instance : this.instances){
                if(instance.isPlayerOnInstance(mmcPlayer)){
                    if(instance.isRunning()){
                        this.logger.info(String.format("Disconnecting player %s to instance %d...", mmcPlayer.getName(), instance.getInstanceId()));
                        instance.onPlayerDisconnect(mmcPlayer);
                        this.logger.info(String.format("Player %s disconnected to instance %d...", mmcPlayer.getName(), instance.getInstanceId()));
                    }
                }
            }
        }
    }
}
