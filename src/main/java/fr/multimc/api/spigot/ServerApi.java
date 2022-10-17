package fr.multimc.api.spigot;

import fr.multimc.api.spigot.samplecode.teams.TeamSampleCode;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
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
        new TeamSampleCode().run(this);
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}
