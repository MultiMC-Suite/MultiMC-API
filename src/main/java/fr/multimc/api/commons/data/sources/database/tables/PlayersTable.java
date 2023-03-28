package fr.multimc.api.commons.data.sources.database.tables;

import fr.multimc.api.commons.data.sources.database.Database;
import fr.multimc.api.commons.data.sources.database.enums.FieldType;
import fr.multimc.api.commons.data.sources.database.enums.Property;
import fr.multimc.api.commons.data.sources.database.interfaces.IConstraint;
import fr.multimc.api.commons.data.sources.database.models.Field;
import fr.multimc.api.commons.data.sources.database.models.Table;
import fr.multimc.api.commons.data.sources.database.models.constraints.ForeignKeyConstraint;
import fr.multimc.api.commons.data.sources.database.models.constraints.PrimaryKeyConstraint;
import fr.multimc.api.commons.data.sources.database.queries.InsertQuery;
import fr.multimc.api.commons.data.sources.database.queries.QueryResult;
import fr.multimc.api.commons.data.sources.database.queries.SelectQuery;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused"})
public class PlayersTable extends Table {

    // Fields
    public static final String name = "players";
    public static final Field usernameField = new Field("username", FieldType.VARCHAR, 30, Property.NOT_NULL, Property.UNIQUE);
    public static final Field teamCodeField = new Field("team_code", FieldType.VARCHAR, 6);
    public static final IConstraint pkConstraint = new PrimaryKeyConstraint("pk_players", usernameField);
    public static final IConstraint fkConstraint = new ForeignKeyConstraint("fk_players", teamCodeField, TeamsTable.name, TeamsTable.codeField);

    public PlayersTable(@NotNull Database database) {
        super(database, name, List.of(usernameField, teamCodeField), List.of(pkConstraint, fkConstraint), false);
    }

    public void addPlayer(String teamCode, String playerName){
        InsertQuery insertQuery = new InsertQuery(this.getName(), new HashMap<>() {{
            put(teamCodeField, teamCode);
            put(usernameField, playerName);
        }});
        insertQuery.execute(this.getDatabase());
    }

    public void addPlayers(String teamCode, String... playersName){
        for(String playerName : playersName){
            this.addPlayer(teamCode, playerName);
        }
    }

    public Map<String, List<String>> getPlayersByTeam(){
        SelectQuery selectQuery = new SelectQuery(this.getName(), null, null, usernameField, teamCodeField);
        HashMap<String, List<String>> teams = new HashMap<>();
        try (ResultSet resultSet = selectQuery.execute(this.getDatabase()).resultSet()) {
            while (resultSet.next()) {
                String teamCode = resultSet.getString(teamCodeField.name());
                if (!teams.containsKey(teamCode)) {
                    teams.put(teamCode, new ArrayList<>());
                }
                teams.get(teamCode).add(resultSet.getString(usernameField.name()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teams;
    }

    @Deprecated
    public List<String> getTeamMembersNames(String teamCode){
        SelectQuery selectQuery = new SelectQuery(this.getName(), "%s='%s'".formatted(teamCodeField.name(), teamCode), null, usernameField);
        QueryResult queryResult = selectQuery.execute(this.getDatabase());
        List<String> playersName = null;
        try (ResultSet resultSet = queryResult.resultSet()) {
            playersName = new ArrayList<>();
            while (resultSet.next()) {
                playersName.add(resultSet.getString(usernameField.name()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playersName;
    }


}
