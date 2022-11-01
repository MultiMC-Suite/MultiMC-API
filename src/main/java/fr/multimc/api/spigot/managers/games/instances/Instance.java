package fr.multimc.api.spigot.managers.games.instances;

import com.sk89q.worldedit.WorldEditException;
import fr.multimc.api.spigot.managers.teams.MmcTeam;
import fr.multimc.api.spigot.tools.entities.MmcEntity;
import fr.multimc.api.spigot.tools.entities.player.MmcPlayer;
import fr.multimc.api.spigot.tools.locations.RelativeLocation;
import fr.multimc.api.spigot.tools.schematics.Schematic;
import fr.multimc.api.spigot.tools.schematics.SchematicOptions;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class Instance extends BukkitRunnable{

    private final JavaPlugin plugin;
    private final InstancesManager instancesManager;
    private final InstanceSettings instanceSettings;
    private final Location instanceLocation;
    private final int instanceId;
    private final List<MmcTeam> mmcTeams;
    private boolean isRunning = false;
    private final List<Entity> instanceEntities;
    private final List<MmcPlayer> players;
    private final HashMap<UUID, Location> playerSpawns;
    private InstanceState instanceState;

    private int remainingTime;

    public Instance(JavaPlugin plugin, InstancesManager instancesManager, int instanceId, InstanceSettings settings, Location instanceLocation, List<MmcTeam> mmcTeams) {
        this.instancesManager = instancesManager;
        this.instanceId = instanceId;
        this.updateState(InstanceState.PRE_CREATE);
        this.plugin = plugin;
        this.instanceSettings = settings;
        this.instanceLocation = instanceLocation;
        this.mmcTeams = new ArrayList<>(mmcTeams);
        this.instanceEntities = new ArrayList<>();
        this.remainingTime = this.instanceSettings.duration();
        this.players = this.getInstancePlayers();
        this.playerSpawns = this.getPlayerSpawnsList();
        this.updateState(InstanceState.CREATE);
    }

    // GAME ACTIONS
    /**
     * Initialize game instance
     */
    public void init(){
        if(this.instanceState == InstanceState.PRE_INIT || this.instanceState == InstanceState.INIT) return;
        this.updateState(InstanceState.PRE_INIT);
        // Place schematic
        SchematicOptions options = instanceSettings.schematicOptions();
        options.setLocation(instanceLocation);
        this.pasteSchematic(instanceSettings.schematic(), options);
        for(UUID uuid: this.playerSpawns.keySet()){
            MmcPlayer mmcPlayer = this.players.stream().filter(p -> p.getUUID().equals(uuid)).findFirst().orElse(null);
            if(mmcPlayer != null){
                this.teleportPlayer(mmcPlayer, this.playerSpawns.get(uuid));
                this.setPlayerSpawn(mmcPlayer, this.playerSpawns.get(uuid));
                mmcPlayer.setGameModeSync(this.plugin, GameMode.SURVIVAL);
            }
        }
        // Spawn entities
        for(MmcEntity entity : instanceSettings.entities()){
            instanceEntities.add(entity.spawn(instanceLocation, this.instanceId));
        }
        this.updateState(InstanceState.INIT);
    }

    /**
     * Start game instance
     */
    public void start(){
        if(this.instanceState == InstanceState.PRE_START || this.instanceState == InstanceState.START) return;
        this.updateState(InstanceState.PRE_START);
        this.isRunning = true;
        this.runTaskAsynchronously(this.plugin);
        this.updateState(InstanceState.START);
    }

    /**
     * Default instance stop (players will return to the lobby)
     */
    public void stop(){
        if(this.instanceState == InstanceState.PRE_STOP || this.instanceState == InstanceState.STOP) return;
        this.updateState(InstanceState.PRE_STOP);
        this.isRunning = false;
        for(MmcPlayer mmcPlayer : this.players){
            this.teleportPlayer(mmcPlayer, this.instancesManager.getLobbyWorld().getSpawnPoint());
        }
        this.cancel();
        this.updateState(InstanceState.STOP);
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
        this.init();
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
        this.remainingTime = this.instanceSettings.duration();
    }

    /**
     * Method called every tick delay defined in InstanceSettings
     */
    public void tick(){}

    /**
     * Main game loop, run async when instance is started
     */
    @SuppressWarnings("BusyWait")
    @Override
    public void run(){
        double deltaTick = 0.05 * this.instanceSettings.tickDelay();
        long lastTickTime;
        long lastSecondTime;
        long nextTickTime = (long) (System.currentTimeMillis() + deltaTick * 1000L);
        long nextSecondTime = System.currentTimeMillis() + 1000L;
        while(isRunning && remainingTime >= 0){
            if(System.currentTimeMillis() >= nextTickTime){
                this.tick();
                lastTickTime = nextTickTime;
                nextTickTime = (long) (lastTickTime + deltaTick * 1000L);
            }
            if(System.currentTimeMillis() >= nextSecondTime){
                this.remainingTime--;
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
            this.stop();
        }
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

    public void setPlayerSpawn(@NotNull MmcPlayer mmcPlayer, @Nullable Location location){
        if(location != null){
            Bukkit.getScheduler().runTask(this.plugin, () -> mmcPlayer.setSpawnPoint(location));
        }
    }

    public void broadcast(@Nonnull String message) {
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
    protected void updateState(@NotNull InstanceState state){
        this.instanceState = state;
        this.instancesManager.updateInstanceState(this.instanceId, state);
    }

    /**
     * Paste instance schematic
     * @param schematic Schematic object
     * @param schematicOptions SchematicOptions object
     */
    private void pasteSchematic(@NotNull Schematic schematic, @NotNull SchematicOptions schematicOptions){
        try {
            schematic.paste(schematicOptions);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    // PRIVATE GETTERS
    /**
     * Return a map with all players spawns locations
     * @return Hashmap with as key a Player and as value a Location
     */
    private HashMap<UUID, Location> getPlayerSpawnsList(){
        HashMap<UUID, Location> playerSpawns = new HashMap<>();
        switch(this.instanceSettings.gameType()) {
            case SOLO -> playerSpawns.put(this.mmcTeams.get(0).getPlayers().get(0).getUUID(), this.getSpawnPoints().get(0));
            case ONLY_TEAM -> {
                List<Location> spawnPoints = this.getSpawnPoints();
                int mod = spawnPoints.size();
                for (int i = 0; i < this.getPlayerCount(); i++) {
                    playerSpawns.put(this.mmcTeams.get(0).getPlayers().get(i).getUUID(), spawnPoints.get(i % mod));
                }
            }
            case TEAM_VS_TEAM -> {
                int spawnPointsCount = this.getSpawnPoints().size() % 2 == 0 ? this.getSpawnPoints().size() / 2 : ((this.getSpawnPoints().size() - 1) / 2);
                List<Location> t1SpawnPoints = this.getSpawnPoints().subList(0, spawnPointsCount);
                List<Location> t2SpawnPoints = this.getSpawnPoints().subList(spawnPointsCount, this.getSpawnPoints().size());
                for (int i = 0; i < this.mmcTeams.get(0).getTeamSize(); i++) {
                    playerSpawns.put(this.mmcTeams.get(0).getPlayers().get(i).getUUID(), t1SpawnPoints.get(i % t1SpawnPoints.size()));
                }
                for (int i = 0; i < this.mmcTeams.get(1).getTeamSize(); i++) {
                    playerSpawns.put(this.mmcTeams.get(1).getPlayers().get(i).getUUID(), t2SpawnPoints.get(i % t2SpawnPoints.size()));
                }
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
        for(RelativeLocation customLocation : this.instanceSettings.spawnPoints()){
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
    public boolean isPlayerOnInstance(MmcPlayer mmcPlayer){
        for(MmcPlayer gamePlayer : this.players){
            if(gamePlayer.equals(mmcPlayer)){
                return true;
            }
        }
        return false;
    }

    // PUBLIC GETTERS
    public InstanceSettings getInstanceSettings() {
        return instanceSettings;
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
    public InstanceState getInstanceState() {
        return instanceState;
    }
    public HashMap<UUID, Location> getPlayerSpawns() {
        return playerSpawns;
    }
}
