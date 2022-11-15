package fr.multimc.api.commons.database.interfaces;

import fr.multimc.api.commons.database.enums.DatabaseType;
import fr.multimc.api.commons.old_database.Database;
import fr.multimc.api.commons.old_database.query.QueryResult;
import org.jetbrains.annotations.NotNull;

public interface IQuery {
    String getQuery(@NotNull DatabaseType databaseType);
    QueryResult execute(@NotNull Database database);
}
