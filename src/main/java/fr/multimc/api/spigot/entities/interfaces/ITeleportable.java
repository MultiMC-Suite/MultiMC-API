package fr.multimc.api.spigot.entities.interfaces;

import fr.multimc.api.spigot.entities.player.MmcPlayer;
import fr.multimc.api.spigot.worlds.locations.RelativeLocation;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface ITeleportable {
    boolean teleportTo(@NotNull MmcPlayer target);
    boolean teleportRelative(@NotNull RelativeLocation location);
    boolean teleportRelative(@NotNull RelativeLocation location, boolean center);
    boolean teleport(@NotNull Location target);
    boolean teleport(@NotNull Location location, boolean center);
    boolean teleportToSync(@NotNull Plugin plugin, @NotNull MmcPlayer target);
    boolean teleportRelativeSync(@NotNull Plugin plugin, @NotNull RelativeLocation location);
    boolean teleportRelativeSync(@NotNull Plugin plugin, @NotNull RelativeLocation location, boolean center);
    boolean teleportSync(@NotNull Plugin plugin, @NotNull Location location);
    boolean teleportSync(@NotNull Plugin plugin, @NotNull Location location, boolean center);
}
