package fr.multimc.api.spigot.pre_made.samplecode.database;

import fr.multimc.api.commons.data.database.Database;
import fr.multimc.api.spigot.pre_made.samplecode.SampleCode;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@SuppressWarnings("unused")
public class DatabaseSampleCode implements SampleCode {
    @Override
    public void run(JavaPlugin plugin) {
        Database database = new Database(new File(plugin.getDataFolder().getPath() + "/database.db"), plugin.getLogger());
        CustomTableSample customTableSample = new CustomTableSample(database);
        customTableSample.addContent("value1", "value2");
        System.out.println(customTableSample.getValue2FromValue1("value1"));
    }
}
