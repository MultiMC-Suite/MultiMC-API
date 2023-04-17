package fr.multimc.api.commons.data.sources.hibernate.enums;

import org.hibernate.community.dialect.SQLiteDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;

public enum DatabaseType {

    SQLITE(SQLiteDialect.class),
    MYSQL(MySQLDialect .class);

    private final Class<? extends Dialect> dialect;

    DatabaseType(Class<? extends Dialect> dialect){
        this.dialect = dialect;
    }

    public Class<? extends Dialect> getDialect() {
        return dialect;
    }
}
