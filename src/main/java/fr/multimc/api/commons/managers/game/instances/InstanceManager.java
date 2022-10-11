package fr.multimc.api.commons.managers.game.instances;

import fr.multimc.api.commons.managers.game.GameType;
import fr.multimc.api.commons.managers.teammanager.Team;
import fr.multimc.api.commons.managers.worldmanagement.CustomWorldCreator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class InstanceManager {

    private final JavaPlugin plugin;
    private final List<Instance> instances;
    private final List<Team> teams = new ArrayList<>();
    private final GameType gameType;
    private final Class<? extends Instance> instanceClass;
    private final InstanceSettings settings;

    public InstanceManager(JavaPlugin plugin, GameType gameType, Class<? extends Instance> instanceClass, InstanceSettings settings) {
        this.plugin = plugin;
        this.instances = new ArrayList<>();
        this.gameType = gameType;
        this.instanceClass = instanceClass;
        this.settings = settings;
        this.generateLobbyWorld();
    }

    private void generateLobbyWorld(){
        if (this.getLobbyWorld() == null) {
            CustomWorldCreator worldCreator = new CustomWorldCreator();
            worldCreator.generate(this.getLobbyWorldName());
        }
        for(int x = -1; x < 2; x++){
            for(int z = -1; z < 2; z++){
                this.getLobbyWorld().getBlockAt(x, 0, z).setType(Material.BEDROCK);
            }
        }
    }

    public World getLobbyWorld(){
        return Bukkit.getWorld(this.getLobbyWorldName());
    }

    private String getLobbyWorldName(){
        return String.format("%s_lobby", this.settings.getWorldsPrefix());
    }

    public void start(List<Team> teams) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        for(int i = 0; i < this.getInstanceCount(); i++){
            Location location = new Location(this.getLobbyWorld(), i * 50, 100, 0);
            this.instances.add((Instance) this.instanceClass.getConstructors()[0].newInstance(this.plugin, this.settings, location, teams)); // TODO teams repartition
        }
        // Init instances
        for(Instance instance: instances){
            instance.init();
        }
        // Start instances
        for(Instance instance: instances){
            instance.start();
        }
    }

    private int getInstanceCount(){
        switch (this.gameType){
            case ONLY_TEAM:
                return this.teams.size();
            case SOLO:
                int count = 0;
                for(Team team : this.teams){
                    count += team.getPlayers().size();
                }
                return count;
            case TEAM_VS_TEAM:
                if(this.teams.size() % 2 == 0) {
                    return this.teams.size() / 2;
                }
            default:
                return -1;
        }
    }
}
