package fr.multimc.api.spigot.samplecode.teams;

import fr.multimc.api.commons.managers.game.instances.Instance;
import fr.multimc.api.commons.managers.game.instances.InstanceSettings;
import fr.multimc.api.commons.managers.teammanager.Team;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomInstanceSample extends Instance {
    public CustomInstanceSample(JavaPlugin plugin, InstanceSettings settings, Location instanceLocation, Team... teams) {
        super(plugin, settings, instanceLocation, teams);
    }
}
