package fr.multimc.api.commons.managers.game.instances;

import com.sk89q.worldedit.WorldEditException;
import fr.multimc.api.commons.managers.game.CustomEntity;
import fr.multimc.api.commons.managers.game.CustomLocation;
import fr.multimc.api.commons.managers.teammanager.Team;
import fr.multimc.api.commons.managers.worldmanagement.SchematicManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Instance extends BukkitRunnable{

    private final JavaPlugin plugin;
    private final InstanceManager instanceManager;
    private final int instanceId;
    private final InstanceSettings instanceSettings;
    private final Location instanceLocation;
    private final List<Team> teams;
    private boolean isRunning = false;
    private final List<Entity> instanceEntities;

    private int remainingTime;

    public Instance(JavaPlugin plugin, InstanceManager instanceManager, int instanceId, InstanceSettings settings, Location instanceLocation, List<Team> teams) {
        this.plugin = plugin;
        this.instanceManager = instanceManager;
        this.instanceId = instanceId;
        this.instanceSettings = settings;
        this.instanceLocation = instanceLocation;
        this.teams = new ArrayList<>(teams);
        this.instanceEntities = new ArrayList<>();
        this.remainingTime = this.instanceSettings.getDuration();
    }

    public void init(){
        // Place schematic
        File schematicFile = instanceSettings.getSchematicFile();
        SchematicManager schematicManager = new SchematicManager(schematicFile);
        try {
            schematicManager.pasteSchematic(instanceLocation, false, false, false);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
        // Spawn entities
        for(CustomEntity entity : instanceSettings.getEntities()){
            instanceEntities.add(entity.spawn(instanceLocation, this.instanceId));
        }
    }

    public void start(){
        switch(this.instanceSettings.getGameType()) {
            case SOLO -> this.teleportPlayer(this.teams.get(0).getPlayers().get(0), this.getSpawnPoints().get(0));
            case ONLY_TEAM -> {
                List<Location> spawnPoints = this.getSpawnPoints();
                int mod = spawnPoints.size();
                for (int i = 0; i < this.getPlayerCount(); i++) {
                    this.teleportPlayer(this.teams.get(0).getPlayers().get(i), spawnPoints.get(i % mod));
                }
            }
            case TEAM_VS_TEAM -> {
                int spawnPointsCount = this.getSpawnPoints().size() % 2 == 0 ? this.getSpawnPoints().size() / 2 : ((this.getSpawnPoints().size() - 1) / 2);
                List<Location> t1SpawnPoints = this.getSpawnPoints().subList(0, spawnPointsCount);
                List<Location> t2SpawnPoints = this.getSpawnPoints().subList(spawnPointsCount, this.getSpawnPoints().size());
                for (int i = 0; i < this.teams.get(0).getPlayers().size(); i++) {
                    this.teleportPlayer(this.teams.get(0).getPlayers().get(i), t1SpawnPoints.get(i % t1SpawnPoints.size()));
                }
                for (int i = 0; i < this.teams.get(1).getPlayers().size(); i++) {
                    this.teleportPlayer(this.teams.get(1).getPlayers().get(i), t2SpawnPoints.get(i % t2SpawnPoints.size()));
                }
            }
        }
        this.isRunning = true;
        this.runTaskAsynchronously(this.plugin);
        // this.runTaskTimerAsynchronously(this.plugin, 0, 20);
    }

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

    private void resetInstance(){
        // Clear entities
        this.instanceEntities.forEach(Entity::remove);
        this.instanceEntities.clear();
        // Reset time
        this.remainingTime = this.instanceSettings.getDuration();
    }

    public void stop(boolean teleportLobby){
        this.isRunning = false;
        this.cancel();
        if(teleportLobby){
            for(Team team : this.teams){
                for(Player player : team.getPlayers()){
                    this.teleportPlayer(player, this.instanceManager.getLobbySpawnLocation());
                }
            }
        }
    }

    public void stop(){
        this.stop(true);
    }

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

    public void tick(){}

    private List<Location> getSpawnPoints(){
        List<Location> spawnPoints = new ArrayList<>();
        for(CustomLocation customLocation : this.instanceSettings.getSpawnPoints()){
            spawnPoints.add(customLocation.toAbsolute(this.instanceLocation));
        }
        return spawnPoints;
    }

    private int getPlayerCount(){
        int count = 0;
        for(Team team : this.teams){
            count += team.getPlayers().size();
        }
        return count;
    }

    private void teleportPlayer(Player player, Location location){
        Bukkit.getScheduler().runTask(this.plugin, () -> player.teleport(location));
    }

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
}
