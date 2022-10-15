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
    private final int instanceId;
    private final InstanceSettings instanceSettings;
    private final Location instanceLocation;
    private final List<Team> teams;
    private boolean isRunning = false;
    private final List<Entity> instanceEntities;

    public Instance(JavaPlugin plugin, int instanceId, InstanceSettings settings, Location instanceLocation, List<Team> teams) {
        this.plugin = plugin;
        this.instanceId = instanceId;
        this.instanceSettings = settings;
        this.instanceLocation = instanceLocation;
        this.teams = new ArrayList<>(teams);
        this.instanceEntities = new ArrayList<>();
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

    // TODO: Restart or something like that

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
    }

    public void stop(){
        this.isRunning = false;
    }

    public void tick(){

    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run(){
        while(this.isRunning){
            try {
                Thread.sleep(instanceSettings.getTickTime());
                this.tick();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

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
}
