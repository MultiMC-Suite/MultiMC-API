package fr.multimc.api.spigot.games;

import com.sk89q.worldedit.WorldEditException;
import fr.multimc.api.spigot.entities.MmcEntity;
import fr.multimc.api.spigot.entities.player.MmcPlayer;
import fr.multimc.api.spigot.games.enums.GameState;
import fr.multimc.api.spigot.games.settings.GameSettings;
import fr.multimc.api.spigot.managers.GamesManager;
import fr.multimc.api.spigot.teams.MmcTeam;
import fr.multimc.api.spigot.tools.dispatcher.DispatchAlgorithm;
import fr.multimc.api.spigot.tools.dispatcher.Dispatcher;
import fr.multimc.api.spigot.worlds.locations.RelativeLocation;
import fr.multimc.api.spigot.worlds.schematics.Schematic;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public class GameInstance implements Listener {

    private final Plugin plugin;
    private final Logger logger;
    private final GamesManager gamesManager;
    private final GameSettings gameSettings;
    private final Location instanceLocation;
    private final int instanceId;

    private GameState gameState;

    private final List<MmcTeam> mmcTeams;
    private final List<MmcPlayer> players;
    private final Map<UUID, Location> playerSpawns;

    private final List<MmcPlayer> spectators;

    private final List<Entity> instanceEntities;

    private final Map<Long, GameState> instanceStateUpdates = new HashMap<>();

    private boolean isRunning = false;
    private BukkitTask task;
    private int taskId;

    private int remainingTime;

    public GameInstance(@NotNull JavaPlugin plugin, @NotNull GamesManager gamesManager, @NotNull GameSettings settings, @NotNull Location instanceLocation, @NotNull List<MmcTeam> mmcTeams, int instanceId) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.gamesManager = gamesManager;
        this.updateState(GameState.PRE_CREATE);
        this.gameSettings = settings;
        this.instanceLocation = instanceLocation;
        this.mmcTeams = new ArrayList<>(mmcTeams);
        this.instanceId = instanceId;
        this.instanceEntities = new ArrayList<>();
        this.remainingTime = this.gameSettings.duration();
        this.players = this.getInstancePlayers();
        this.playerSpawns = this.getPlayerSpawnsList();
        this.spectators = new ArrayList<>();
        this.updateState(GameState.CREATE);
    }

    // GAME ACTIONS

    /**
     * Past the gameSchematic for the instance (can be called for pre-allocation too)
     * @param schematic Schematic to paste
     * @param location Location to paste the gameSchematic
     */
    public static void allocate(@NotNull Schematic schematic, @NotNull Location location) {
        schematic.getOptions().setLocation(location);
        GameInstance.pasteSchematic(schematic);
    }

    /**
     * Initialize game instance
     * @param isPreAllocated If the gameSchematic is already pasted
     */
    public void init(boolean isPreAllocated){
        if(this.gameState == GameState.PRE_INIT || this.gameState == GameState.INIT) throw new RuntimeException("Game instance is already initialized or initializing");
        // Paste gameSchematic
        if(!isPreAllocated){
            this.updateState(GameState.PRE_ALLOCATE);
            GameInstance.allocate(gameSettings.gameSchematic(), instanceLocation);
            this.updateState(GameState.ALLOCATE);
        }
        this.updateState(GameState.PRE_INIT);
        // Teleport players
        for(UUID uuid: this.playerSpawns.keySet()){
            MmcPlayer mmcPlayer = this.players.stream().filter(p -> p.getUUID().equals(uuid)).findFirst().orElse(null);
            if(mmcPlayer != null){
                this.teleportPlayer(mmcPlayer, this.playerSpawns.get(uuid));
                this.setPlayerSpawn(mmcPlayer, this.playerSpawns.get(uuid));
                mmcPlayer.setGameModeSync(this.plugin, GameMode.SURVIVAL);
            }
        }
        // Spawn entities
        for(MmcEntity entity : gameSettings.entities()){
            instanceEntities.add(entity.spawn(instanceLocation, this.instanceId));
        }
        this.updateState(GameState.INIT);
    }

    /**
     * Start game instance
     */
    public void start(){
        if(this.gameState == GameState.PRE_START || this.gameState == GameState.START) throw new RuntimeException("Game instance is already started or starting");
        this.updateState(GameState.PRE_START);
        for(MmcPlayer player : this.players){
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);
        }
        if(taskId != 0)
            Bukkit.getScheduler().cancelTask(taskId);
        this.taskId = this.run();
        this.updateState(GameState.START);
    }

    /**
     * Default instance stop (players will return to the lobby)
     */
    public void stop(){
        if(this.gameState == GameState.PRE_STOP || this.gameState == GameState.STOP) throw new RuntimeException("Game instance is already stopped or stopping");
        this.updateState(GameState.PRE_STOP);
        this.isRunning = false;
        if(!this.isCancelled()) this.cancel();
        this.updateState(GameState.STOP);
    }

    /**
     * Restart game instance
     */
    public void restart(){
        // Stop BukkitRunnable
        this.isRunning = false;
        this.cancel();
        // Reset instance
        this.resetInstance();
        // Restart instance
        this.init(false);
        this.start();
    }

    /**
     * Reset game instance before restarting
     */
    private void resetInstance(){
        // Clear entities
        this.instanceEntities.forEach(Entity::remove);
        this.instanceEntities.clear();
        // Reset time
        this.remainingTime = this.gameSettings.duration();
    }

    /**
     * Method called every tick delay defined in InstanceSettings
     */
    public void tick(){}

    /**
     * Main game loop, run async when instance is started
     */
    @SuppressWarnings("BusyWait")
    private int run(){
        this.task = new BukkitRunnable(){
            @Override
            public void run() {
                isRunning = true;
                double deltaTick = 0.05 * gameSettings.tickDelay();
                long lastTickTime;
                long lastSecondTime;
                long nextTickTime = (long) (System.currentTimeMillis() + deltaTick * 1000L);
                long nextSecondTime = System.currentTimeMillis() + 1000L;
                while(isRunning && remainingTime >= 0){
                    if(System.currentTimeMillis() >= nextTickTime){
                        tick();
                        lastTickTime = nextTickTime;
                        nextTickTime = (long) (lastTickTime + deltaTick * 1000L);
                    }
                    if(System.currentTimeMillis() >= nextSecondTime){
                        remainingTime--;
                        lastSecondTime = nextSecondTime;
                        nextSecondTime = lastSecondTime + 1000L;
                    }
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if(remainingTime < 0){
                    stop();
                }
            }
        }.runTaskAsynchronously(this.plugin);
        return this.task.getTaskId();
    }

    public void cancel() throws IllegalStateException {
        this.isRunning = false;
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public boolean isCancelled(){
        return this.task.isCancelled();
    }

    /**
     * Asynchronously teleport a player to a location
     * @param mmcPlayer Player to teleport
     * @param location Target location
     */
    public void teleportPlayer(@NotNull MmcPlayer mmcPlayer, @Nullable Location location){
        if(location != null){
            mmcPlayer.teleportSync(this.plugin, location);
            Bukkit.getScheduler().runTask(this.plugin, () -> mmcPlayer.teleport(location));
        }
    }

    /**
     * Teleport all players to their spawn
     */
    public void teleportPlayersToSpawn(){
        this.getPlayers().forEach(this::teleportPlayerToSpawn);
    }

    /**
     * Teleport a player to his spawn
     * @param player {@link MmcPlayer} to teleport
     */
    public void teleportPlayerToSpawn(@NotNull MmcPlayer player){
        this.getPlayers().stream().filter(p -> p.getUUID().equals(player.getUUID())).findFirst().ifPresent(mmcPlayer -> this.teleportPlayer(mmcPlayer, this.getPlayerSpawns().get(player.getUUID())));
    }

    /**
     * Asynchronously set a player spawn
     * @param mmcPlayer Player to set spawn
     * @param location Target location
     */
    public void setPlayerSpawn(@NotNull MmcPlayer mmcPlayer, @Nullable Location location){
        if(location != null){
            Bukkit.getScheduler().runTask(this.plugin, () -> mmcPlayer.setSpawnPoint(location));
        }
    }

    /**
     * Broadcast a message to all players in the instance
     * @param message Message to broadcast
     */
    public void broadcast(@Nonnull Component message) {
        this.getPlayers().forEach(player -> player.sendMessage(message));
    }

    /**
     * Called to reconnect a disconnected player
     * @param mmcPlayer Player to reconnect
     */
    public void onPlayerReconnect(@NotNull MmcPlayer mmcPlayer){
        // If instance is running, teleport player into it
        if(this.isRunning){
            this.teleportPlayer(mmcPlayer, this.playerSpawns.get(mmcPlayer.getUUID()));
            mmcPlayer.setGameModeSync(this.plugin, GameMode.SURVIVAL);
        }
    }

    /**
     * Called when a player disconnect from the server
     */
    public void onPlayerDisconnect(@NotNull MmcPlayer mmcPlayer){}

    /**
     * Called to update instance state for InstanceManager
     * @param state New InstanceState
     */
    protected void updateState(@NotNull GameState state){
        this.instanceStateUpdates.put(System.currentTimeMillis(), state);
        this.gameState = state;
        this.gamesManager.updateInstanceState(this.instanceId, state);
    }

    /**
     * Called when a spectator spectate this game
     * @param spectator MmcPlayer instance of the spectator
     */
    public void onSpectatorJoin(@NotNull MmcPlayer spectator){
        this.spectators.add(spectator);
        logger.warning("Spectator " + spectator.getName() + " joined game " + this.instanceId);
    }

    /**
     * Called when a spectator stop spectating this game
     * @param spectator MmcPlayer instance of the spectator
     */
    public void onSpectatorLeave(@NotNull MmcPlayer spectator){
        this.spectators.remove(spectator);
        logger.warning("Spectator " + spectator.getName() + " left game " + this.instanceId);
    }

    /**
     * Paste instance gameSchematic
     * @param schematic Schematic object
     */
    private static void pasteSchematic(@NotNull Schematic schematic){
        try {
            schematic.paste();
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    // PRIVATE GETTERS
    /**
     * Return a map with all players spawns locations
     * @return Hashmap with as key a Player and as value a Location
     */
    private Map<UUID, Location> getPlayerSpawnsList(){
        // TODO: need tests
        Map<UUID, Location> playerSpawns = new HashMap<>();
        switch(this.gameSettings.gameType()) {
            case SOLO -> playerSpawns.put(this.mmcTeams.get(0).getPlayers().get(0).getUUID(), this.getSpawnPoints().get(0));
            case ONLY_TEAM -> {
                List<Location> locations = this.getSpawnPoints();
                List<UUID> playersUUID = this.mmcTeams.get(0).getPlayers().stream().map(MmcPlayer::getUUID).toList();
                Map<UUID, Location> spawns = new Dispatcher(DispatchAlgorithm.ROUND_ROBIN).dispatch(playersUUID, locations);
                if(Objects.nonNull(spawns))
                    playerSpawns.putAll(spawns);
            }
            case TEAM_VS_TEAM -> {
                int spawnPointsCount = this.getSpawnPoints().size() % 2 == 0 ? this.getSpawnPoints().size() / 2 : ((this.getSpawnPoints().size() - 1) / 2);
                List<Location> team1Locations = this.getSpawnPoints().subList(0, spawnPointsCount);
                List<Location> team2Locations = this.getSpawnPoints().subList(spawnPointsCount, this.getSpawnPoints().size());
                List<UUID> team1UUIDs = this.mmcTeams.get(0).getPlayers().stream().map(MmcPlayer::getUUID).toList();
                List<UUID> team2UUIDs = this.mmcTeams.get(1).getPlayers().stream().map(MmcPlayer::getUUID).toList();
                Map<UUID, Location> team1Spawns = new Dispatcher(DispatchAlgorithm.ROUND_ROBIN).dispatch(team1UUIDs, team1Locations);
                Map<UUID, Location> team2Spawns = new Dispatcher(DispatchAlgorithm.ROUND_ROBIN).dispatch(team2UUIDs, team2Locations);
                if(Objects.nonNull(team1Spawns) && Objects.nonNull(team2Spawns)){
                    playerSpawns.putAll(team1Spawns);
                    playerSpawns.putAll(team2Spawns);
                }
            }
            case FFA -> {
                List<Location> locations = this.getSpawnPoints();
                List<UUID> playersUUID = new ArrayList<>();
                for(MmcTeam team : this.mmcTeams){
                    playersUUID.addAll(team.getPlayers().stream().map(MmcPlayer::getUUID).toList());
                }
                Map<UUID, Location> spawns = new Dispatcher(DispatchAlgorithm.ROUND_ROBIN).dispatch(playersUUID, locations);
                if(Objects.nonNull(spawns))
                    playerSpawns.putAll(spawns);
            }
        }
        return playerSpawns;
    }

    /**
     * Get all players in this instance
     * @return List of players
     */
    private List<MmcPlayer> getInstancePlayers(){
        List<MmcPlayer> localPlayers = new ArrayList<>();
        for(MmcTeam mmcTeam : this.getTeams()){
            localPlayers.addAll(mmcTeam.getPlayers());
        }
        return localPlayers;
    }

    /**
     * Get all spawn Locations
     * @return List of locations
     */
    private List<Location> getSpawnPoints(){
        List<Location> spawnPoints = new ArrayList<>();
        for(RelativeLocation customLocation : this.gameSettings.spawnPoints()){
            spawnPoints.add(customLocation.toAbsolute(this.instanceLocation));
        }
        return spawnPoints;
    }

    /**
     * Get game player count
     * @return Number of player on this instance
     */
    private int getPlayerCount(){
        return this.players.size();
    }

    /**
     * Check if the player is on this instance
     * @param mmcPlayer Target player
     * @return True if the player is on this instance
     */
    public boolean isPlayerOnInstance(@NotNull MmcPlayer mmcPlayer){
        return this.players.stream().anyMatch(player -> player.equals(mmcPlayer));
    }

    /**
     * Check if the spectator is on this instance
     * @param spectator Target spectator
     * @return True if the spectator is on this instance
     */
    public boolean isSpectatorOnInstance(@NotNull MmcPlayer spectator){
        return this.spectators.stream().anyMatch(player -> player.equals(spectator));
    }

    // PUBLIC GETTERS
    public Plugin getPlugin() {
        return plugin;
    }
    public List<MmcPlayer> getSpectators() {
        return spectators;
    }
    public Logger getLogger() {
        return logger;
    }
    public GameSettings getInstanceSettings() {
        return gameSettings;
    }
    public Location getInstanceLocation() {
        return instanceLocation;
    }
    public List<MmcTeam> getTeams() {
        return mmcTeams;
    }
    public int getInstanceId() {
        return instanceId;
    }
    public List<Entity> getInstanceEntities() {
        return instanceEntities;
    }
    public int getRemainingTime() {
        return remainingTime;
    }
    public boolean isRunning() {
        return isRunning;
    }
    public List<MmcPlayer> getPlayers() {
        return players;
    }
    public GameState getInstanceState() {
        return gameState;
    }
    public Map<UUID, Location> getPlayerSpawns() {
        return playerSpawns;
    }
    public Map<Long, GameState> getInstanceStateUpdates() {
        return instanceStateUpdates;
    }

    // PUBLIC SETTERS
    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }
}
