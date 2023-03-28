package fr.multimc.api.spigot.common.worlds.schematics;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class SchematicOptions {
    private Location location;
    private final boolean IGNORE_AIR;
    private final boolean COPY_ENTITIES;
    private final boolean COPY_BIOMES;

    public SchematicOptions() {
        this.location = null;
        this.IGNORE_AIR = true;
        this.COPY_BIOMES = false;
        this.COPY_ENTITIES = false;
    }

    public SchematicOptions(boolean ignoreAir, boolean copyBiomes, boolean copyEntities) {
        this.location = null;
        this.IGNORE_AIR = ignoreAir;
        this.COPY_BIOMES = copyBiomes;
        this.COPY_ENTITIES = copyEntities;
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

    public void setLocation(@NotNull Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
    public boolean isIgnoreAir() {
        return IGNORE_AIR;
    }
    public boolean isCopyEntity() {
        return COPY_ENTITIES;
    }
    public boolean isCopyBiomes() {
        return COPY_BIOMES;
    }
}
