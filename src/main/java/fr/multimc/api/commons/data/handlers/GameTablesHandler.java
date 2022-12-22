package fr.multimc.api.commons.data.handlers;

import fr.multimc.api.commons.data.database.Database;
import fr.multimc.api.commons.data.database.enums.SQLState;
import fr.multimc.api.commons.data.database.queries.QueryResult;
import fr.multimc.api.commons.data.database.tables.PlayersTable;
import fr.multimc.api.commons.data.database.tables.TeamsTable;
import fr.multimc.api.commons.data.handlers.interfaces.ITeamHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class GameTablesHandler implements ITeamHandler {
    private final TeamsTable teamsTable;
    private final PlayersTable playersTable;

    public GameTablesHandler(@NotNull Database database) {
        this.teamsTable = new TeamsTable(database);
        this.playersTable = new PlayersTable(database);
    }

    @Override
    public void addTeam(String teamCode, String teamName, String... playersName){
        QueryResult queryResult = this.teamsTable.addTeam(teamCode, teamName, playersName);
        if(queryResult.queryStatus() != SQLState.SUCCESS) throw new RuntimeException("Error while adding team to database: %s".formatted(queryResult.queryStatus()));
        for(String playerName: playersName){
            this.playersTable.addPlayer(teamCode, playerName);
        }
    }

    @Override
    public Map<String, List<String>> getPlayersByTeam(){
        return this.playersTable.getPlayersByTeam();
    }

    @Override
    public Map<String, String> getTeamNamesByTeam(){
        return this.teamsTable.getTeamNames();
    }

    @Override
    public Map<String, Integer> getScores(){
        return this.teamsTable.getScores();
    }

    @Override
    public void setScores(Map<String, Integer> scores){
        this.teamsTable.setScores(scores);
    }
}
