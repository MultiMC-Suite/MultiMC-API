package fr.multimc.api.spigot.managers;

import fr.multimc.api.commons.data.database.Database;
import fr.multimc.api.spigot.teams.MmcTeam;
import fr.multimc.api.commons.data.handlers.GameTablesHandler;
import fr.multimc.api.spigot.entities.player.MmcPlayer;
import fr.multimc.api.commons.data.handlers.interfaces.ITeamHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "DataFlowIssue"})
public class TeamManager {

    private final ITeamHandler ITeamHandler;
    private final List<MmcTeam> mmcTeams = new ArrayList<>();

    public TeamManager(@NotNull Database database) {
        this.ITeamHandler = new GameTablesHandler(database);
    }

    public TeamManager(@NotNull String apiURL) {
        // TODO: init handler with REST API handler
        this.ITeamHandler = null;
    }

    public void addTeam(@NotNull String teamCode, @NotNull String name, @NotNull String... players){
        this.ITeamHandler.addTeam(teamCode, name, players);
    }

    public List<MmcTeam> loadTeams(){
        // Create teams
        this.mmcTeams.clear();
        Map<String, List<String>> playersByTeam = this.ITeamHandler.getPlayersByTeam();
        Map<String, String> teamNames = this.ITeamHandler.getTeamNamesByTeam();
        // Iterate by team code
        for(String teamCode: playersByTeam.keySet()){
            List<String> playersName = playersByTeam.get(teamCode);
            List<MmcPlayer> players = new ArrayList<>();
            // Add all players to team
            for(String playerName: playersName){
                MmcPlayer player = new MmcPlayer(playerName);
                players.add(player);
            }
            // Add team object to list
            MmcTeam mmcTeam = new MmcTeam(teamCode, teamNames.get(teamCode), players.toArray(new MmcPlayer[0]));
            this.mmcTeams.add(mmcTeam);
        }
        return this.mmcTeams;
    }

    public List<MmcTeam> getTeams() {
        return this.mmcTeams;
    }

    public MmcTeam getTeamFromCode(@NotNull String teamCode){
        for(MmcTeam mmcTeam : this.mmcTeams){
            if(mmcTeam.getTeamCode().equals(teamCode)){
                return mmcTeam;
            }
        }
        return null;
    }

    public MmcTeam getTeamFromPlayer(@NotNull MmcPlayer player){
        for(MmcTeam mmcTeam : this.mmcTeams){
            if(mmcTeam.isPlayerInTeam(player)){
                return mmcTeam;
            }
        }
        return null;
    }

    public void pushScores(@NotNull Map<String, Integer> localScores){
        Map<String, Integer> currentScores = this.ITeamHandler.getScores();
        Map<String, Integer> newScores = new HashMap<>();
        localScores.forEach((teamCode, score) -> newScores.put(teamCode, currentScores.getOrDefault(teamCode, 0) + score));
        this.ITeamHandler.setScores(newScores);
    }

    public void pushTeamScores(@NotNull Map<MmcTeam, Integer> localScores){
        Map<String, Integer> currentScores = new HashMap<>();
        localScores.forEach((team, score) -> currentScores.put(team.getTeamCode(), score));
        this.pushScores(currentScores);
    }
}
