package fr.multimc.api.commons.managers.teammanager;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.commons.managers.teammanager.database.TeamsTable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamManager {

    private final TeamsTable teamsTable;

    public TeamManager(Database database) {
        this.teamsTable = new TeamsTable(database);
    }

    public void addTeam(String name, String... players){
        teamsTable.addTeam(name, players);
    }

    public List<Team> loadTeams(){
        List<Team> teams = new ArrayList<>();
        HashMap<Integer, List<String>> playersByTeam = teamsTable.getPlayersByTeam();
        System.out.println(playersByTeam);
        for(int teamId: playersByTeam.keySet()){
            List<String> playersName = playersByTeam.get(teamId);
            List<Player> players = new ArrayList<>();
            String teamName = teamsTable.getTeamName(teamId);
            if(teamName != null){
                for(String playerName: playersName){
                    Player player = Bukkit.getPlayer(playerName);
                    if(player != null){
                        players.add(player);
                    }else{
                        return null;
                    }
                }
                Team team = new Team(teamsTable.getTeamName(teamId), teamId, players.toArray(new Player[0]));
                teams.add(team);
            }
        }
        return teams;
    }
}
