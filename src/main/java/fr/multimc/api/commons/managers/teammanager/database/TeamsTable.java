package fr.multimc.api.commons.managers.teammanager.database;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.commons.database.Table;
import fr.multimc.api.commons.database.enums.DatabaseStatus;
import fr.multimc.api.commons.database.query.Query;
import fr.multimc.api.commons.database.query.QueryBuilder;
import fr.multimc.api.commons.database.query.QueryResult;
import fr.multimc.api.commons.database.query.QueryType;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

@SuppressWarnings("unused")
public class TeamsTable extends Table {

    private final PlayersTable playersTable;

    public TeamsTable(@NotNull Database database) {
        super(database, "teams", "code VARCHAR(6) PRIMARY KEY", "name VARCHAR(30) UNIQUE", "score INT");

        this.playersTable = new PlayersTable(this.getDatabase(), "players");
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean addTeam(String teamId, String teamName, String... playersName){
        Query teamQuery = new QueryBuilder()
                .setQueryType(QueryType.UPDATE)
                .setQuery(String.format("INSERT INTO %s VALUES ('%s', '%s', 0)", this.getTableName(), teamId, teamName))
                .getQuery();
        QueryResult queryResult = this.getDatabase().executeQuery(teamQuery);
        if(queryResult.queryStatus() == DatabaseStatus.SUCCESS){
            this.playersTable.addPlayers(teamId, playersName);
            return true;
        }
        return false;
    }

    private String getTeamId(String teamName){
        Query teamQuery = new QueryBuilder()
                .setQueryType(QueryType.SELECT)
                .setQuery(String.format("SELECT code FROM %s WHERE name='%s';", this.getTableName(), teamName))
                .getQuery();
        QueryResult queryResult = this.getDatabase().executeQuery(teamQuery);
        ResultSet resultSet = queryResult.resultSet();
        try {
            return resultSet.getString("code");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTeamName(String code) {
        Query teamQuery = new QueryBuilder()
                .setQueryType(QueryType.SELECT)
                .setQuery(String.format("SELECT name FROM %s WHERE code='%s';", this.getTableName(), code))
                .getQuery();
        QueryResult queryResult = this.getDatabase().executeQuery(teamQuery);
        ResultSet resultSet = queryResult.resultSet();
        try {
            return resultSet.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PlayersTable getPlayersTable() {
        return this.playersTable;
    }

    public HashMap<String, String> getTeamNames() {
        HashMap<String, String> teamNames = new HashMap<>();
        Query teamQuery = new QueryBuilder()
                .setQueryType(QueryType.SELECT)
                .setQuery(String.format("SELECT code, name FROM %s;", this.getTableName()))
                .getQuery();
        QueryResult queryResult = this.getDatabase().executeQuery(teamQuery);
        ResultSet resultSet = queryResult.resultSet();
        try{
            while(resultSet.next()){
                String code = resultSet.getString("code");
                String name = resultSet.getString("name");
                teamNames.put(code, name);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return teamNames;
    }
}
