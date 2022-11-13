package fr.multimc.api.spigot.pre_made.commands.executors;

import fr.multimc.api.spigot.managers.instance.InstancesManager;
import fr.multimc.api.spigot.managers.teams.MmcTeam;
import fr.multimc.api.spigot.managers.teams.TeamManager;
import fr.multimc.api.spigot.tools.entities.player.MmcPlayer;
import fr.multimc.api.spigot.tools.utils.dispatcher.DispatchAlgorithm;
import fr.multimc.api.spigot.tools.utils.dispatcher.Dispatcher;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class StartCommand implements CommandExecutor {
    private final InstancesManager instancesManager;
    private final TeamManager teamManager;

    public StartCommand(@NotNull InstancesManager instancesManager, @NotNull TeamManager teamManager){
        this.instancesManager = instancesManager;
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        switch (args.length){
            case 1 -> this.instancesManager.start(this.getTeams(Integer.parseInt(args[0]), DispatchAlgorithm.RANDOM_UNIQUE));
            case 2 -> {
                int teamSize = Integer.parseInt(args[0]);
                if(args[0].equals("round_robin")){
                    this.instancesManager.start(this.getTeams(teamSize, DispatchAlgorithm.ROUND_ROBIN));
                }else{
                    this.instancesManager.start(this.getTeams(teamSize, DispatchAlgorithm.RANDOM_UNIQUE));
                }
            }
            default -> this.instancesManager.start(this.teamManager.loadTeams());
        }
        return true;
    }

    public List<MmcTeam> getTeams(int teamSize, DispatchAlgorithm dispatchAlgorithm){
        List<MmcTeam> teams = new ArrayList<>();
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        // Create all empty teams
        for(int i = 0; i < onlinePlayers.size() / teamSize; i++){
            teams.add(new MmcTeam(String.format("TEAM %d", i), String.format("TEAM_%d", i)));
        }
        // Dispatch players into these teams
        List<Player> selectedPlayers = new ArrayList<>();
        for(int i = 0; i < onlinePlayers.size(); i++){
            selectedPlayers.add(onlinePlayers.get(i));
            if((i + 1) % teamSize == 0 || i == onlinePlayers.size() - 1){
                Map<Player, MmcTeam> dispatchedTeams = new Dispatcher(dispatchAlgorithm).dispatch(selectedPlayers, teams);
                if(dispatchedTeams == null) return null;
                for(Map.Entry<Player, MmcTeam> entry: dispatchedTeams.entrySet()){
                    entry.getValue().addPlayer(new MmcPlayer(entry.getKey()));
                }
                selectedPlayers.clear();
            }
        }
        return teams;
    }
}
