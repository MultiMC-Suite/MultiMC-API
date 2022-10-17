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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class TeamsTable extends Table {

    private final PlayersTable playersTable;

    public TeamsTable(@NotNull Database database) {
        super(database, "multimc_teams", "id INT PRIMARY KEY AUTO_INCREMENT", "teamName VARCHAR(30) UNIQUE", "points INT");

        this.playersTable = new PlayersTable(this.getDatabase(), "multimc_players");
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean addTeam(String teamName, String... players){
        Query teamQuery = new QueryBuilder()
                .setQueryType(QueryType.UPDATE)
                .setQuery(String.format("INSERT INTO %s VALUES (NULL, '%s', 0)", this.getTableName(), teamName))
                .getQuery();
        QueryResult queryResult = this.getDatabase().executeQuery(teamQuery);
        if(queryResult.queryStatus() == DatabaseStatus.SUCCESS){
            int teamId = this.getTeamId(teamName);
            if(teamId != -1){
                this.playersTable.addPlayers(teamId, players);
                return true;
            }
        }
        return false;
    }

    private int getTeamId(String teamName){
        Query teamQuery = new QueryBuilder()
                .setQueryType(QueryType.SELECT)
                .setQuery(String.format("SELECT id FROM %s WHERE teamName='%s'", this.getTableName(), teamName))
                .getQuery();
        QueryResult queryResult = this.getDatabase().executeQuery(teamQuery);
        ResultSet resultSet = queryResult.resultSet();
        try {
            return resultSet.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // TO TEST
    public String[] getTeamMembersNames(int teamId){
        Query teamQuery = new QueryBuilder()
                .setQueryType(QueryType.UPDATE)
                .setQuery(String.format("SELECT playerName FROM %s WHERE teamId=%d", this.playersTable.getTableName(), teamId))
                .getQuery();
        QueryResult queryResult = this.getDatabase().executeQuery(teamQuery);
        ResultSet resultSet = queryResult.resultSet();
        try {
            return new String[]{resultSet.getString(0), resultSet.getString(1)};
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TO TEST
    public HashMap<Integer, List<String>> getPlayersByTeam(){
        HashMap<Integer, List<String>> teams = new HashMap<>();
        Query teamQuery = new QueryBuilder()
                .setQueryType(QueryType.SELECT)
                .setQuery(String.format("SELECT teamId, playerName FROM %s", this.playersTable.getTableName()))
                .getQuery();
        ResultSet resultSet = this.getDatabase().executeQuery(teamQuery).resultSet();
        try{
            while(resultSet.next()){
                int teamId = resultSet.getInt("teamId");
                if(!teams.containsKey(teamId)) {
                    teams.put(teamId, new ArrayList<>());
                }
                teams.get(teamId).add(resultSet.getString("playerName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teams;
    }

    public String getTeamName(int teamId) {
        Query teamQuery = new QueryBuilder()
                .setQueryType(QueryType.SELECT)
                .setQuery(String.format("SELECT teamName FROM %s WHERE id='%s'", this.getTableName(), teamId))
                .getQuery();
        QueryResult queryResult = this.getDatabase().executeQuery(teamQuery);
        ResultSet resultSet = queryResult.resultSet();
        try {
            return resultSet.getString("teamName");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
