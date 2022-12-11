package fr.multimc.api.commons.database.tables;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.commons.database.enums.SQLState;
import fr.multimc.api.commons.database.query.QueryResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class GameTablesHandler {
    private final TeamsTable teamsTable;
    private final PlayersTable playersTable;

    public GameTablesHandler(@NotNull Database database) {
        this.teamsTable = new TeamsTable(database);
        this.playersTable = new PlayersTable(database);
    }

    public void addTeam(String teamCode, String teamName, String... playersName){
        QueryResult queryResult = this.teamsTable.addTeam(teamCode, teamName, playersName);
        if(queryResult.queryStatus() != SQLState.SUCCESS) throw new RuntimeException("Error while adding team to database: %s".formatted(queryResult.queryStatus()));
        for(String playerName: playersName){
            this.playersTable.addPlayer(teamCode, playerName);
        }
    }

    public Map<String, List<String>> getPlayersByTeam(){
        return this.playersTable.getPlayersByTeam();
    }

    public Map<String, String> getTeamNamesByTeam(){
        return this.teamsTable.getTeamNames();
    }

    public Map<String, Integer> getScores(){
        return this.teamsTable.getScores();
    }

    public void setScores(Map<String, Integer> scores){
        this.teamsTable.setScores(scores);
    }
}
