package fr.multimc.api.commons.database.models.constraints;

import fr.multimc.api.commons.database.interfaces.IConstraint;
import fr.multimc.api.commons.database.models.Field;

public class PrimaryKeyConstraint implements IConstraint {

    private final String name;
    private final Field[] fields;

    public PrimaryKeyConstraint(String name, Field... fields) {
        this.name = name;
        this.fields = fields;
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
        return String.format("CONSTRAINT %s PRIMARY KEY (%s)", name, builder);
    }
}
