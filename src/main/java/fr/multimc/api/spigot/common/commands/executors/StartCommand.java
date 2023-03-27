package fr.multimc.api.spigot.common.commands.executors;

import fr.multimc.api.spigot.common.entities.player.MmcPlayer;
import fr.multimc.api.spigot.common.tools.dispatcher.DispatchAlgorithm;
import fr.multimc.api.spigot.common.tools.dispatcher.Dispatcher;
import fr.multimc.api.spigot.games.GamesManager;
import fr.multimc.api.spigot.games.teams.MmcTeam;
import fr.multimc.api.spigot.games.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class that implements the {@link CommandExecutor} interface for the "start" command.
 * Allows for starting a game with specified teams.
 */
@SuppressWarnings("unused")
public class StartCommand implements CommandExecutor {
    private final GamesManager gamesManager;
    private final TeamManager teamManager;

    /**
     * Constructor for the StartCommand class.
     * @param gamesManager The {@link GamesManager} instance to be used for starting the game.
     * @param teamManager The {@link TeamManager} instance to be used for managing teams.
     */
    public StartCommand(@NotNull GamesManager gamesManager, @NotNull TeamManager teamManager){
        this.gamesManager = gamesManager;
        this.teamManager = teamManager;
    }

    /**
     * Executes the "start" command when called.
     * @param commandSender The {@link CommandSender} of the command.
     * @param command The {@link Command} being executed.
     * @param s {@link String} that represents yhe name of the command being executed.
     * @param args {@link String}[] that represents the additional arguments passed with the command.
     * @return boolean indicating if the command was successful or not.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<MmcTeam> teams;
        switch (args.length){
            case 1 -> teams = this.getTeams(Integer.parseInt(args[0]), DispatchAlgorithm.RANDOM_UNIQUE);
            case 2 -> {
                int teamSize = Integer.parseInt(args[0]);
                if(args[0].equals("round_robin")){
                    teams = this.getTeams(teamSize, DispatchAlgorithm.ROUND_ROBIN);
                }else{
                    teams = this.getTeams(teamSize, DispatchAlgorithm.RANDOM_UNIQUE);
                }
            }
            default -> teams = this.teamManager.loadTeams();
        }
        this.gamesManager.start(commandSender, teams);
        return true;
    }

    /**
     * Creates and dispatch players into teams with a specified team size and dispatch algorithm.
     * @param teamSize The size of each team.
     * @param dispatchAlgorithm The {@link DispatchAlgorithm} to be used for dispatching players into teams.
     * @return A {@link List<MmcTeam>} with players.
     */
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
