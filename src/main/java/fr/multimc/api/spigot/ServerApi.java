package fr.multimc.api.spigot;

import fr.multimc.api.spigot.samplecode.database.DatabaseSampleCode;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerApi extends JavaPlugin {

    private static JavaPlugin instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        new DatabaseSampleCode().run(this);
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}
