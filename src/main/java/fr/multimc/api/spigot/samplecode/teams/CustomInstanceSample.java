package fr.multimc.api.spigot.samplecode.teams;

import fr.multimc.api.commons.managers.game.instances.Instance;
import fr.multimc.api.commons.managers.game.instances.InstanceManager;
import fr.multimc.api.commons.managers.game.instances.InstanceSettings;
import fr.multimc.api.commons.managers.teammanager.Team;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CustomInstanceSample extends Instance {
    public CustomInstanceSample(JavaPlugin plugin, InstanceManager instanceManager, int instanceId, InstanceSettings settings, Location instanceLocation, List<Team> teams) {
        super(plugin, instanceManager, instanceId, settings, instanceLocation, teams);
        plugin.getLogger().info(String.format("Instance %d created", instanceId));
    }

    @Override
    public void tick() {

    }
}
