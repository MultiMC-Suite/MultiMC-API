package fr.multimc.api.commons.managers.teammanager.database;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.commons.database.Table;
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
public class PlayersTable extends Table {
    public PlayersTable(@NotNull Database database, String name) {
        super(database, name, "username VARCHAR(16) PRIMARY KEY", "team_code VARCHAR(6)");
    }

    public void addPlayer(String playerName, String teamId){
        Query playerQuery = new QueryBuilder()
                .setQueryType(QueryType.UPDATE)
                .setQuery(String.format("INSERT INTO %s VALUES ('%s', '%s');", this.getTableName(), playerName, teamId))
                .getQuery();
        this.getDatabase().executeQuery(playerQuery);
    }

    public void addPlayers(String teamCode, String... players){
        StringBuilder playersQueryString = new StringBuilder();
        for(String player: players){
            playersQueryString.append(String.format("INSERT INTO %s VALUES ('%s', '%s');", this.getTableName(), player, teamCode));
        }
        Query playerQuery = new QueryBuilder()
                .setQueryType(QueryType.UPDATE)
                .setQuery(playersQueryString.toString())
                .getQuery();
        this.getDatabase().executeQuery(playerQuery);
    }

    public List<String> getTeamMembersNames(String teamCode){
        Query teamQuery = new QueryBuilder()
                .setQueryType(QueryType.UPDATE)
                .setQuery(String.format("SELECT username FROM %s WHERE team_code='%s';", this.getTableName(), teamCode))
                .getQuery();
        QueryResult queryResult = this.getDatabase().executeQuery(teamQuery);
        ResultSet resultSet = queryResult.resultSet();
        List<String> playersName = new ArrayList<>();
        try{
            while(resultSet.next()){
                playersName.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playersName;
    }

    public HashMap<String, List<String>> getPlayersByTeam(){
        HashMap<String, List<String>> teams = new HashMap<>();
        Query teamQuery = new QueryBuilder()
                .setQueryType(QueryType.SELECT)
                .setQuery(String.format("SELECT username, team_code FROM %s;", this.getTableName()))
                .getQuery();
        ResultSet resultSet = this.getDatabase().executeQuery(teamQuery).resultSet();
        try{
            while(resultSet.next()){
                String teamCode = resultSet.getString("team_code");
                if(!teams.containsKey(teamCode)) {
                    teams.put(teamCode, new ArrayList<>());
                }
                teams.get(teamCode).add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teams;
    }
}
