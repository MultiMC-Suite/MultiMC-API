package fr.multimc.api.spigot.tools.locations;

import org.bukkit.Location;

@SuppressWarnings("unused")
public class RelativeLocation {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public RelativeLocation(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public RelativeLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
        this.pitch = 0;
    }

    public static RelativeLocation getRelativeLocation(Location loc1, Location loc2){
        return new RelativeLocation(loc1.getX() - loc2.getX(), loc1.getY() - loc2.getY(), loc1.getZ() - loc2.getZ(), loc1.getYaw() - loc2.getYaw(), loc1.getPitch() - loc2.getPitch());
    }

    public Location toAbsolute(Location loc){
        return new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z, loc.getYaw() + yaw, loc.getPitch() + pitch);
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }
    public float getPitch() {
        return pitch;
    }
    public float getYaw() {
        return yaw;
    }
}