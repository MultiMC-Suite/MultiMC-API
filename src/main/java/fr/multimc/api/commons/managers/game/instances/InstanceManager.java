package fr.multimc.api.commons.managers.game.instances;

import fr.multimc.api.commons.managers.game.GameType;
import fr.multimc.api.commons.managers.game.Lobby;
import fr.multimc.api.commons.managers.teammanager.Team;
import fr.multimc.api.commons.managers.worldmanagement.CustomWorldCreator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class InstanceManager {

    private final JavaPlugin plugin;
    private final List<Instance> instances;
    private List<Team> teams = new ArrayList<>();
    private final GameType gameType;
    private final Class<? extends Instance> instanceClass;
    private final InstanceSettings settings;
    private final Logger logger;

    public InstanceManager(JavaPlugin plugin, Class<? extends Instance> instanceClass, InstanceSettings settings, Lobby lobby) {
        this.plugin = plugin;
        this.instances = new ArrayList<>();
        this.gameType = settings.getGameType();
        this.instanceClass = instanceClass;
        this.settings = settings;
        this.logger = plugin.getLogger();
        this.generateLobbyWorld(lobby);
        this.generateGameWorld();
    }

    private void generateGameWorld() {
        this.generateWorld(this.getGameWorldName());
    }

    private void generateLobbyWorld(Lobby lobby) {
        this.generateWorld(this.getLobbyWorldName());
        lobby.init();
    }

    private void generateWorld(String name){
        if (Bukkit.getWorld(name) == null) {
            this.logger.info(String.format("Generating world %s...", name));
            CustomWorldCreator worldCreator = new CustomWorldCreator();
            worldCreator.generate(name);
            this.logger.info(String.format("World %s generated and loaded!", name));
        }else{
            this.logger.info(String.format("World %s loaded!", name));
        }
    }

    public World getLobbyWorld(){
        return Bukkit.getWorld(this.getLobbyWorldName());
    }

    public World getGameWorld(){
        return Bukkit.getWorld(this.getGameWorldName());
    }

    private String getLobbyWorldName(){
        return String.format("%s_lobby", this.settings.getWorldsPrefix());
    }

    private String getGameWorldName(){
        return String.format("%s_game", this.settings.getWorldsPrefix());
    }

    public void start(List<Team> teams) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        this.teams = new ArrayList<>(teams);
        List<List<Team>> gameTeams = new ArrayList<>();
        switch (this.gameType) {
            case SOLO -> {
                List<Team> localTeams1 = this.getOnePlayerTeams();
                gameTeams.add(localTeams1);
            }
            case ONLY_TEAM -> {
                List<Team> localTeams2 = new ArrayList<>(teams);
                gameTeams.add(localTeams2);
            }
            case TEAM_VS_TEAM -> gameTeams = this.getTeamsTuple(this.teams);
        }
        // Create instances
        for(int i = 0; i < this.getInstanceCount(); i++){
            this.logger.info(String.format("Creating instance %d/%d", i+1, this.getInstanceCount()));
            Location location = new Location(this.getGameWorld(), i * 1000, 100, 0);
            switch(this.gameType) {
                case SOLO -> {
                    List<Team> instanceTeams = new ArrayList<>();
                    instanceTeams.add(gameTeams.get(0).get(i));
                    this.instances.add((Instance) this.instanceClass.getConstructors()[0].newInstance(this.plugin, this, i, this.settings, location, instanceTeams));
                }
                case ONLY_TEAM ->
                        this.instances.add((Instance) this.instanceClass.getConstructors()[0].newInstance(this.plugin, this, i, this.settings, location, List.of(gameTeams.get(0).get(i))));
                case TEAM_VS_TEAM ->
                        this.instances.add((Instance) this.instanceClass.getConstructors()[0].newInstance(this.plugin, this, i, this.settings, location, gameTeams.get(i)));
            }
        }
        // Init instances
        instances.forEach(this::initInstance);
        // Start instances
        instances.forEach(this::startInstance);
    }

    private void initInstance(Instance instance){
        this.logger.info(String.format("Initializing instance %d...", instance.getInstanceId()));
        instance.init();
        this.logger.info(String.format("Instance %d initialized!", instance.getInstanceId()));
    }

    private void startInstance(Instance instance){
        this.logger.info(String.format("Starting instance %d...", instance.getInstanceId()));
        instance.start();
        this.logger.info(String.format("Instance %d started!", instance.getInstanceId()));
    }

    public void stop(){
        instances.forEach(Instance::stop);
    }

    private List<List<Team>> getTeamsTuple(List<Team> teams){
        List<List<Team>> teamsTuple = new ArrayList<>();
        if(teams.size() % 2 == 0){
            for(int i = 0; i < teams.size(); i += 2){
                List<Team> teamTuple = new ArrayList<>();
                teamTuple.add(teams.get(i));
                teamTuple.add(teams.get(i+1));
                teamsTuple.add(teamTuple);
            }
        }
        return teamsTuple;
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

    public Location getLobbySpawnLocation(){
        return this.getLobbyWorld().getSpawnLocation();
    }
}
