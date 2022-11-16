package fr.multimc.api.commons.database.enums;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public enum Property {
    AUTO_INCREMENT("AUTO_INCREMENT", "AUTO_INCREMENT"),
    NOT_NULL("NOT NULL", "NOT NULL"),
    UNIQUE("UNIQUE", "UNIQUE");

    private final String mysql;
    private final String sqlite;

    Property(@NotNull String mysql, @NotNull String sqlite) {
        this.mysql = mysql;
        this.sqlite = sqlite;
    }

    public String getProperty(DatabaseType databaseType){
        return switch (databaseType) {
            case MYSQL -> this.mysql;
            case SQLITE -> this.sqlite;
        };
    }

    public String getMysql() {
        return mysql;
    }
    public String getSqlite() {
        return sqlite;
    }
}
