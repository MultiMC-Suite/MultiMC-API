package fr.multimc.api.commons.old_database.tables;

import fr.multimc.api.commons.old_database.Database;
import fr.multimc.api.commons.old_database.enums.DatabaseStatus;
import fr.multimc.api.commons.old_database.query.QueryResult;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class GameTablesHandler {
    private final TeamsTable teamsTable;
    private final PlayersTable playersTable;

    public GameTablesHandler(@NotNull Database database) {
        this.teamsTable = new TeamsTable(database);
        this.playersTable = new PlayersTable(database);
    }

    public void addTeam(String teamCode, String teamName, String... playersName){
        QueryResult queryResult = this.teamsTable.addTeam(teamCode, teamName, playersName);
        if(queryResult.queryStatus() != DatabaseStatus.SUCCESS) throw new RuntimeException("Error while adding team to database: %s".formatted(queryResult.queryStatus()));
        for(String playerName: playersName){
            this.playersTable.addPlayer(teamCode, playerName);
        }
    }

    public HashMap<String, List<String>> getPlayersByTeam(){
        return this.playersTable.getPlayersByTeam();
    }

    public HashMap<String, String> getTeamNamesByTeam(){
        return this.teamsTable.getTeamNames();
    }

    public HashMap<String, Integer> getScores(){
        return this.teamsTable.getScores();
    }

    public void setScores(HashMap<String, Integer> scores){
        this.teamsTable.setScores(scores);
    }
}
