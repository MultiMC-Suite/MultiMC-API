package fr.multimc.api.spigot.managers.schematics;

import org.bukkit.Location;

import javax.annotation.Nonnull;

public class SchematicOptions {
    public final Location LOCATION;
    public final boolean IGNORE_AIR, COPY_ENTITIES, COPY_BIOMES;

    public SchematicOptions(@Nonnull Location location) {
        this.LOCATION = location;
        this.IGNORE_AIR = false;
        this.COPY_BIOMES = false;
        this.COPY_ENTITIES = false;
    }

    public SchematicOptions(@Nonnull Location location, boolean ignoreAir, boolean copyEntities, boolean copyBiomes) {
        this.LOCATION = location;
        this.IGNORE_AIR = ignoreAir;
        this.COPY_BIOMES = copyBiomes;
        this.COPY_ENTITIES = copyEntities;
    }
}
