package fr.multimc.api.spigot.samplecode.teams;

import fr.multimc.api.spigot.managers.games.instances.Instance;
import fr.multimc.api.spigot.managers.games.instances.InstancesManager;
import fr.multimc.api.spigot.managers.games.instances.InstanceSettings;
import fr.multimc.api.spigot.managers.teams.Team;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CustomInstanceSample extends Instance {
    public CustomInstanceSample(JavaPlugin plugin, InstancesManager instancesManager, int instanceId, InstanceSettings settings, Location instanceLocation, List<Team> teams) {
        super(plugin, instancesManager, instanceId, settings, instanceLocation, teams);
        plugin.getLogger().info(String.format("Instance %d created", instanceId));
    }

    @Override
    public void tick() {

    }
}
