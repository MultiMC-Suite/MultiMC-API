package fr.multimc.api.commons.database.query;

import fr.multimc.api.commons.database.enums.DatabaseType;
import fr.multimc.api.commons.database.interfaces.IQuery;
import fr.multimc.api.commons.database.models.Field;
import fr.multimc.api.commons.old_database.Database;
import fr.multimc.api.commons.old_database.query.QueryResult;
import fr.multimc.api.commons.old_database.query.QueryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SelectTableQuery implements IQuery {

    private final String tableName;
    private final String whereClause;
    private final String orderByClause;
    private final Field[] targetFields;

    public SelectTableQuery(@NotNull String tableName, @Nullable String whereClause, @Nullable String orderByClause, Field... targetFields) {
        this.tableName = tableName;
        this.whereClause = whereClause;
        this.orderByClause = orderByClause;
        this.targetFields = targetFields;
    }

    @Override
    public String getQuery(@NotNull DatabaseType databaseType) {
        StringBuilder fieldsBuilder = new StringBuilder();
        for (int i = 0; i < this.targetFields.length; i++) {
            fieldsBuilder.append(this.targetFields[i].name());
            if (i != this.targetFields.length - 1) {
                fieldsBuilder.append(", ");
            }
        }
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT %s FROM %s".formatted(fieldsBuilder, this.tableName));
        if(Objects.nonNull(this.whereClause))
            queryBuilder.append(" WHERE %s".formatted(this.whereClause));
        if(!Objects.isNull(this.orderByClause))
            queryBuilder.append(" ORDER BY %s".formatted(this.orderByClause));
        queryBuilder.append(";");
        return queryBuilder.toString();
    }

    @Override
    public QueryResult execute(@NotNull Database database) {
        return database.executeQuery(this.getQuery(database.getDatabaseType()), QueryType.SELECT);
    }
}
