package fr.multimc.api.spigot.pre_made.samplecode.database;

import fr.multimc.api.commons.database.Table;
import fr.multimc.api.commons.database.enums.FieldType;
import fr.multimc.api.commons.database.enums.Property;
import fr.multimc.api.commons.database.models.Field;
import fr.multimc.api.commons.old_database.Database;
import fr.multimc.api.spigot.pre_made.samplecode.SampleCode;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

@SuppressWarnings("unused")
public class DatabaseSampleCode implements SampleCode {
    @Override
    public void run(JavaPlugin plugin) {
        Database database = new Database(new File(plugin.getDataFolder().getPath() + "/database.db"), plugin.getLogger());
        List<Field> fields = List.of(new Field("playerName", FieldType.VARCHAR, List.of(new Property[]{Property.NOT_NULL})), new Field("playerUUID", FieldType.INTEGER, List.of(new Property[]{Property.NOT_NULL})));
        Table table = new Table(database, "table_name", fields, null, true);
    }
}
