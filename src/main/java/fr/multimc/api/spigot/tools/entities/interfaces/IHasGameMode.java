package fr.multimc.api.spigot.tools.entities.interfaces;

import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnusedReturnValue")
public interface IHasGameMode {
    boolean setGameMode(@NotNull GameMode mode);
    boolean setGameModeSync(@NotNull JavaPlugin plugin, @NotNull GameMode mode);
}
