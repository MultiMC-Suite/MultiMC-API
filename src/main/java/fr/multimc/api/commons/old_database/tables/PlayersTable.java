package fr.multimc.api.commons.old_database.tables;

import fr.multimc.api.commons.database.Table;
import fr.multimc.api.commons.database.enums.FieldType;
import fr.multimc.api.commons.database.enums.Property;
import fr.multimc.api.commons.database.interfaces.IConstraint;
import fr.multimc.api.commons.database.models.Field;
import fr.multimc.api.commons.database.models.constraints.ForeignKeyConstraint;
import fr.multimc.api.commons.database.models.constraints.PrimaryKeyConstraint;
import fr.multimc.api.commons.database.query.SelectTableQuery;
import fr.multimc.api.commons.old_database.Database;
import fr.multimc.api.commons.old_database.query.Query;
import fr.multimc.api.commons.old_database.query.QueryBuilder;
import fr.multimc.api.commons.old_database.query.QueryResult;
import fr.multimc.api.commons.old_database.query.QueryType;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class PlayersTable extends Table {

    // Fields
    public static final String name = "players";
    public static final Field teamCodeField = new Field("team_code", FieldType.VARCHAR, 6);
    public static final Field usernameField = new Field("username", FieldType.VARCHAR, 30, Property.NOT_NULL, Property.UNIQUE);
    public static final IConstraint pkConstraint = new PrimaryKeyConstraint("pk_players", usernameField);
    public static final IConstraint fkConstraint = new ForeignKeyConstraint("fk_players", teamCodeField, "teams", "code");

    public PlayersTable(@NotNull Database database, String name) {
        super(database, name, List.of(teamCodeField, usernameField), List.of(pkConstraint, fkConstraint), false);
    }

    public void addPlayer(String playerName, String teamId){
        Query playerQuery = new QueryBuilder()
                .setQueryType(QueryType.UPDATE)
                .setQuery(String.format("INSERT INTO %s VALUES ('%s', '%s');", this.getName(), playerName, teamId))
                .getQuery();
        this.getDatabase().executeQuery(playerQuery);
    }

    public void addPlayers(String teamCode, String... players){
        StringBuilder playersQueryString = new StringBuilder();
        for(String player: players){
            playersQueryString.append(String.format("INSERT INTO %s (username, team_code) VALUES ('%s', '%s');", this.getName(), player, teamCode));
        }
        Query playerQuery = new QueryBuilder()
                .setQueryType(QueryType.UPDATE)
                .setQuery(playersQueryString.toString())
                .getQuery();
        this.getDatabase().executeQuery(playerQuery);
    }

    public List<String> getTeamMembersNames(String teamCode){
        SelectTableQuery selectTableQuery = new SelectTableQuery(this.getName(), String.format("%s = '%s'", teamCodeField.name(), teamCode), null, usernameField);
        QueryResult queryResult = selectTableQuery.execute(this.getDatabase());
        List<String> playersName = null;
        try (ResultSet resultSet = queryResult.resultSet()) {
            playersName = new ArrayList<>();
            while (resultSet.next()) {
                playersName.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playersName;
    }

    public HashMap<String, List<String>> getPlayersByTeam(){
        SelectTableQuery selectTableQuery = new SelectTableQuery(this.getName(), null, null, usernameField, teamCodeField);
        HashMap<String, List<String>> teams = new HashMap<>();
        try (ResultSet resultSet = selectTableQuery.execute(this.getDatabase()).resultSet()) {
            while (resultSet.next()) {
                String teamCode = resultSet.getString("team_code");
                if (!teams.containsKey(teamCode)) {
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
