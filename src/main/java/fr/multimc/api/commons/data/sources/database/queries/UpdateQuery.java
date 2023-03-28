package fr.multimc.api.commons.data.sources.database.queries;

import fr.multimc.api.commons.data.sources.database.Database;
import fr.multimc.api.commons.data.sources.database.enums.DatabaseType;
import fr.multimc.api.commons.data.sources.database.enums.QueryType;
import fr.multimc.api.commons.data.sources.database.models.Field;
import fr.multimc.api.commons.data.sources.database.models.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class UpdateQuery extends Query {

    private final String tableName;
    private final Map<Field, Object> valuesMap;
    private final String whereClause;

    public UpdateQuery(@NotNull String tableName, @NotNull Map<Field, Object> valuesMap, @Nullable String whereClause) {
        this.tableName = tableName;
        this.valuesMap = Map.copyOf(valuesMap);
        this.whereClause = whereClause;
    }

    @Override
    public String getQuery(@NotNull DatabaseType databaseType) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE %s".formatted(tableName));
        StringBuilder valuesBuilder = new StringBuilder(" SET");
        for (Map.Entry<Field, Object> entry : valuesMap.entrySet()) {
            if(entry.getValue() instanceof String)
                valuesBuilder.append(" %s='%s'".formatted(entry.getKey().name(), entry.getValue()));
            else
                valuesBuilder.append(" %s=%s".formatted(entry.getKey().name(), entry.getValue()));
            if(entry != valuesMap.entrySet().stream().reduce((first, second) -> second).get()){
                valuesBuilder.append(",");
            }
        }
        queryBuilder.append(valuesBuilder);
        if(Objects.nonNull(this.whereClause))
            queryBuilder.append(" WHERE %s".formatted(this.whereClause));
        return queryBuilder.toString();
    }

    @Override
    public QueryResult execute(@NotNull Database database) {
        return this.execute(database, QueryType.UPDATE);
    }
}
