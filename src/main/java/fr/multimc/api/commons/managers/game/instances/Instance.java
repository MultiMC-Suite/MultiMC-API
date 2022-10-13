package fr.multimc.api.commons.managers.game.instances;

import com.sk89q.worldedit.WorldEditException;
import fr.multimc.api.commons.managers.game.CustomEntity;
import fr.multimc.api.commons.managers.game.CustomLocation;
import fr.multimc.api.commons.managers.teammanager.Team;
import fr.multimc.api.commons.managers.worldmanagement.SchematicManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class Instance extends BukkitRunnable{

    private final JavaPlugin plugin;
    private final int instanceId;
    private final InstanceSettings instanceSettings;
    private final Location instanceLocation;
    private final List<Team> teams;
    private boolean isRunning = false;

    public Instance(JavaPlugin plugin, int instanceId, InstanceSettings settings, Location instanceLocation, List<Team> teams) {
        this.plugin = plugin;
        this.instanceId = instanceId;
        this.instanceSettings = settings;
        this.instanceLocation = instanceLocation;
        this.teams = new ArrayList<>(teams);
    }

    public Instance init(){
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
            entity.spawn(instanceLocation);
        }
        return this;
    }

    public void start(){
        switch (this.instanceSettings.getGameType()){
            case SOLO:
                this.teleportPlayer(this.teams.get(0).getPlayers().get(0), this.getSpawnPoints().get(0));
                break;
            case ONLY_TEAM:
                List<Location> spawnPoints = this.getSpawnPoints();
                int mod = spawnPoints.size();
                for(int i = 0; i < this.getPlayerCount(); i++){
                    this.teleportPlayer(this.teams.get(0).getPlayers().get(i), spawnPoints.get(i % mod));
                }
                break;
            case TEAM_VS_TEAM:
                // TODO
                break;
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

    private Location getLocationFromCustomLocation(CustomLocation customLocation){
        return new Location(this.instanceLocation.getWorld(),
                this.instanceLocation.getX() + customLocation.x(),
                this.instanceLocation.getY() + customLocation.y(),
                this.instanceLocation.getZ() + customLocation.z());
    }

    private List<Location> getSpawnPoints(){
        List<Location> spawnPoints = new ArrayList<>();
        for(CustomLocation customLocation : this.instanceSettings.getSpawnPoints()){
            spawnPoints.add(this.getLocationFromCustomLocation(customLocation));
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
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            player.teleport(location);
        });
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
}
