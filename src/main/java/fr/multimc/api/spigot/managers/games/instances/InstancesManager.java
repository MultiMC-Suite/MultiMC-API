package fr.multimc.api.spigot.managers.games.instances;

import fr.multimc.api.spigot.managers.games.GameType;
import fr.multimc.api.spigot.managers.teams.Team;
import fr.multimc.api.spigot.managers.worlds.CustomWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class InstancesManager implements Listener {

    private final JavaPlugin plugin;
    private final List<Instance> instances;
    private List<Team> teams = new ArrayList<>();
    private final GameType gameType;
    private final Class<? extends Instance> instanceClass;
    private final InstanceSettings settings;
    private final Logger logger;
    private boolean isStarted = false;

    private final CustomWorld lobby;
    private final CustomWorld game;

    public InstancesManager(JavaPlugin plugin, Class<? extends Instance> instanceClass, InstanceSettings settings, CustomWorld lobby, CustomWorld game) {
        this.plugin = plugin;
        this.instances = new ArrayList<>();
        this.gameType = settings.getGameType();
        this.instanceClass = instanceClass;
        this.settings = settings;
        this.logger = plugin.getLogger();
        this.lobby = lobby;
        this.game = game;
        // Register local events handlers
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void start(List<Team> teams) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        this.instances.clear();
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
            Location location = new Location(this.game.getWorld(), i * 1000, 100, 0);
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
        this.instances.forEach(this::initInstances);
        // Start instances
        this.instances.forEach(this::startInstances);
        this.isStarted = true;
    }

    public void stopInstances(){
        instances.forEach(this::stopInstances);
    }

    private void initInstances(Instance instance){
        this.logger.info(String.format("Initializing instance %d...", instance.getInstanceId()));
        instance.init();
        this.logger.info(String.format("Instance %d initialized!", instance.getInstanceId()));
    }

    private void startInstances(Instance instance){
        this.logger.info(String.format("Starting instance %d...", instance.getInstanceId()));
        instance.start();
        this.logger.info(String.format("Instance %d started!", instance.getInstanceId()));
    }

    private void stopInstances(Instance instance){
        this.logger.info(String.format("Stopping instance %d...", instance.getInstanceId()));
        instance.stop();
        this.logger.info(String.format("Instance %d stopped!", instance.getInstanceId()));
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
                Team onePlayerTeam = new Team(team.getName(), team.getTeamCode(), player);
                teams.add(onePlayerTeam);
            }
        }
        return teams;
    }


    public CustomWorld getLobbyWorld(){
        return this.lobby;
    }

    public CustomWorld getGameWorld(){
        return this.game;
    }

    public boolean isStarted() {
        return this.isStarted;
    }

    @Deprecated
    public Location getLobbySpawnLocation(){
        return this.lobby.getSpawnPoint();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        if(this.isStarted){
            for(Instance instance : this.instances){
                if(instance.isPlayerOnInstance(player)){
                    if(instance.isRunning()){
                        this.logger.info(String.format("Reconnecting player %s to instance %d...", player.getName(), instance.getInstanceId()));
                        instance.onPlayerReconnect(player);
                        this.logger.info(String.format("Player %s reconnected to instance %d...", player.getName(), instance.getInstanceId()));
                    }else{
                        player.teleport(this.getLobbyWorld().getSpawnPoint());
                    }
                }
            }
        }else{
            player.teleport(this.getLobbyWorld().getSpawnPoint());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        if(this.isStarted){
            for(Instance instance : this.instances){
                if(instance.isPlayerOnInstance(player)){
                    if(instance.isRunning()){
                        this.logger.info(String.format("Disconnecting player %s to instance %d...", player.getName(), instance.getInstanceId()));
                        instance.onPlayerDisconnect(player);
                        this.logger.info(String.format("Player %s disconnected to instance %d...", player.getName(), instance.getInstanceId()));
                    }
                }
            }
        }
    }
}
