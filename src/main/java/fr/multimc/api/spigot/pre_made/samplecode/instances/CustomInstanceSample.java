package fr.multimc.api.spigot.pre_made.samplecode.instances;

import fr.multimc.api.spigot.managers.instance.Instance;
import fr.multimc.api.spigot.managers.instance.InstanceSettings;
import fr.multimc.api.spigot.managers.instance.InstancesManager;
import fr.multimc.api.spigot.managers.teams.MmcTeam;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CustomInstanceSample extends Instance {
    public CustomInstanceSample(JavaPlugin plugin, InstancesManager instancesManager, int instanceId, InstanceSettings settings, Location instanceLocation, List<MmcTeam> mmcTeams) {
        super(plugin, instancesManager, instanceId, settings, instanceLocation, mmcTeams);
        plugin.getLogger().info(String.format("Instance %d created", instanceId));
    }

    @Override
    public void tick() {

    }
}
