package fr.multimc.api.spigot;

import fr.multimc.api.spigot.pre_made.commands.RelativeToCommand;
import fr.multimc.api.spigot.pre_made.samplecode.instances.InstanceSampleCode;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings({"unused", "ConstantConditions"})
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
        this.getCommand("relativeto").setExecutor(new RelativeToCommand());
        new InstanceSampleCode().run(this);
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}
