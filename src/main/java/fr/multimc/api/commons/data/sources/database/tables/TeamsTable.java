package fr.multimc.api.commons.data.sources.database.tables;

import fr.multimc.api.commons.data.sources.database.Database;
import fr.multimc.api.commons.data.sources.database.enums.FieldType;
import fr.multimc.api.commons.data.sources.database.enums.Property;
import fr.multimc.api.commons.data.sources.database.interfaces.IConstraint;
import fr.multimc.api.commons.data.sources.database.models.Field;
import fr.multimc.api.commons.data.sources.database.models.Table;
import fr.multimc.api.commons.data.sources.database.models.constraints.PrimaryKeyConstraint;
import fr.multimc.api.commons.data.sources.database.queries.InsertQuery;
import fr.multimc.api.commons.data.sources.database.queries.QueryResult;
import fr.multimc.api.commons.data.sources.database.queries.SelectQuery;
import fr.multimc.api.commons.data.sources.database.queries.UpdateQuery;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class TeamsTable extends Table {

    // Fields
    public static final String name = "teams";
    public static final Field codeField = new Field("code", FieldType.VARCHAR, 6, Property.NOT_NULL, Property.UNIQUE);
    public static final Field nameField = new Field("name", FieldType.VARCHAR, 30, Property.NOT_NULL, Property.UNIQUE);
    public static final Field scoreField = new Field("score", FieldType.INTEGER, Property.NOT_NULL);
    public static final IConstraint pkConstraint = new PrimaryKeyConstraint("pk_teams", codeField);

    public TeamsTable(@NotNull Database database) {
        super(database, name, List.of(codeField, nameField, scoreField), List.of(pkConstraint), false);
    }

    public QueryResult addTeam(String teamCode, String teamName, String... playersName){
        InsertQuery insertQuery = new InsertQuery(this.getName(), new HashMap<>() {{
            put(codeField, teamCode);
            put(nameField, teamName);
            put(scoreField, 0);
        }});
        return insertQuery.execute(this.getDatabase());
    }

    @Deprecated
    private String getTeamId(String teamName){
        SelectQuery selectQuery = new SelectQuery(this.getName(), "%s='%s'".formatted(nameField.name(), teamName), null, codeField);
        try (ResultSet resultSet = selectQuery.execute(this.getDatabase()).resultSet()) {
            return resultSet.getString(codeField.name());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Deprecated
    public String getTeamName(String teamCode) {
        SelectQuery selectQuery = new SelectQuery(this.getName(), "%s='%s'".formatted(codeField.name(), teamCode), null, nameField);
        try (ResultSet resultSet = selectQuery.execute(this.getDatabase()).resultSet()) {
            try {
                return resultSet.getString(nameField.name());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getTeamNames() {
        SelectQuery selectQuery = new SelectQuery(this.getName(), null, null, codeField, nameField);
        Map<String, String> teamNames = new HashMap<>();
        try (ResultSet resultSet = selectQuery.execute(this.getDatabase()).resultSet()) {
            while (resultSet.next()) {
                String code = resultSet.getString(codeField.name());
                String name = resultSet.getString(nameField.name());
                teamNames.put(code, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teamNames;
    }

    public Map<String, Integer> getScores(){
        SelectQuery selectQuery = new SelectQuery(this.getName(), null, null, codeField, scoreField);
        Map<String, Integer> scores = new HashMap<>();
        try (ResultSet resultSet = selectQuery.execute(this.getDatabase()).resultSet()) {
            while (resultSet.next()) {
                String code = resultSet.getString(codeField.name());
                int score = resultSet.getInt(scoreField.name());
                scores.put(code, score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    public void setScores(Map<String, Integer> scores){
        StringBuilder playersQueryString = new StringBuilder();
        for(Map.Entry<String, Integer> entry : scores.entrySet()){
            setScore(entry.getKey(), entry.getValue());
        }
    }

    private void setScore(String teamCode, int score){
        UpdateQuery updateQuery = new UpdateQuery(this.getName(), new HashMap<>() {{
            put(scoreField, score);
        }}, "%s='%s'".formatted(codeField.name(), teamCode));
        updateQuery.execute(this.getDatabase());
    }
}
