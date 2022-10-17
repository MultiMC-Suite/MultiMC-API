package fr.multimc.api.commons.managers.teammanager.database;

import fr.multimc.api.commons.database.Database;
import fr.multimc.api.commons.database.Table;
import fr.multimc.api.commons.database.query.Query;
import fr.multimc.api.commons.database.query.QueryBuilder;
import fr.multimc.api.commons.database.query.QueryType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PlayersTable extends Table {
    public PlayersTable(@NotNull Database database, String name) {
        super(database, name, "id INT PRIMARY KEY AUTO_INCREMENT", "playerName VARCHAR(30) UNIQUE", "teamId INT");
    }

    public void addPlayer(String playerName, int teamId){
        Query playerQuery = new QueryBuilder()
                .setQueryType(QueryType.UPDATE)
                .setQuery(String.format("INSERT INTO %s VALUES (NULL, '%s', %d)", this.getTableName(), playerName, teamId))
                .getQuery();
        this.getDatabase().executeQuery(playerQuery);
    }

    public void addPlayers(int teamId, String... players){
        StringBuilder playersQueryString = new StringBuilder();
        for(String player: players){
            playersQueryString.append(String.format("INSERT INTO %s VALUES (NULL, '%s', %d);", this.getTableName(), player, teamId));
        }
        Query playerQuery = new QueryBuilder()
                .setQueryType(QueryType.UPDATE)
                .setQuery(playersQueryString.toString())
                .getQuery();
        this.getDatabase().executeQuery(playerQuery);
    }
}
