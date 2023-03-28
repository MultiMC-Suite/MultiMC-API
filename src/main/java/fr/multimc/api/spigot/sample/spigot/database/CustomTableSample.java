package fr.multimc.api.spigot.sample.spigot.database;

import fr.multimc.api.commons.data.sources.database.Database;
import fr.multimc.api.commons.data.sources.database.enums.FieldType;
import fr.multimc.api.commons.data.sources.database.enums.Property;
import fr.multimc.api.commons.data.sources.database.interfaces.IConstraint;
import fr.multimc.api.commons.data.sources.database.models.Field;
import fr.multimc.api.commons.data.sources.database.models.Table;
import fr.multimc.api.commons.data.sources.database.models.constraints.PrimaryKeyConstraint;
import fr.multimc.api.commons.data.sources.database.queries.InsertQuery;
import fr.multimc.api.commons.data.sources.database.queries.QueryResult;
import fr.multimc.api.commons.data.sources.database.queries.SelectQuery;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"unused", "resource"})
public class CustomTableSample extends Table {

    public static final String name = "multimc_sample";
    public static final Field idField = new Field("id", FieldType.INTEGER, Property.AUTO_INCREMENT);
    public static final Field value1Field = new Field("value1", FieldType.VARCHAR, 20, Property.NOT_NULL, Property.UNIQUE);
    public static final Field value2Field = new Field("value2", FieldType.VARCHAR, 20, Property.NOT_NULL, Property.UNIQUE);

    public static final IConstraint primaryKey = new PrimaryKeyConstraint("pk_multimc_sample", idField);

    public CustomTableSample(@NotNull Database database) {
        super(database, name, List.of(idField, value1Field, value2Field), List.of(primaryKey), false);
    }

    @SuppressWarnings("UnusedReturnValue")
    public QueryResult addContent(String value1, String value2){
        InsertQuery insertQuery = new InsertQuery(name, new HashMap<>() {{
            put(value1Field, value1);
            put(value2Field, value2);
        }});
        return insertQuery.execute(getDatabase());
    }

    public String getValue2FromValue1(String value1){
        SelectQuery selectQuery = new SelectQuery(name, "%s='%s'".formatted(value1Field.name(), value1), null, value2Field);
        ResultSet rs = selectQuery.execute(getDatabase()).resultSet();
        try {
            return rs.getString(value2Field.name());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
