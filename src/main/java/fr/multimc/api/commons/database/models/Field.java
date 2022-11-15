package fr.multimc.api.commons.database.models;

import fr.multimc.api.commons.database.enums.DatabaseType;
import fr.multimc.api.commons.database.enums.FieldType;
import fr.multimc.api.commons.database.enums.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Field(@NotNull String name, @NotNull FieldType type, int size, @Nullable List<Property> properties) {

    public Field(@NotNull String name, @NotNull FieldType type, int size, @Nullable List<Property> properties){
        this.name = name;
        this.type = type;
        this.size = size;
        this.properties = Objects.isNull(properties) ? new ArrayList<>() : new ArrayList<>(properties);
    }

    public Field(@NotNull String name, @NotNull FieldType type, @Nullable List<Property> properties){
        this(name, type, type.getDefaultSize(), Objects.isNull(properties) ? new ArrayList<>() : new ArrayList<>(properties));
    }

    public String getField(DatabaseType databaseType){
        StringBuilder propertiesBuilder = new StringBuilder();
        assert properties != null;
        properties.forEach(property -> propertiesBuilder.append(" ").append(property.getProperty(databaseType)));
        return String.format("%s %s%s", name, type.getField(databaseType), propertiesBuilder);
    }
}
