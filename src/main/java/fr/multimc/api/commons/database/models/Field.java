package fr.multimc.api.commons.database.models;

import fr.multimc.api.commons.database.enums.DatabaseType;
import fr.multimc.api.commons.database.enums.FieldType;
import fr.multimc.api.commons.database.enums.Property;

import java.util.List;

public record Field(String name, FieldType type, int size, List<Property> properties) {

    public Field(String name, FieldType type, List<Property> properties){
        this(name, type, type.getDefaultSize(), properties);
    }

    public String getField(DatabaseType databaseType){
        StringBuilder propertiesBuilder = new StringBuilder();
        properties.forEach(property -> propertiesBuilder.append(" ").append(property.getProperty(databaseType)));
        return String.format("%s %s%s", name, type.getField(databaseType), propertiesBuilder);
    }
}
