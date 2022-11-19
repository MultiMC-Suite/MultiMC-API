package fr.multimc.api.commons.database.query;

import fr.multimc.api.commons.database.enums.DatabaseType;
import fr.multimc.api.commons.database.interfaces.IConstraint;
import fr.multimc.api.commons.database.models.Query;
import fr.multimc.api.commons.database.models.Field;
import fr.multimc.api.commons.database.enums.FieldType;
import fr.multimc.api.commons.database.enums.Property;
import fr.multimc.api.commons.database.models.constraints.PrimaryKeyConstraint;
import fr.multimc.api.commons.old_database.Database;
import fr.multimc.api.commons.old_database.query.QueryResult;
import fr.multimc.api.commons.old_database.query.QueryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateTableQuery extends Query {

    private final String tableName;
    private final List<Field> fields;
    private final List<IConstraint> constraints;

    public CreateTableQuery(@NotNull String tableName, @NotNull List<Field> fields, @Nullable List<IConstraint> constraints, boolean autoId) {
        this.tableName = tableName;
        this.fields = fields;
        this.constraints = Objects.isNull(constraints) ? new ArrayList<>() : new ArrayList<>(constraints);
        if(autoId){
            Field idField = new Field("id", FieldType.INTEGER, Property.AUTO_INCREMENT);
            this.fields.add(0, idField);
            this.constraints.add(new PrimaryKeyConstraint("pk_%s".formatted(tableName), idField));
        }
    }

    @Override
    public String getQuery(@NotNull DatabaseType databaseType) {
        StringBuilder fieldsBuilder = new StringBuilder();
        // Serialize fields
        for (int i = 0; i < this.fields.size(); i++) {
            fieldsBuilder.append(this.fields.get(i).getField(databaseType));
            if (i != this.fields.size() - 1 || this.constraints.size() != 0) {
                fieldsBuilder.append(", ");
            }
        }
        // Serialize constraints
        for (int i = 0; i < this.constraints.size(); i++) {
            fieldsBuilder.append(this.constraints.get(i).getConstraint());
            if (i != this.constraints.size() - 1) {
                fieldsBuilder.append(", ");
            }
        }
        return String.format("CREATE TABLE %s (%s);", tableName, fieldsBuilder);
    }

    @Override
    public QueryResult execute(@NotNull Database database) {
        return this.execute(database, QueryType.UPDATE);
    }


}
