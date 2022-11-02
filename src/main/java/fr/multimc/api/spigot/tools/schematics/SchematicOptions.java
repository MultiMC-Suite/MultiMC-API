package fr.multimc.api.spigot.tools.schematics;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class SchematicOptions {
    public Location LOCATION;
    public final boolean IGNORE_AIR, COPY_ENTITIES, COPY_BIOMES;

    public SchematicOptions() {
        this.LOCATION = null;
        this.IGNORE_AIR = true;
        this.COPY_BIOMES = false;
        this.COPY_ENTITIES = false;
    }

    public SchematicOptions(boolean ignoreAir, boolean copyBiomes, boolean copyEntities) {
        this.LOCATION = null;
        this.IGNORE_AIR = ignoreAir;
        this.COPY_BIOMES = copyBiomes;
        this.COPY_ENTITIES = copyEntities;
    }

    public SchematicOptions(@Nullable Location location) {
        this.LOCATION = location;
        this.IGNORE_AIR = true;
        this.COPY_BIOMES = false;
        this.COPY_ENTITIES = false;
    }

    public SchematicOptions(@Nullable Location location, boolean ignoreAir, boolean copyEntities, boolean copyBiomes) {
        this.LOCATION = location;
        this.IGNORE_AIR = ignoreAir;
        this.COPY_BIOMES = copyBiomes;
        this.COPY_ENTITIES = copyEntities;
    }

    public void setLocation(@NotNull Location location) {
        this.LOCATION = location;
    }
}
