package fr.multimc.api.spigot;

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

    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}