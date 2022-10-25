package fr.multimc.api.spigot.managers.games.instances;

import com.sk89q.worldedit.WorldEditException;
import fr.multimc.api.spigot.customs.CustomEntity;
import fr.multimc.api.spigot.managers.teams.APIPlayer;
import fr.multimc.api.spigot.tools.locations.RelativeLocation;
import fr.multimc.api.spigot.managers.schematics.Schematic;
import fr.multimc.api.spigot.managers.schematics.SchematicOptions;
import fr.multimc.api.spigot.managers.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class Instance extends BukkitRunnable{

    private final JavaPlugin plugin;
    private final InstancesManager instancesManager;
    private final int instanceId;
    private final InstanceSettings instanceSettings;
    private final Location instanceLocation;
    private final List<Team> teams;
    private boolean isRunning = false;
    private final List<Entity> instanceEntities;
    private final List<APIPlayer> players;
    HashMap<APIPlayer, Location> playerSpawns;

    private int remainingTime;

    public Instance(JavaPlugin plugin, InstancesManager instancesManager, int instanceId, InstanceSettings settings, Location instanceLocation, List<Team> teams) {
        this.plugin = plugin;
        this.instancesManager = instancesManager;
        this.instanceId = instanceId;
        this.instanceSettings = settings;
        this.instanceLocation = instanceLocation;
        this.teams = new ArrayList<>(teams);
        this.instanceEntities = new ArrayList<>();
        this.remainingTime = this.instanceSettings.getDuration();
        this.players = this.getInstancePlayers();
        this.playerSpawns = this.getPlayerSpawnsList();
    }

    // GAME ACTIONS
    /**
     * Initialize game instance
     */
    public void init(){
        // Place schematic
        File schematicFile = instanceSettings.getSchematicFile();
        Schematic schematic = new Schematic(schematicFile);
        try {
            SchematicOptions options = new SchematicOptions(
                    instanceLocation);
            schematic.paste(options);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
        // Spawn entities
        for(CustomEntity entity : instanceSettings.getEntities()){
            instanceEntities.add(entity.spawn(instanceLocation, this.instanceId));
        }
    }

    /**
     * Start game instance
     */
    public void start(){
        // Teleport players
        for(APIPlayer player: this.playerSpawns.keySet()){
            this.teleportPlayer(player.getPlayer(), this.playerSpawns.get(player));
        }
        this.isRunning = true;
        this.runTaskAsynchronously(this.plugin);
        // this.runTaskTimerAsynchronously(this.plugin, 0, 20);
    }

    /**
     * Stop instance
     * @param teleportLobby True if player need to be teleported to the lobby at the end
     */
    public void stop(boolean teleportLobby){
        this.isRunning = false;
        if(teleportLobby){
            for(APIPlayer player : this.players){
                this.teleportPlayer(player.getPlayer(), this.instancesManager.getLobbyWorld().getSpawnPoint());
            }
        }
        this.cancel();
    }

    /**
     * Default instance stop (players will return to the lobby)
     */
    public void stop(){
        this.stop(true);
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
        this.remainingTime = this.instanceSettings.getDuration();
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
        double deltaTick = 0.05 * this.instanceSettings.getTickDelay();
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
            this.stop(true);
        }
    }

    /**
     * Asynchronously teleport a player to a location
     * @param player Player to teleport
     * @param location Target location
     */
    private void teleportPlayer(Player player, Location location){
        Bukkit.getScheduler().runTask(this.plugin, () -> player.teleport(location));
    }

    /**
     * Called to reconnect a disconnected player
     * @param player Player to reconnect
     */
    public void onPlayerReconnect(APIPlayer player){
        // Delete old player and add new one into players list
        // TODO: test without removing player
        this.players.removeIf(_player -> _player.equals(player));
        this.players.add(player);
        // Delete old player spawn and re-set it into playerSpawns list
        for(APIPlayer _player : this.playerSpawns.keySet()){
            if(_player.equals(player)){
                Location spawnLocation = this.playerSpawns.get(_player);
                this.playerSpawns.remove(_player);
                this.playerSpawns.put(player, spawnLocation);
                break;
            }
        }
        // If instance is running, teleport player into it
        if(this.isRunning){
            this.teleportPlayer(player.getPlayer(), this.playerSpawns.get(player));
        }
    }

    /**
     * Called when a player disconnect from the server
     */
    public void onPlayerDisconnect(Player player){

    }

    // PRIVATE GETTERS
    /**
     * Return a map with all players spawns locations
     * @return Hashmap with as key a Player and as value a Location
     */
    private HashMap<APIPlayer, Location> getPlayerSpawnsList(){
        HashMap<APIPlayer, Location> playerSpawns = new HashMap<>();
        switch(this.instanceSettings.getGameType()) {
            case SOLO -> playerSpawns.put(this.teams.get(0).getPlayers().get(0), this.getSpawnPoints().get(0));
            case ONLY_TEAM -> {
                List<Location> spawnPoints = this.getSpawnPoints();
                int mod = spawnPoints.size();
                for (int i = 0; i < this.getPlayerCount(); i++) {
                    playerSpawns.put(this.teams.get(0).getPlayers().get(i), spawnPoints.get(i % mod));
                }
            }
            case TEAM_VS_TEAM -> {
                int spawnPointsCount = this.getSpawnPoints().size() % 2 == 0 ? this.getSpawnPoints().size() / 2 : ((this.getSpawnPoints().size() - 1) / 2);
                List<Location> t1SpawnPoints = this.getSpawnPoints().subList(0, spawnPointsCount);
                List<Location> t2SpawnPoints = this.getSpawnPoints().subList(spawnPointsCount, this.getSpawnPoints().size());
                for (int i = 0; i < this.teams.get(0).getTeamSize(); i++) {
                    playerSpawns.put(this.teams.get(0).getPlayers().get(i), t1SpawnPoints.get(i % t1SpawnPoints.size()));
                }
                for (int i = 0; i < this.teams.get(1).getTeamSize(); i++) {
                    playerSpawns.put(this.teams.get(1).getPlayers().get(i), t2SpawnPoints.get(i % t2SpawnPoints.size()));
                }
            }
        }
        return playerSpawns;
    }

    /**
     * Get all players in this instance
     * @return List of players
     */
    private List<APIPlayer> getInstancePlayers(){
        List<APIPlayer> localPlayers = new ArrayList<>();
        for(Team team: this.getTeams()){
            localPlayers.addAll(team.getPlayers());
        }
        return localPlayers;
    }

    /**
     * Get all spawn Locations
     * @return List of locations
     */
    private List<Location> getSpawnPoints(){
        List<Location> spawnPoints = new ArrayList<>();
        for(RelativeLocation customLocation : this.instanceSettings.getSpawnPoints()){
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
     * @param player Target player
     * @return True if the player is on this instance
     */
    public boolean isPlayerOnInstance(Player player){
        for(APIPlayer _player: this.players){
            if(_player.getName().equals(player.getName())){
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
    public List<Team> getTeams() {
        return teams;
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
    public List<APIPlayer> getPlayers() {
        return players;
    }
}
