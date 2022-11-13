package fr.multimc.api.spigot.pre_made.samplecode.instances;

import fr.multimc.api.spigot.managers.instance.Instance;
import fr.multimc.api.spigot.tools.settings.InstanceSettings;
import fr.multimc.api.spigot.managers.instance.InstancesManager;
import fr.multimc.api.spigot.managers.teams.MmcTeam;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CustomInstanceSample extends Instance {

    public CustomInstanceSample(JavaPlugin plugin, InstancesManager instancesManager, InstanceSettings settings, Location instanceLocation, List<MmcTeam> mmcTeams, int instanceId) {
        super(plugin, instancesManager, settings, instanceLocation, mmcTeams, instanceId);
    }

    @Override
    public void tick() {

    }
}
