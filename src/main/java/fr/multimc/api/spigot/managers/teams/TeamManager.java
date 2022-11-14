package fr.multimc.api.spigot.managers.teams;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.commons.database.tables.PlayersTable;
import fr.multimc.api.commons.database.tables.TeamsTable;
import fr.multimc.api.spigot.tools.entities.player.MmcPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class TeamManager {

    private final TeamsTable teamsTable;
    private final PlayersTable playersTable;
    private final List<MmcTeam> mmcTeams = new ArrayList<>();

    public TeamManager(@NotNull Database database) {
        this.teamsTable = new TeamsTable(database);
        this.playersTable = teamsTable.getPlayersTable();
    }

    public void addTeam(@NotNull String teamCode, @NotNull String name, @NotNull String... players){
        teamsTable.addTeam(teamCode, name, players);
    }

    public List<MmcTeam> loadTeams(){
        // Create teams
        this.mmcTeams.clear();
        HashMap<String, List<String>> playersByTeam = playersTable.getPlayersByTeam();
        HashMap<String, String> teamNames = teamsTable.getTeamNames();
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

    public void pushScores(@NotNull HashMap<String, Integer> localScores){
        HashMap<String, Integer> currentScores = this.teamsTable.getCurrentScores();
        HashMap<String, Integer> newScores = new HashMap<>();
        localScores.forEach((teamCode, score) -> newScores.put(teamCode, currentScores.getOrDefault(teamCode, 0) + score));
        this.teamsTable.updateScores(newScores);
    }

    public void pushTeamScores(@NotNull HashMap<MmcTeam, Integer> localScores){
        HashMap<String, Integer> currentScores = new HashMap<>();
        localScores.forEach((team, score) -> currentScores.put(team.getTeamCode(), score));
        this.pushScores(currentScores);
    }
}
