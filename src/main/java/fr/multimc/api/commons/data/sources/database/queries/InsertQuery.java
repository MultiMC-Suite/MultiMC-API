package fr.multimc.api.commons.data.sources.database.queries;

import fr.multimc.api.commons.data.sources.database.enums.DatabaseType;
import fr.multimc.api.commons.data.sources.database.enums.QueryType;
import fr.multimc.api.commons.data.sources.database.models.Field;
import fr.multimc.api.commons.data.sources.database.models.Query;
import fr.multimc.api.commons.data.sources.database.Database;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class InsertQuery extends Query {

    private final String tableName;
    private final Map<Field, Object> values;

    public InsertQuery(@NotNull String tableName, @NotNull Map<Field, Object> values) {
        this.tableName = tableName;
        this.values = values;
    }

    @Override
    public String getQuery(@NotNull DatabaseType databaseType) {
        StringBuilder fieldsBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();
        for(Map.Entry<Field, Object> entry : this.values.entrySet()){
            fieldsBuilder.append(entry.getKey().name());
            Object value = entry.getValue();
            if(value instanceof String)
                valuesBuilder.append("'%s'".formatted(entry.getValue()));
            else
                valuesBuilder.append(entry.getValue());
            if(entry != this.values.entrySet().stream().reduce((one, two) -> two).get()){
                fieldsBuilder.append(", ");
                valuesBuilder.append(", ");
            }
        }
        return "INSERT INTO %s (%s) VALUES (%s);".formatted(this.tableName, fieldsBuilder, valuesBuilder);
    }

    @Override
    public QueryResult execute(@NotNull Database database) {
        return this.execute(database, QueryType.UPDATE);
    }
}
