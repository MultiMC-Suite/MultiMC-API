package fr.multimc.api.commons.managers.game.instances;

import fr.multimc.api.commons.managers.teammanager.Team;
import org.bukkit.Location;

@SuppressWarnings("unused")
public class Instance {

    private Location instanceLocation;

    public Instance(InstanceSettings settings, Location instanceLocation, Team... teams) {

    }

    public Location getInstanceLocation() {
        return instanceLocation;
    }
}
