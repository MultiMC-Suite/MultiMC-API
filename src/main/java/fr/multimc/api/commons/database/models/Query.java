package fr.multimc.api.commons.database.models;

import fr.multimc.api.commons.database.enums.DatabaseType;
import fr.multimc.api.commons.old_database.Database;
import fr.multimc.api.commons.old_database.query.QueryResult;
import fr.multimc.api.commons.old_database.query.QueryType;
import org.jetbrains.annotations.NotNull;

public abstract class Query {

    public abstract String getQuery(@NotNull DatabaseType databaseType);

    protected QueryResult execute(@NotNull Database database, QueryType queryType){
        return database.executeQuery(this.getQuery(database.getDatabaseType()), queryType);
    }

}
