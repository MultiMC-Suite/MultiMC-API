package fr.multimc.api.commons.data.database.models.constraints;

import fr.multimc.api.commons.data.database.models.Field;
import fr.multimc.api.commons.data.database.interfaces.IConstraint;

@SuppressWarnings("unused")
public class ForeignKeyConstraint implements IConstraint {

    private final String constraintName;
    private final Field localField;
    private final String targetTableName;
    private final Field targetField;

    public ForeignKeyConstraint(String constraintName, Field localField, String targetTableName, Field targetFieldName) {
        this.constraintName = constraintName;
        this.localField = localField;
        this.targetTableName = targetTableName;
        this.targetField = targetFieldName;
    }

    @Override
    public String getConstraint() {
        return "CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s (%s)".formatted(constraintName, localField.name(), targetTableName, targetField.name());
    }
}
