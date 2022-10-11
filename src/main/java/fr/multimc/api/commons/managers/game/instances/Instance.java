package fr.multimc.api.commons.managers.game.instances;

import com.sk89q.worldedit.WorldEditException;
import fr.multimc.api.commons.managers.game.CustomEntity;
import fr.multimc.api.commons.managers.teammanager.Team;
import fr.multimc.api.commons.managers.worldmanagement.SchematicManager;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class Instance extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final InstanceSettings instanceSettings;
    private final Location instanceLocation;
    private final List<Team> teams;
    private boolean isRunning = false;

    public Instance(JavaPlugin plugin, InstanceSettings settings, Location instanceLocation, Team... teams) {
        this.plugin = plugin;
        this.instanceSettings = settings;
        this.instanceLocation = instanceLocation;
        this.teams = Arrays.asList(teams);
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
        int playerCount = this.getPlayerCount();
        for(int i = 0; i < teams.size(); i++){

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

    private int getPlayerCount(){
        int count = 0;
        for(Team team : this.teams){
            count += team.getPlayers().size();
        }
        return count;
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
}
