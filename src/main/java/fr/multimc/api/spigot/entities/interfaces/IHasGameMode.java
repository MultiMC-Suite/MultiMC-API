package fr.multimc.api.spigot.entities.interfaces;

import org.bukkit.GameMode;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface IHasGameMode {
    boolean setGameMode(@NotNull GameMode mode);
    boolean setGameModeSync(@NotNull Plugin plugin, @NotNull GameMode mode);
}
