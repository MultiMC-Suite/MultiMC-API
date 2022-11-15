package fr.multimc.api.spigot.pre_made.samplecode.database;

import fr.multimc.api.commons.old_database.Database;
import fr.multimc.api.commons.old_database.Table;
import fr.multimc.api.commons.old_database.enums.DatabaseStatus;
import fr.multimc.api.commons.old_database.query.QueryBuilder;
import fr.multimc.api.commons.old_database.query.QueryResult;
import fr.multimc.api.commons.old_database.query.QueryType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class CustomTableSample extends Table {
    public CustomTableSample(@NotNull Database database, String name) {
        super(database, name);
    }

    public CustomTableSample(@NotNull Database database, String name, String... fields) {
        super(database, name, fields);
    }

    public CustomTableSample(@NotNull Database database, String name, String fields) {
        super(database, name, fields);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean addContent(String content1, String content2){
        QueryResult queryResult = this.getDatabase().executeQuery(new QueryBuilder(QueryType.UPDATE,
                String.format("INSERT INTO %s VALUES (NULL, '%s', '%s');", this.getTableName(), content1, content2))
                .getQuery());
        return queryResult.queryStatus() == DatabaseStatus.SUCCESS;
    }
}
