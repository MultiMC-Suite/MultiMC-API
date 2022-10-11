package fr.multimc.api.commons.managers.game;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public record CustomEntity(EntityType entityType, CustomLocation location) {

    public void spawn(Location instanceLocation) {
        Location spawnLocation = new Location(instanceLocation.getWorld(),
                instanceLocation.getX() + location.x(),
                instanceLocation.getY() + location.y(),
                instanceLocation.getZ() + location.z());
        spawnLocation.getWorld().spawnEntity(spawnLocation, entityType);
    }
}
