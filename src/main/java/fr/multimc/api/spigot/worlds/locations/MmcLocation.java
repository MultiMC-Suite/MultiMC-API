package fr.multimc.api.spigot.worlds.locations;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class MmcLocation {
    private static final String SPLITTER = ":";

    public static Location toLocation(@Nonnull String entry) {
        String[] p = entry.split(SPLITTER);
        World world = Bukkit.getWorld(p[0]);
        double x = Double.parseDouble(p[1]);
        double y = Double.parseDouble(p[2]);
        double z = Double.parseDouble(p[3]);

        if (p.length > 4) {
            float yaw = Float.parseFloat(p[4]);
            float pitch = Float.parseFloat(p[5]);
            return new Location(world, x, y, z, yaw, pitch);
        }

        return new Location(world, x, y, z, 0, 0);
    }

    public static RelativeLocation toRelativeLocation(@Nonnull String entry) {
        String[] p = entry.split(SPLITTER);
        double x = Double.parseDouble(p[0]);
        double y = Double.parseDouble(p[1]);
        double z = Double.parseDouble(p[2]);

        if (p.length > 3) {
            float yaw = Float.parseFloat(p[3]);
            float pitch = Float.parseFloat(p[4]);
            return new RelativeLocation(x, y, z, yaw, pitch);
        }

        return new RelativeLocation(x, y, z, 0, 0);
    }

    public static String toString(@Nonnull Location location, boolean direction) {
        double x = Math.floor(location.getX() * 10) / 10;
        double y = Math.floor(location.getX() * 10) / 10;
        double z = Math.floor(location.getX() * 10) / 10;
        float yaw = Math.round(location.getX() * 10) / 10F;
        float pitch = Math.round(location.getX() * 10) / 10F;

        return x + SPLITTER + y + SPLITTER + z + (direction ? SPLITTER + yaw + SPLITTER + pitch : "");
    }

    public static String toString(@Nonnull RelativeLocation location, boolean direction) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        return x + SPLITTER + y + SPLITTER + z + (direction ? SPLITTER + yaw + SPLITTER + pitch : "");
    }
}
