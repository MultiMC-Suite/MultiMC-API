package fr.multimc.api.commons.data.sources.database.models;

import fr.multimc.api.commons.data.sources.database.enums.DatabaseType;
import fr.multimc.api.commons.data.sources.database.enums.QueryType;
import fr.multimc.api.commons.data.sources.database.Database;
import fr.multimc.api.commons.data.sources.database.queries.QueryResult;
import org.jetbrains.annotations.NotNull;

public abstract class Query {

    /**
     * Get a SQL query from an object that extends Query
     * @param databaseType Database type, can be SQLITE or MYSQL
     * @return String that represent a SQL query
     */
    public abstract String getQuery(@NotNull DatabaseType databaseType);

    /**
     * Execute a query
     * @param database Database instance
     * @return QueryResult
     */
    public abstract QueryResult execute(@NotNull Database database);

    protected QueryResult execute(@NotNull Database database, @NotNull QueryType queryType){
        return database.executeQuery(this.getQuery(database.getDatabaseType()), queryType);
    }

}
