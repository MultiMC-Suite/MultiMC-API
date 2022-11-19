package fr.multimc.api.commons.old_database.tables;

import fr.multimc.api.commons.database.Table;
import fr.multimc.api.commons.database.enums.FieldType;
import fr.multimc.api.commons.database.enums.Property;
import fr.multimc.api.commons.database.interfaces.IConstraint;
import fr.multimc.api.commons.database.models.Field;
import fr.multimc.api.commons.database.models.constraints.PrimaryKeyConstraint;
import fr.multimc.api.commons.database.query.SelectTableQuery;
import fr.multimc.api.commons.old_database.Database;
import fr.multimc.api.commons.old_database.enums.DatabaseStatus;
import fr.multimc.api.commons.old_database.query.Query;
import fr.multimc.api.commons.old_database.query.QueryBuilder;
import fr.multimc.api.commons.old_database.query.QueryResult;
import fr.multimc.api.commons.old_database.query.QueryType;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class TeamsTable extends Table {

    // Fields
    public static final String name = "teams";
    public static final Field codeField = new Field("code", FieldType.VARCHAR, 6, Property.NOT_NULL, Property.UNIQUE);
    public static final Field nameField = new Field("name", FieldType.VARCHAR, 30, Property.NOT_NULL, Property.UNIQUE);
    public static final Field scoreField = new Field("score", FieldType.INTEGER, Property.NOT_NULL);
    public static final IConstraint pkConstraint = new PrimaryKeyConstraint("pk_teams", codeField);

    private final PlayersTable playersTable;

    public TeamsTable(@NotNull Database database) {
        super(database, name, List.of(codeField, nameField, scoreField), List.of(pkConstraint), false);

        this.playersTable = new PlayersTable(this.getDatabase(), "players");
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean addTeam(String teamCode, String teamName, String... playersName){
        Query teamQuery = new QueryBuilder()
                .setQueryType(QueryType.UPDATE)
                .setQuery(String.format("INSERT INTO %s VALUES ('%s', '%s', 0)", this.getName(), teamCode, teamName))
                .getQuery();
        QueryResult queryResult = this.getDatabase().executeQuery(teamQuery);
        if(queryResult.queryStatus() == DatabaseStatus.SUCCESS){
            this.playersTable.addPlayers(teamCode, playersName);
            return true;
        }
        return false;
    }

    private String getTeamId(String teamName){
        SelectTableQuery selectTableQuery = new SelectTableQuery(this.getName(), String.format("%s = '%s'", nameField.name(), teamName), null, codeField);
        try (ResultSet resultSet = selectTableQuery.execute(this.getDatabase()).resultSet()) {
            return resultSet.getString("code");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTeamName(String code) {
        SelectTableQuery selectTableQuery = new SelectTableQuery(this.getName(), String.format("%s = '%s'", codeField.name(), code), null, nameField);
        try (ResultSet resultSet = selectTableQuery.execute(this.getDatabase()).resultSet()) {
            try {
                return resultSet.getString("name");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PlayersTable getPlayersTable() {
        return this.playersTable;
    }

    public HashMap<String, String> getTeamNames() {
        SelectTableQuery selectTableQuery = new SelectTableQuery(this.getName(), null, null, codeField, nameField);
        HashMap<String, String> teamNames = new HashMap<>();
        try (ResultSet resultSet = selectTableQuery.execute(this.getDatabase()).resultSet()) {
            while (resultSet.next()) {
                String code = resultSet.getString("code");
                String name = resultSet.getString("name");
                teamNames.put(code, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teamNames;
    }

    public HashMap<String, Integer> getCurrentScores(){
        SelectTableQuery selectTableQuery = new SelectTableQuery(this.getName(), null, null, codeField, scoreField);
        HashMap<String, Integer> scores = new HashMap<>();
        try (ResultSet resultSet = selectTableQuery.execute(this.getDatabase()).resultSet()) {
            while (resultSet.next()) {
                String code = resultSet.getString("code");
                int score = resultSet.getInt("score");
                scores.put(code, score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    public void updateScores(HashMap<String, Integer> scores){
        StringBuilder playersQueryString = new StringBuilder();
        for(String teamCode : scores.keySet()){
            Query playerQuery = new QueryBuilder()
                    .setQueryType(QueryType.UPDATE)
                    .setQuery(String.format("UPDATE %s SET score=%d WHERE code='%s';", this.getName(), scores.get(teamCode), teamCode))
                    .getQuery();
            this.getDatabase().executeQuery(playerQuery);
        }
    }
}
