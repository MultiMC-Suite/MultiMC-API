package fr.multimc.api.spigot.common.worlds.locations.zones;

import fr.multimc.api.commons.tools.formatters.MathNumber;
import fr.multimc.api.spigot.common.worlds.locations.RelativeLocation;
import fr.multimc.api.spigot.common.worlds.locations.zones.enums.ZoneListener;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class Zone implements Listener {

    private final World world;
    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;
    private final double minZ;
    private final double maxZ;
    private final Location minLocation;
    private final Location maxLocation;
    private final ZoneListener callback;

    /**
     * Constructs a new zone using two {@link Location} representing the two opposite corners of the zone.
     * @param location1 The first corner of the zone.
     * @param location2 The second corner of the zone.
     * @param plugin The plugin registering this zone as a listener
     * @param callback The {@link ZoneListener} callbacks for enter and exit event
     * @throws IllegalArgumentException if the locations are not in the same world
     */
    public Zone(@Nonnull Location location1, @Nonnull Location location2, @Nullable Plugin plugin, @Nullable ZoneListener callback) {
        if(location1.getWorld() != location2.getWorld())
            throw new IllegalArgumentException("The two locations must be in the same world.");
        this.world = location1.getWorld();
        this.minX = Math.min(location1.getBlockX(), location2.getBlockX());
        this.minY = Math.min(location1.getBlockY(), location2.getBlockY());
        this.minZ = Math.min(location1.getBlockZ(), location2.getBlockZ());
        this.maxX = Math.max(location1.getBlockX(), location2.getBlockX());
        this.maxY = Math.max(location1.getBlockY(), location2.getBlockY());
        this.maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ());
        this.minLocation = new Location(world, minX, minY, minZ);
        this.maxLocation = new Location(world, maxX, maxY, maxZ);
        this.callback = callback;
        if(Objects.nonNull(plugin) && Objects.nonNull(callback))
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Constructs a new zone using two {@link Location} representing the two opposite corners of the zone.
     * @param location1 The first corner of the zone.
     * @param location2 The second corner of the zone.
     */
    public Zone(@Nonnull Location location1, @Nonnull Location location2) {
        this(location1, location2, null, null);
    }

    /**
     * Constructs a new zone using a center {@link Location} and two {@link RelativeLocation} representing the two point on the circle
     * @param center center location of the zone
     * @param location1 the first point on the circle
     * @param location2 the second point on the circle
     * @param plugin The plugin registering this zone as a listener
     * @param callback The {@link ZoneListener} callbacks for enter and exit event
     */
    public Zone(@Nonnull Location center, @Nonnull RelativeLocation location1, @Nonnull RelativeLocation location2, @Nullable Plugin plugin, @Nullable ZoneListener callback) {
        this(location1.toAbsolute(center), location2.toAbsolute(center), plugin, callback);
    }

    /**
     * Constructs a new zone using a center {@link Location} and two {@link RelativeLocation} representing the two point on the circle
     * @param center center location of the zone
     * @param location1 the first point on the circle
     * @param location2 the second point on the circle
     */
    public Zone(@Nonnull Location center, @Nonnull RelativeLocation location1, @Nonnull RelativeLocation location2) {
        this(location1.toAbsolute(center), location2.toAbsolute(center), null, null);
    }

    /**
     * Check whether the given {@link Entity} is inside the zone
     * @param entity the given entity
     * @return true if the entity is inside the zone, false otherwise
     */
    public boolean isIn(@NotNull Entity entity){
        return isIn(entity.getLocation());
    }

    /**
     * Check whether the given {@link Player} is inside the zone
     * @param player the given player
     * @return true if the player is inside the zone, false otherwise
     */
    public boolean isIn(@Nonnull Player player) {
        return isIn(player.getLocation());
    }

    /**
     * Check whether the given {@link Location} is inside the zone
     * @param location the given location
     * @return true if the location is inside the zone, false otherwise
     */
    public boolean isIn(@Nonnull Location location) {
        if(location.getWorld() != world) return false;
        return MathNumber.isDoubleBetween(location.getX(), minX, maxX)
                && MathNumber.isDoubleBetween(location.getY(), minY, maxY)
                && MathNumber.isDoubleBetween(location.getZ(), minZ, maxZ);
    }

    /**
     * Get a {@link List<Block>} of all the blocks in the zone.
     * @return {@link List<Block>}
     */
    public List<Block> getBlocks(){
        int minBlockX = this.minLocation.getBlockX();
        int minBlockY = this.minLocation.getBlockY();
        int minBlockZ = this.minLocation.getBlockZ();
        int maxBlockX = this.maxLocation.getBlockX();
        int maxBlockY = this.maxLocation.getBlockY();
        int maxBlockZ = this.maxLocation.getBlockZ();
        List<Block> blocks = new ArrayList<>();
        for(int x = minBlockX; x <= maxBlockX; x++)
            for(int y = minBlockY; y <= maxBlockY; y++)
                for(int z = minBlockZ; z <= maxBlockZ; z++)
                    blocks.add(this.world.getBlockAt(x, y, z));
        return blocks;
    }

    /**
     * Get the world this zone is in.
     * @return {@link World}
     */
    public World getWorld() {
        return world;
    }

    /**
     * EventHandler for {@link PlayerMoveEvent}
     * @param e the player move event
     */
    @EventHandler
    private void onPlayerMove(PlayerMoveEvent e) {
        if(!this.isIn(e.getFrom()) && this.isIn(e.getTo()))
            this.callback.onPlayerEnter(e);
        else if(this.isIn(e.getFrom()) && !this.isIn(e.getTo()))
            this.callback.onPlayerExit(e);
    }


    /**
     * EventHandler for {@link EntityMoveEvent}
     * @param e the entity move event
     */
    @EventHandler
    private void onEntityMove(EntityMoveEvent e){
        if(!this.isIn(e.getFrom()) && this.isIn(e.getTo()))
            this.callback.onEntityEnter(e);
        else if(this.isIn(e.getFrom()) && !this.isIn(e.getTo()))
            this.callback.onEntityExit(e);
    }

    /**
     * EventHandler for {@link PlayerTeleportEvent}
     * @param e the player teleport event
     */
    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent e){
        if(!this.isIn(e.getFrom()) && this.isIn(e.getTo()))
            this.callback.onPlayerEnter(e);
        else if(this.isIn(e.getFrom()) && !this.isIn(e.getTo()))
            this.callback.onPlayerExit(e);
    }
}
