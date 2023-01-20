package fr.multimc.api.commons.data.sources.database.enums;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public enum Property {
    AUTO_INCREMENT("AUTO_INCREMENT"),
    NOT_NULL("NOT NULL"),
    UNIQUE("UNIQUE");

    private final String property;

    Property(@NotNull String property) {
        this.property = property;
    }

    public String getProperty(DatabaseType databaseType){
        return property;
    }
}
