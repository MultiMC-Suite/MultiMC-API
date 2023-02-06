package fr.multimc.api.commons.data.sources.database.queries;

import fr.multimc.api.commons.data.sources.database.Database;
import fr.multimc.api.commons.data.sources.database.enums.DatabaseType;
import fr.multimc.api.commons.data.sources.database.enums.QueryType;
import fr.multimc.api.commons.data.sources.database.models.Field;
import fr.multimc.api.commons.data.sources.database.models.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SelectQuery extends Query {

    private final String tableName;
    private final String whereClause;
    private final String orderByClause;
    private final Field[] targetFields;

    public SelectQuery(@NotNull String tableName, @Nullable String whereClause, @Nullable String orderByClause, Field... targetFields) {
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
        if(Objects.nonNull(this.orderByClause))
            queryBuilder.append(" ORDER BY %s".formatted(this.orderByClause));
        queryBuilder.append(";");
        return queryBuilder.toString();
    }

    @Override
    public QueryResult execute(@NotNull Database database) {
        return this.execute(database, QueryType.SELECT);
    }
}
