package fr.multimc.api.commons.database.models;

import fr.multimc.api.commons.database.enums.DatabaseType;
import fr.multimc.api.commons.database.enums.FieldType;
import fr.multimc.api.commons.database.enums.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public record Field(@NotNull String name, @NotNull FieldType type, int size, @NotNull Property... properties) {

    public Field(@NotNull String name, @NotNull FieldType type, @NotNull Property... properties){
        this(name, type, type.getDefaultSize(), properties);
    }

    public String getField(DatabaseType databaseType){
        StringBuilder propertiesBuilder = new StringBuilder();
        Arrays.asList(properties).forEach(property -> propertiesBuilder.append(" ").append(property.getProperty(databaseType)));
        return "%s %s%s".formatted(name, type.getField(databaseType), propertiesBuilder);
    }
}
