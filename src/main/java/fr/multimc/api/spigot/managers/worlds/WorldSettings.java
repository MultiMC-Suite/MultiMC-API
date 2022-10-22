package fr.multimc.api.spigot.managers.worlds;

import fr.multimc.api.spigot.managers.schematics.Schematic;
import fr.multimc.api.spigot.tools.locations.RelativeLocation;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class WorldSettings {

    public final String WORLD_NAME;
    public final boolean PREVENT_PVP, PREVENT_DAMAGES, PREVENT_BUILD, PREVENT_FOOD_LOSS;
    public final Schematic SCHEMATIC;
    public final RelativeLocation SPAWN;

    public WorldSettings(@NotNull String worldName) {
        this.WORLD_NAME = worldName;
        this.SCHEMATIC = null;
        this.PREVENT_DAMAGES = false;
        this.PREVENT_PVP = false;
        this.PREVENT_BUILD = false;
        this.PREVENT_FOOD_LOSS = false;
        this.SPAWN = new RelativeLocation(0.5, 100, 0.5);
    }

    public WorldSettings(@NotNull String worldName, Schematic schematic) {
        this.WORLD_NAME = worldName;
        this.SCHEMATIC = schematic;
        this.PREVENT_DAMAGES = false;
        this.PREVENT_PVP = false;
        this.PREVENT_BUILD = false;
        this.PREVENT_FOOD_LOSS = false;
        this.SPAWN = new RelativeLocation(0.5, 100, 0.5);
    }

    public WorldSettings(@NotNull String world_name, Schematic schematic, boolean prevent_damages, boolean prevent_pvp, boolean prevent_build, boolean prevent_food) {
        this.WORLD_NAME = world_name;
        this.PREVENT_DAMAGES = prevent_damages;
        this.SCHEMATIC = schematic;
        this.PREVENT_PVP = prevent_pvp;
        this.PREVENT_BUILD = prevent_build;
        this.PREVENT_FOOD_LOSS = prevent_food;
        this.SPAWN = new RelativeLocation(0.5, 100, 0.5);
    }

    public WorldSettings(@NotNull String world_name, Schematic schematic, boolean prevent_damages, boolean prevent_pvp, boolean prevent_build, boolean prevent_food, RelativeLocation spawn) {
        this.WORLD_NAME = world_name;
        this.PREVENT_DAMAGES = prevent_damages;
        this.SCHEMATIC = schematic;
        this.PREVENT_PVP = prevent_pvp;
        this.PREVENT_BUILD = prevent_build;
        this.PREVENT_FOOD_LOSS = prevent_food;
        this.SPAWN = spawn;
    }
}
