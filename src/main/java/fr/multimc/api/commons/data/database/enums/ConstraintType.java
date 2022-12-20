package fr.multimc.api.commons.data.database.enums;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public enum ConstraintType {

    PRIMARY_KEY("PRIMARY_KEY", "PRIMARY KEY");

    private final String mysql;
    private final String sqlite;

    ConstraintType(@NotNull String mysql, @NotNull String sqlite) {
        this.mysql = mysql;
        this.sqlite = sqlite;
    }

    public String getMysql() {
        return mysql;
    }
    public String getSqlite() {
        return sqlite;
    }
}
