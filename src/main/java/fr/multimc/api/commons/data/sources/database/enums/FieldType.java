package fr.multimc.api.commons.data.sources.database.enums;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public enum FieldType {
    INTEGER("INT"),
    BIGINT("BIGINT"),
    VARCHAR("VARCHAR", 30);

    private final String mysql;
    private final String sqlite;
    private final int defaultSize;

    FieldType(@NotNull String field) {
        this(field, field, -1);
    }

    FieldType(@NotNull String field, int defaultSize) {
        this(field, field, defaultSize);
    }

    FieldType(@NotNull String mysql, @NotNull String sqlite) {
        this(mysql, sqlite, -1);
    }

    FieldType(@NotNull String mysql, @NotNull String sqlite, int defaultSize) {
        this.mysql = mysql;
        this.sqlite = sqlite;
        this.defaultSize = defaultSize;
    }

    @Nullable
    public String getField(DatabaseType type){
        switch (type) {
            case MYSQL -> {
                if (this.defaultSize != -1)
                    return this.mysql + "(" + this.defaultSize + ")";
                return this.mysql;
            }
            case SQLITE -> {
                if (this.defaultSize != -1)
                    return this.sqlite + "(" + this.defaultSize + ")";
                return this.sqlite;
            }
            default -> {
                return null;
            }
        }
    }

    @Nullable
    public String getField(DatabaseType type, int size){
        switch (type) {
            case MYSQL -> {
                if (this.defaultSize != -1)
                    return this.mysql + "(" + size + ")";
                return this.mysql;
            }
            case SQLITE -> {
                if (this.defaultSize != -1)
                    return this.sqlite + "(" + size + ")";
                return this.sqlite;
            }
            default -> {
                return null;
            }
        }
    }

    public String getMysql() {
        return mysql;
    }
    public String getSqlite() {
        return sqlite;
    }
    public int getDefaultSize() {
        return defaultSize;
    }
}
