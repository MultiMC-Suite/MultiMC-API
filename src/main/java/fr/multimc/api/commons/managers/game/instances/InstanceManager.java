package fr.multimc.api.commons.managers.game.instances;

import fr.multimc.api.commons.managers.game.GameType;
import fr.multimc.api.commons.managers.teammanager.Team;
import fr.multimc.api.commons.managers.worldmanagement.CustomWorldCreator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class InstanceManager {

    private final JavaPlugin plugin;
    private final List<Instance> instances;
    private List<Team> teams = new ArrayList<>();
    private final GameType gameType;
    private final Class<? extends Instance> instanceClass;
    private final InstanceSettings settings;

    public InstanceManager(JavaPlugin plugin, Class<? extends Instance> instanceClass, InstanceSettings settings) {
        this.plugin = plugin;
        this.instances = new ArrayList<>();
        this.gameType = settings.getGameType();
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

    public boolean start(List<Team> teams) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        this.teams = new ArrayList<>(teams);
        System.out.println("Starting instances");
        List<Team> gameTeams = new ArrayList<>();
        System.out.println("Game teams created");
        switch (this.gameType){
            case SOLO:
                System.out.println("Loading game teams");
                gameTeams = this.getOnePlayerTeams();
                System.out.println("Game teams loaded");
                break;
            case ONLY_TEAM:
                gameTeams = new ArrayList<>(teams);
                break;
            case TEAM_VS_TEAM:
                // TODO
                break;
        }
        // Create instances
        System.out.println(String.format("%d instances to create", this.getInstanceCount()));
        for(int i = 0; i < this.getInstanceCount(); i++){
            System.out.printf(String.format("Creating instance %d/%d%n", i, this.getInstanceCount()));
            Location location = new Location(this.getLobbyWorld(), i * 50, 100, 0);
            switch (this.gameType){
                case SOLO:
                    List<Team> instanceTeams = new ArrayList<>();
                    instanceTeams.add(gameTeams.get(i));
                    this.instances.add((Instance) this.instanceClass.getConstructors()[0].newInstance(this.plugin, i, this.settings, location, instanceTeams));
                    break;
                case ONLY_TEAM:
                    this.instances.add((Instance) this.instanceClass.getConstructors()[0].newInstance(this.plugin, i, this.settings, location, gameTeams));
                    break;
                case TEAM_VS_TEAM:
                    // TODO
                    break;
            }
        }
        // Init instances
        for(Instance instance: instances){
            System.out.println("Initializing instance with id " + instance.getInstanceId());
            instance.init();
            System.out.println("Instance initialized with id " + instance.getInstanceId());
        }
        // Start instances
        for(Instance instance: instances){
            System.out.println("Starting instance with id " + instance.getInstanceId());
            instance.start();
            System.out.println("Instance started with id " + instance.getInstanceId());
        }
        return true;
    }

    private int getInstanceCount(){
        switch (this.gameType){
            case SOLO:
                int count = 0;
                for(Team team : this.teams){
                    count += team.getPlayers().size();
                }
                return count;
            case ONLY_TEAM:
                return this.teams.size();
            case TEAM_VS_TEAM:
                if(this.teams.size() % 2 == 0) {
                    return this.teams.size() / 2;
                }
            default:
                return -1;
        }
    }

    private List<Team> getOnePlayerTeams(){
        List<Team> teams = new ArrayList<>();
        for(Team team : this.teams){
            for(Player player: team.getPlayers()){
                Team onePlayerTeam = new Team(team.getName(), team.getId(), player);
                teams.add(onePlayerTeam);
            }
        }
        return teams;
    }
}
