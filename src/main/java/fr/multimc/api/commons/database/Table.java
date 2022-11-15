package fr.multimc.api.commons.database;

import fr.multimc.api.commons.database.interfaces.IConstraint;
import fr.multimc.api.commons.database.models.Field;
import fr.multimc.api.commons.database.query.CreateTableQuery;
import fr.multimc.api.commons.old_database.Database;
import fr.multimc.api.commons.old_database.enums.DatabaseStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class Table {

    private final Database database;
    private final String name;
    private final List<Field> fields;
    private final List<IConstraint> constraints;

    public Table(@NotNull Database database, @NotNull String name, @NotNull List<Field> fields, @Nullable List<IConstraint> constraints, boolean autoId) {
        this.database = database;
        this.name = name;
        this.fields = fields;
        this.constraints = constraints;
        if(database.isTableExist(name) == DatabaseStatus.TABLE_NOT_EXIST){
            new CreateTableQuery(name, fields, constraints, autoId).execute(database);
        }
    }

    public Database getDatabase() {
        return database;
    }
    public String getName() {
        return name;
    }
    public List<Field> getFields() {
        return fields;
    }
    @Nullable
    public List<IConstraint> getConstraints() {
        return constraints;
    }
}
