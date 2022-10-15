package fr.multimc.api.commons.managers.game;

import org.bukkit.Location;

@SuppressWarnings("unused")
public record CustomLocation(double x, double y, double z) {
    public static CustomLocation getRelativeLocation(Location loc1, Location loc2){
        return new CustomLocation(loc1.getX() - loc2.getX(), loc1.getY() - loc2.getY(), loc1.getZ() - loc2.getZ());
    }
    public Location toAbsolute(Location loc){
        return new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z);
    }
}
