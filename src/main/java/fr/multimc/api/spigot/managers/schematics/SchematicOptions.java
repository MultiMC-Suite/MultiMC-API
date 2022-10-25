package fr.multimc.api.spigot.managers.schematics;

import org.bukkit.Location;

@SuppressWarnings("unused")
public class SchematicOptions {
    public Location LOCATION;
    public final boolean IGNORE_AIR, COPY_ENTITIES, COPY_BIOMES;

    public SchematicOptions() {
        this.LOCATION = null;
        this.IGNORE_AIR = false;
        this.COPY_BIOMES = false;
        this.COPY_ENTITIES = false;
    }

    public SchematicOptions(Location location) {
        this.LOCATION = location;
        this.IGNORE_AIR = false;
        this.COPY_BIOMES = false;
        this.COPY_ENTITIES = false;
    }

    public SchematicOptions(Location location, boolean ignoreAir, boolean copyEntities, boolean copyBiomes) {
        this.LOCATION = location;
        this.IGNORE_AIR = ignoreAir;
        this.COPY_BIOMES = copyBiomes;
        this.COPY_ENTITIES = copyEntities;
    }

    public void setLocation(Location LOCATION) {
        this.LOCATION = LOCATION;
    }
}
