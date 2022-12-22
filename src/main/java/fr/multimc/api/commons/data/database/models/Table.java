package fr.multimc.api.commons.data.database.models;

import fr.multimc.api.commons.data.database.Database;
import fr.multimc.api.commons.data.database.enums.FieldType;
import fr.multimc.api.commons.data.database.enums.Property;
import fr.multimc.api.commons.data.database.enums.SQLState;
import fr.multimc.api.commons.data.database.interfaces.IConstraint;
import fr.multimc.api.commons.data.database.models.constraints.PrimaryKeyConstraint;
import fr.multimc.api.commons.data.database.queries.CreateTableQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class Table {

    private final Database database;
    private final String name;
    private final List<Field> fields;
    private final List<IConstraint> constraints;

    /**
     * Constructor for SQL table
     * @param database Database object
     * @param name Table name
     * @param fields Field objects that represent SQL columns
     * @param constraints Constraint objects that represent SQL constraints
     * @param autoId If true, add an auto-incremented primary key named "id"
     */
    public Table(@NotNull Database database, @NotNull String name, @NotNull List<Field> fields, @Nullable List<IConstraint> constraints, boolean autoId) {
        this.database = database;
        this.name = name;
        this.fields = fields;
        this.constraints = Objects.isNull(constraints) ? new ArrayList<>() : constraints;
        if(autoId){
            Field idField = new Field("id", FieldType.INTEGER, Property.AUTO_INCREMENT);
            this.fields.add(0, idField);
            this.constraints.add(new PrimaryKeyConstraint("pk_%s".formatted(name), idField));
        }
        if(database.isTableExist(name) == SQLState.TABLE_NOT_EXIST){
            new CreateTableQuery(name, fields, constraints).execute(database);
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
