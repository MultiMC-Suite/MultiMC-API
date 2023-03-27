package fr.multimc.api.spigot.games;

import fr.multimc.api.commons.tools.formatters.times.TimeFormatter;
import fr.multimc.api.commons.tools.messages.MessagesFactory;
import fr.multimc.api.spigot.common.entities.player.MmcPlayer;
import fr.multimc.api.spigot.common.tools.dispatcher.DispatchAlgorithm;
import fr.multimc.api.spigot.common.tools.dispatcher.Dispatcher;
import fr.multimc.api.spigot.common.worlds.MmcWorld;
import fr.multimc.api.spigot.games.enums.GameState;
import fr.multimc.api.spigot.games.enums.ManagerState;
import fr.multimc.api.spigot.games.events.GamesManagerEvents;
import fr.multimc.api.spigot.games.settings.GameSettings;
import fr.multimc.api.spigot.games.settings.GamesManagerSettings;
import fr.multimc.api.spigot.games.teams.MmcTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class GamesManager {

    private final JavaPlugin plugin;
    private final Logger logger;
    private final GamesManagerSettings managerSettings;

    private final List<GameInstance> gameInstances = new ArrayList<>();
    private final HashMap<Integer, GameState> instancesState = new HashMap<>();
    private final List<MmcTeam> mmcTeams = new ArrayList<>();

    private ManagerState managerState = ManagerState.IDLE;
    private int allocations = 0;

    /**
     * Default constructor for the InstancesManager
     * @param plugin JavaPlugin instance
     */
    public GamesManager(@NotNull JavaPlugin plugin, GamesManagerSettings managerSettings) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.managerSettings = managerSettings;
        this.managerSettings.gameWorld().getWorldSettings().setGameMode(null); // Disable game mode changing for game world
        // Register local events handlers
        Bukkit.getPluginManager().registerEvents(new GamesManagerEvents(this, this.logger), plugin);
    }

    /**
     * Pre-allocate schematics for instances
     * @param allocateCount Number of instances to allocate
     */
    public void preAllocate(int allocateCount){
        this.logger.info(String.format("Allocating schematics for %d instances", allocateCount));
        for(int i = 0; i < allocateCount; i++){
            this.logger.info(String.format("Allocating for instance %d/%d", i + 1, allocateCount));
            Location location = new Location(this.managerSettings.gameWorld().getWorld(), i * 1024, 100, 0);
            GameInstance.allocate(this.managerSettings.gameSettings().gameSchematic(), location);
        }
        this.allocations = allocateCount;
        this.logger.info(String.format("%d slots allocated", allocateCount));
    }

    /**
     * Start the instances manager from sync to async
     * @param mmcTeams List of MmcTeam instances
     */
    public void start(@Nullable CommandSender sender, @NotNull List<MmcTeam> mmcTeams){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                boolean state = startAsync(mmcTeams);
                if(sender != null){
                    if(state){
                        sender.sendMessage(Component.text("Instance manager started", NamedTextColor.GREEN));
                    }else {
                        sender.sendMessage(Component.text("An error occurred when starting the instances manager").color(NamedTextColor.RED));
                    }
                }
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Start the instances manager asynchronously
     * @param mmcTeams List of MmcTeam instances
     */
    private boolean startAsync(@NotNull List<MmcTeam> mmcTeams) throws InvocationTargetException, InstantiationException, IllegalAccessException, InterruptedException {
        this.managerState = ManagerState.STARTING;
        // Reset instance manager
        this.gameInstances.clear();
        this.instancesState.clear();
        this.mmcTeams.clear();
        this.mmcTeams.addAll(mmcTeams);
        // Check if all teams have the minimum amount of players
        List<MmcTeam> unsuitableTeams = checkTeamSIze();
        if(unsuitableTeams.size() != 0){
            for(MmcTeam unsuitableTeam : unsuitableTeams){
                this.logger.warning(String.format("Team %s has not enough players (%d/%d)", unsuitableTeam.getName(), unsuitableTeam.getTeamSize(), this.managerSettings.gameSettings().minPlayers()));
            }
            return false;
        }
        // Send warning for each player that is not online
        this.mmcTeams.stream().flatMap(mmcTeam -> mmcTeam.getPlayers().stream())
                .filter(mmcPlayer -> !mmcPlayer.isOnline())
                .forEach(mmcPlayer -> this.logger.warning("Player %s is not online".formatted(mmcPlayer.getName())));
        // Dispatch teams
        List<List<MmcTeam>> gameTeams = new ArrayList<>();
        switch (this.managerSettings.gameSettings().gameType()) {
            case SOLO -> this.getOnePlayerTeams().forEach(team -> gameTeams.add(Collections.singletonList(team)));
            case ONLY_TEAM -> mmcTeams.forEach(team -> gameTeams.add(Collections.singletonList(team)));
            case TEAM_VS_TEAM -> gameTeams.addAll(this.getTeamsTuple(this.mmcTeams));
            case FFA -> gameTeams.add(this.mmcTeams);
        }
        // Create instances
        for(int i = 0; i < gameTeams.size(); i++){
            this.logger.info(String.format("Creating instance %d/%d", i + 1, gameTeams.size()));
            Location location = new Location(this.managerSettings.gameWorld().getWorld(), i * 1024, 100, 0);
            this.gameInstances.add((GameInstance) this.managerSettings.instanceClass().getConstructors()[0].newInstance(this.plugin, this, this.managerSettings.gameSettings(), location, gameTeams.get(i), i));
        }
        if(this.gameInstances.size() == 0){
            this.logger.severe("No instance created");
            return false;
        }
        // Init instances
        this.initInstances();
        // Get and teleport spectators in their instances
        Map<MmcPlayer, Integer> spectators = new HashMap<>(new Dispatcher(DispatchAlgorithm.RANDOM).dispatch(this.getSpectatorList(), IntStream.rangeClosed(0, this.gameInstances.size() - 1).boxed().collect(Collectors.toList())));
        for(GameInstance gameInstance : this.gameInstances){
            for(MmcPlayer spectator : spectators.keySet()){
                if(spectators.get(spectator) == gameInstance.getInstanceId()){
                    spectator.setGameModeSync(this.plugin, GameMode.SPECTATOR);
                    spectator.teleportSync(this.plugin, gameInstance.getInstanceLocation());
                    gameInstance.onSpectatorJoin(spectator);
                }
            }
        }
        // Count down
        this.countDown();
        // Start instances
        this.startInstances();
        this.managerState = ManagerState.STARTED;
        this.allocations = 0; // Reset allocated slots for next start
        this.logger.info("Instances manager started");
        return true;
    }

    /**
     * Init all instances
     */
    @SuppressWarnings("BusyWait")
    private void initInstances() throws InterruptedException {
        long dt;
        long dtAvg = 0;
        for(int i = 0; i < this.gameInstances.size(); i++){
            // Runnable
            long finalDtAvg = dtAvg;
            String timeFormat = TimeFormatter.format(finalDtAvg * (this.gameInstances.size() - i - 1), "mm:ss");
            Component actionBarComponent = Component.text("Instance ").color(NamedTextColor.AQUA)
                    .append(Component.text("%d/%d".formatted(i + 1, this.gameInstances.size())).color(NamedTextColor.GOLD))
                    .append(Component.text(" initialized (").color(NamedTextColor.AQUA))
                    .append(Component.text(timeFormat).color(NamedTextColor.YELLOW))
                    .append(Component.text(" remaining)").color(NamedTextColor.DARK_AQUA));
            int taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () ->
                    this.sendEveryoneActionBar(actionBarComponent),
                    0L, 20L);
            // Instance loading
            dt = this.initInstance(this.gameInstances.get(i), this.gameInstances.get(i).getInstanceId());
            Thread.sleep(1000);
            if(dtAvg == 0){
                dtAvg = dt;
            }
            dtAvg = (dtAvg + dt) / 2;
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }

    private void countDown() throws InterruptedException {
        for(int i = 5; i >= 0; i--){
            Thread.sleep(1000);
            Component titleComponent;
            Component subTitleComponent;
            if (i != 0){
                titleComponent = Component.text(i).color(NamedTextColor.GOLD);
                subTitleComponent = Component.text("Get ready...").color(NamedTextColor.GRAY);
            }else{
                titleComponent = Component.text("Let's").color(NamedTextColor.WHITE)
                        .append(Component.text(" GO!").color(NamedTextColor.AQUA));
                subTitleComponent = null;
            }
            this.sendEveryoneTitle(titleComponent, subTitleComponent);
            this.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
        }
    }
    /**
     * Start all instances
     */
    private void startInstances(){
        this.gameInstances.forEach(this::startInstance);
    }

    /**
     * Stop all instances
     */
    private void stopInstances(){
        for(GameInstance gameInstance: this.gameInstances)
            if(gameInstance.getInstanceState() != GameState.PRE_STOP && gameInstance.getInstanceState() != GameState.STOP)
                gameInstance.stop();
    }

    /**
     * Init an instance
     * @param gameInstance Instance to init
     * @param instanceId ID of the instance
     * @return Time taken to init the instance
     */
    private long initInstance(@NotNull GameInstance gameInstance, int instanceId) {
        long dt = System.currentTimeMillis();
        gameInstance.init(instanceId < this.allocations);
        return System.currentTimeMillis() - dt;
    }

    /**
     * Start an instance
     * @param gameInstance Instance to start
     */
    private void startInstance(@NotNull GameInstance gameInstance){
        gameInstance.start();
    }

    /**
     * Stop an instance
     * @param gameInstance Instance to stop
     */
    private void stopInstance(@NotNull GameInstance gameInstance){
       gameInstance.stop();
    }

    /**
     * Called by instance to update their state, and if all are stopped, reset the instances manager
     * @param instanceId ID if the instance
     * @param state New state for the instance
     */
    public void updateInstanceState(int instanceId, @NotNull GameState state){
        if(instancesState.containsKey(instanceId)){
            this.logger.info(String.format("Instance %d update state from %s to %s", instanceId, instancesState.get(instanceId), state));
            instancesState.replace(instanceId, state);
        } else {
            this.logger.info(String.format("Instance %d set state to %s", instanceId, state));
            instancesState.put(instanceId, state);
        }
        // If instance is stopped, make them spectator (on another instance if available)
        if(state == GameState.STOP){
            GameInstance gameInstance = this.getInstanceFromId(instanceId);
            List<GameInstance> runningInstances = this.gameInstances.stream().filter(instance -> instance.getInstanceState() != GameState.STOP && instance.getInstanceState() != GameState.PRE_STOP).toList();
            if(runningInstances.size() != 0){
                for(MmcPlayer spectator: gameInstance.getSpectators()){
                    spectator.setGameModeSync(this.plugin, GameMode.SPECTATOR);
                    GameInstance randomInstance = runningInstances.get(new Random().nextInt(runningInstances.size()));
                    gameInstance.onSpectatorLeave(spectator);
                    randomInstance.onSpectatorJoin(spectator);
                }
                for(MmcPlayer player: gameInstance.getPlayers()){
                    player.setGameModeSync(this.plugin, GameMode.SPECTATOR);
                    GameInstance randomInstance = runningInstances.get(new Random().nextInt(runningInstances.size()));
                    randomInstance.onSpectatorJoin(player);
                }
            }else{
                for(MmcPlayer spectator: new ArrayList<>(gameInstance.getSpectators())){
                    gameInstance.onSpectatorLeave(spectator);
                }
            }
        }
        // Check if all instances are stopped
        boolean isAllStopped = true;
        for(int _instanceId: this.instancesState.keySet()){
            if(this.instancesState.get(_instanceId) != GameState.STOP){
                isAllStopped = false;
                break;
            }
        }
        if(isAllStopped && this.managerState != ManagerState.STOPPING && this.managerState != ManagerState.STOPPED){
            this.stopManager();
        }
    }

    /**
     * Reset the instances manager
     */
    public void stopManager(){
        this.managerState = ManagerState.STOPPING;
        this.stopInstances();
        // Teleport all spectators and players to the lobby
        for(Player player: this.getLobbyWorld().getWorld().getPlayers()){
            MmcPlayer mmcPlayer = new MmcPlayer(player);
            mmcPlayer.teleportSync(this.plugin, this.managerSettings.lobbyWorld().getSpawnPoint());
            mmcPlayer.setGameModeSync(this.plugin, GameMode.SURVIVAL);
        }
        for(Player player: this.getGameWorld().getWorld().getPlayers()){
            MmcPlayer mmcPlayer = new MmcPlayer(player);
            mmcPlayer.teleportSync(this.plugin, this.managerSettings.lobbyWorld().getSpawnPoint());
            mmcPlayer.setGameModeSync(this.plugin, GameMode.SURVIVAL);
        }
        this.managerState = ManagerState.STOPPED;
    }

    /**
     * Send message to all teams
     * @param message Message to send
     */
    private void broadcast(@NotNull Component message) {
        mmcTeams.forEach(team -> team.sendMessage(message));
    }

    /**
     * Send title to all teams
     * @param title Title to send
     * @param subtitle Subtitle to send
     */
    private void sendEveryoneTitle(@Nullable Component title, @Nullable Component subtitle) {
        List<Player> players = new ArrayList<>();
        players.addAll(this.getLobbyWorld().getWorld().getPlayers());
        players.addAll(this.getGameWorld().getWorld().getPlayers());
        for(Player player: players){
            MmcPlayer mmcPlayer = new MmcPlayer(player);
            mmcPlayer.sendTitle(title, subtitle);
        }
    }

    /**
     * Send action bar to all teams
     * @param actionBar Action bar to send
     */
    private void sendEveryoneActionBar(@NotNull Component actionBar) {
        List<Player> players = new ArrayList<>();
        players.addAll(this.getLobbyWorld().getWorld().getPlayers());
        players.addAll(this.getGameWorld().getWorld().getPlayers());
        for(Player player: players){
            MmcPlayer mmcPlayer = new MmcPlayer(player);
            mmcPlayer.sendActionBar(actionBar);
        }
    }

    /**
     * Play sound to all teams
     * @param sound Sound to play
     */
    @SuppressWarnings("SameParameterValue")
    private void playSound(@NotNull Sound sound) {
        mmcTeams.forEach(mmcTeam -> mmcTeam.playSound(sound));
    }

    /**
     * Get teams of 1 player (for solo game type)
     * @return List of teams with only 1 player into
     */
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

    /**
     * For Team VS Team game type, get tuple of teams
     * @param mmcTeams List of teams
     * @return List of tuple of teams
     */
    private List<List<MmcTeam>> getTeamsTuple(@NotNull List<MmcTeam> mmcTeams) {
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

    /**
     * Get the list of spectators
     * @return List of spectators
     */
    private List<MmcPlayer> getSpectatorList() {
        // Get all players on an instance
        List<MmcPlayer> instancePlayers = new ArrayList<>();
        mmcTeams.forEach(mmcTeam -> instancePlayers.addAll(mmcTeam.getPlayers()));
        List<MmcPlayer> spectators = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()){
            if(instancePlayers.contains(new MmcPlayer(player))) continue;
            if(player.getWorld().equals(this.managerSettings.lobbyWorld().getWorld()) || player.getWorld().equals(this.managerSettings.gameWorld().getWorld())){
                spectators.add(new MmcPlayer(player));
            }
        }
        return spectators;
    }

    /**
     * Get instance count from the game type and the number of teams/players
     * @return Instance count to create
     */
    private int getInstanceCount(){
        switch (this.managerSettings.gameSettings().gameType()){
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

    /**
     * Check if all the teams have the minimum number of players
     * @return List of teams with not enough players
     */
    private List<MmcTeam> checkTeamSIze(){
        return this.mmcTeams.stream().filter(mmcTeam -> mmcTeam.getTeamSize() < this.managerSettings.gameSettings().minPlayers()).collect(Collectors.toList());
    }

    /**
     * Get a team from a MmcPlayer object
     * @param mmcPlayer MmcPlayer object
     * @return Team of the player
     */
    @Nullable
    public MmcTeam getTeamFromPlayer(@NotNull MmcPlayer mmcPlayer){
        return this.mmcTeams.stream().filter(mmcTeam -> mmcTeam.getPlayers().contains(mmcPlayer)).findFirst().orElse(null);
    }

    /**
     * Get an instance from a MmcPlayer object
     * @param mmcPlayer MmcPlayer object
     * @return Instance of the player
     */
    @Nullable
    public GameInstance getInstanceFromPlayer(@NotNull MmcPlayer mmcPlayer){
        return this.gameInstances.stream().filter(gameInstance -> gameInstance.getPlayers().contains(mmcPlayer)).findFirst().orElse(null);
    }

    @Nullable
    public GameInstance getInstanceFromSpectator(@NotNull MmcPlayer mmcPlayer){
        return this.gameInstances.stream().filter(gameInstance -> gameInstance.getSpectators().contains(mmcPlayer)).findFirst().orElse(null);
    }

    /**
     * Get an instance from its id
     * @param instanceId Target id
     * @return Instance object
     */
    private GameInstance getInstanceFromId(int instanceId){
        return this.gameInstances.stream().filter(gameInstance -> gameInstance.getInstanceId() == instanceId).findFirst().orElse(this.gameInstances.get(0));
    }

    public void addSpectator(@NotNull MmcPlayer spectator, int instanceId){
        GameInstance gameInstance = this.getInstanceFromId(instanceId);
        if(gameInstance == null) return;
        spectator.setGameModeSync(this.plugin, GameMode.SPECTATOR);
        spectator.teleportSync(this.plugin, gameInstance.getInstanceLocation());
        gameInstance.onSpectatorJoin(spectator);
    }

    public void removeSpectator(@NotNull MmcPlayer spectator){
        GameInstance gameInstance = this.getInstanceFromSpectator(spectator);
        if(gameInstance == null) return;
        gameInstance.onSpectatorLeave(spectator);
    }

    public boolean isSpectator(@NotNull MmcPlayer player){
        return this.gameInstances.stream().anyMatch(gameInstance -> gameInstance.getSpectators().contains(player));
    }
    public MmcWorld getLobbyWorld(){
        return this.managerSettings.lobbyWorld();
    }
    public MmcWorld getGameWorld(){
        return this.managerSettings.gameWorld();
    }
    public ManagerState getState() {
        return this.managerState;
    }

    public List<GameInstance> getInstances() {
        return gameInstances;
    }

    public MessagesFactory getMessageFactory() {
        return this.managerSettings.messagesFactory();
    }
    
    public GameSettings getGameSettings() {
        return this.managerSettings.gameSettings();
    }
}
