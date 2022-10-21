package fr.multimc.api.commons.managers.teammanager;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.commons.managers.teammanager.database.PlayersTable;
import fr.multimc.api.commons.managers.teammanager.database.TeamsTable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class TeamManager {

    private final TeamsTable teamsTable;
    private final PlayersTable playersTable;
    private List<Team> teams = new ArrayList<>();

    public TeamManager(Database database) {
        this.teamsTable = new TeamsTable(database);
        this.playersTable = teamsTable.getPlayersTable();
    }

    public void addTeam(String teamCode, String name, String... players){
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
            List<Player> players = new ArrayList<>();
            // Add all players to team
            for(String playerName: playersName){
                Player player = Bukkit.getPlayer(playerName);
                if(player != null){
                    players.add(player);
                }else{
                    return null;
                }
            }
            // Add team object to list
            Team team = new Team(teamNames.get(teamCode), teamCode, players.toArray(new Player[0]));
            teams.add(team);
        }
        return teams;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public Team getTeamFromCode(String teamCode){
        for(Team team: teams){
            if(team.getTeamCode().equals(teamCode)){
                return team;
            }
        }
        return null;
    }

    public Team getTeamFromPlayer(Player player){
        for(Team team: teams){
            if(team.isPlayerInTeam(player)){
                return team;
            }
        }
        return null;
    }

    public void pushScores(HashMap<String, Integer> localScores){
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
