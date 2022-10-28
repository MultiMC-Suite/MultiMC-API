package fr.multimc.api.spigot.managers.teams;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.commons.database.tables.PlayersTable;
import fr.multimc.api.commons.database.tables.TeamsTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class TeamManager {

    private final TeamsTable teamsTable;
    private final PlayersTable playersTable;
    private List<Team> teams = new ArrayList<>();

    public TeamManager(@NotNull Database database) {
        this.teamsTable = new TeamsTable(database);
        this.playersTable = teamsTable.getPlayersTable();
    }

    public void addTeam(@NotNull String teamCode, @NotNull String name, @NotNull String... players){
        teamsTable.addTeam(teamCode, name, players);
    }

    public List<Team> loadTeams(){
        // Create teams
        teams = new ArrayList<>();
        HashMap<String, List<String>> playersByTeam = playersTable.getPlayersByTeam();
        HashMap<String, String> teamNames = teamsTable.getTeamNames();
        // Iterate by team code
        for(String teamCode: playersByTeam.keySet()){
            List<String> playersName = playersByTeam.get(teamCode);
            List<APIPlayer> players = new ArrayList<>();
            // Add all players to team
            for(String playerName: playersName){
                APIPlayer player = new APIPlayer(playerName);
                players.add(player);
            }
            // Add team object to list
            Team team = new Team(teamNames.get(teamCode), teamCode, players.toArray(new APIPlayer[0]));
            teams.add(team);
        }
        return teams;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public Team getTeamFromCode(@NotNull String teamCode){
        for(Team team: teams){
            if(team.getTeamCode().equals(teamCode)){
                return team;
            }
        }
        return null;
    }

    public Team getTeamFromPlayer(@NotNull APIPlayer player){
        for(Team team: teams){
            if(team.isPlayerInTeam(player)){
                return team;
            }
        }
        return null;
    }

    public void pushScores(@NotNull HashMap<String, Integer> localScores){
        HashMap<String, Integer> currentScores = this.teamsTable.getCurrentScores();
        HashMap<String, Integer> newScores = new HashMap<>();
        for(String teamCode: localScores.keySet()){
            int remoteScore = localScores.get(teamCode);
            int currentScore = currentScores.get(teamCode);
            newScores.put(teamCode, currentScore + remoteScore);
        }
        this.teamsTable.updateScores(newScores);
    }
}
