package fr.multimc.api.commons.data.sources.database.models.constraints;

import fr.multimc.api.commons.data.sources.database.interfaces.IConstraint;
import fr.multimc.api.commons.data.sources.database.models.Field;

import java.util.Arrays;

public class PrimaryKeyConstraint implements IConstraint {

    private final String constraintName;
    private final Field[] fields;

    public PrimaryKeyConstraint(String constraintName, Field... fields) {
        this.constraintName = constraintName;
        this.fields = Arrays.copyOf(fields, fields.length);
    }

    @Override
    public String getConstraint() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.fields.length; i++) {
            builder.append(this.fields[i].name());
            if (i != this.fields.length - 1) {
                builder.append(", ");
            }
        }
        return "CONSTRAINT %s PRIMARY KEY (%s)".formatted(constraintName, builder);
    }
}
