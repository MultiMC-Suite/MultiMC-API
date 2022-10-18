package fr.multimc.api.commons.managers.teammanager;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.commons.managers.teammanager.database.PlayersTable;
import fr.multimc.api.commons.managers.teammanager.database.TeamsTable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamManager {

    private final TeamsTable teamsTable;
    private final PlayersTable playersTable;

    public TeamManager(Database database) {
        this.teamsTable = new TeamsTable(database);
        this.playersTable = teamsTable.getPlayersTable();
    }

    public void addTeam(String teamCode, String name, String... players){
        teamsTable.addTeam(teamCode, name, players);
    }

    public List<Team> loadTeams(){
        List<Team> teams = new ArrayList<>();
        HashMap<String, List<String>> playersByTeam = playersTable.getPlayersByTeam();
        for(String teamCode: playersByTeam.keySet()){
            List<String> playersName = playersByTeam.get(teamCode);
            List<Player> players = new ArrayList<>();
            String teamName = teamsTable.getTeamName(teamCode);
            if(teamName != null){
                for(String playerName: playersName){
                    Player player = Bukkit.getPlayer(playerName);
                    if(player != null){
                        players.add(player);
                    }else{
                        return null;
                    }
                }
                Team team = new Team(teamsTable.getTeamName(teamCode), teamCode, players.toArray(new Player[0]));
                teams.add(team);
            }
        }
        return teams;
    }
}
