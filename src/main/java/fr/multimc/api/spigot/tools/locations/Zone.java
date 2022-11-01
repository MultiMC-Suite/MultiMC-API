package fr.multimc.api.spigot.tools.locations;

import fr.multimc.api.commons.tools.compares.MathNumber;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class Zone {
    private final double minX, maxX, minY, maxY, minZ, maxZ;

    public Zone(@Nonnull Location location1, @Nonnull Location location2) {
        this.minX = Math.min(location1.getBlockX(), location2.getBlockX());
        this.minY = Math.min(location1.getBlockY(), location2.getBlockY());
        this.minZ = Math.min(location1.getBlockZ(), location2.getBlockZ());
        this.maxX = Math.max(location1.getBlockX(), location2.getBlockX());
        this.maxY = Math.max(location1.getBlockY(), location2.getBlockY());
        this.maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ());
    }

    public Zone(@Nonnull Location center, @Nonnull RelativeLocation location1, @Nonnull RelativeLocation location2) {
        Location loc1 = location1.toAbsolute(center);
        Location loc2 = location2.toAbsolute(center);

        this.minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        this.minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        this.minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        this.maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        this.maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        this.maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
    }

    public boolean isIn(@Nonnull Player player) {
        return MathNumber.isDoubleBetween(player.getLocation().getBlockX(), minX, maxX)
                && MathNumber.isDoubleBetween(player.getLocation().getBlockY(), minY, maxY)
                && MathNumber.isDoubleBetween(player.getLocation().getBlockZ(), minZ, maxZ);
    }

    public boolean isIn(@Nonnull Location location) {
        return MathNumber.isDoubleBetween(location.getX(), minX, maxX)
                && MathNumber.isDoubleBetween(location.getY(), minY, maxY)
                && MathNumber.isDoubleBetween(location.getZ(), minZ, maxZ);
    }
}
