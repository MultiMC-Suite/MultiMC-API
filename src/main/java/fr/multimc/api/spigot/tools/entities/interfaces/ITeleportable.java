package fr.multimc.api.spigot.tools.entities.interfaces;

import fr.multimc.api.spigot.tools.entities.player.MmcPlayer;
import fr.multimc.api.spigot.tools.worlds.locations.RelativeLocation;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface ITeleportable {
    boolean teleportTo(@NotNull MmcPlayer target);
    boolean teleportRelative(@NotNull RelativeLocation location);
    boolean teleportRelative(@NotNull RelativeLocation location, boolean center);
    boolean teleport(@NotNull Location target);
    boolean teleport(@NotNull Location location, boolean center);
    boolean teleportToSync(@NotNull JavaPlugin plugin, @NotNull MmcPlayer target);
    boolean teleportRelativeSync(@NotNull JavaPlugin plugin, @NotNull RelativeLocation location);
    boolean teleportRelativeSync(@NotNull JavaPlugin plugin, @NotNull RelativeLocation location, boolean center);
    boolean teleportSync(@NotNull JavaPlugin plugin, @NotNull Location location);
    boolean teleportSync(@NotNull JavaPlugin plugin, @NotNull Location location, boolean center);
}
