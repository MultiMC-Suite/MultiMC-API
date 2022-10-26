package fr.multimc.api.spigot.managers.schematics;

import org.bukkit.Location;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class SchematicOptions {
    public Location location;
    public final boolean IGNORE_AIR, COPY_ENTITIES, COPY_BIOMES;

    public SchematicOptions() {
        this.location = null;
        this.IGNORE_AIR = true;
        this.COPY_BIOMES = false;
        this.COPY_ENTITIES = false;
    }

    public SchematicOptions(@Nullable Location location) {
        this.location = location;
        this.IGNORE_AIR = true;
        this.COPY_BIOMES = false;
        this.COPY_ENTITIES = false;
    }

    public SchematicOptions(@Nullable Location location, boolean ignoreAir, boolean copyEntities, boolean copyBiomes) {
        this.location = location;
        this.IGNORE_AIR = ignoreAir;
        this.COPY_BIOMES = copyBiomes;
        this.COPY_ENTITIES = copyEntities;
    }

    public void setLocation(Location LOCATION) {
        this.location = LOCATION;
    }
}
