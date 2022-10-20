package fr.multimc.api.commons.managers.game;

import org.bukkit.Location;

@SuppressWarnings("unused")
public class CustomLocation{

    private final double x;
    private final double y;
    private final double z;
    private final double pitch;
    private final double yaw;

    public CustomLocation(double x, double y, double z, double pitch, double yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public CustomLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = 0;
        this.yaw = 0;
    }

    public static CustomLocation getRelativeLocation(Location loc1, Location loc2){
        return new CustomLocation(loc1.getX() - loc2.getX(), loc1.getY() - loc2.getY(), loc1.getZ() - loc2.getZ());
    }
    public Location toAbsolute(Location loc){
        return new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z);
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

    public double getPitch() {
        return pitch;
    }

    public double getYaw() {
        return yaw;
    }
}