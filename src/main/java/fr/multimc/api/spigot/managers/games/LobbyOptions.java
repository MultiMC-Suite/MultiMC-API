package fr.multimc.api.spigot.managers.games;

import fr.multimc.api.spigot.tools.locations.RelativeLocation;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class LobbyOptions {
    public final RelativeLocation SPAWN;
    public final String WORLD_NAME;
    public final boolean PREVENT_PVP, PREVENT_BUILD, PREVENT_FOOD;

    public LobbyOptions(@Nonnull RelativeLocation spawn, @Nonnull String worldName) {
        this.SPAWN = spawn;
        this.WORLD_NAME = worldName;
        this.PREVENT_PVP = false;
        this.PREVENT_BUILD = false;
        this.PREVENT_FOOD = false;
    }

    public LobbyOptions(@Nonnull RelativeLocation spawn, @Nonnull String worldName, boolean preventPvp, boolean preventBuild, boolean preventFood) {
        this.SPAWN = spawn;
        this.WORLD_NAME = worldName;
        this.PREVENT_PVP = preventPvp;
        this.PREVENT_BUILD = preventBuild;
        this.PREVENT_FOOD = preventFood;
    }
}
