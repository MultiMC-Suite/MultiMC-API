package fr.multimc.api.spigot.managers.instance;

import fr.multimc.api.spigot.managers.instance.enums.GameType;
import fr.multimc.api.spigot.tools.entities.MmcEntity;
import fr.multimc.api.spigot.tools.worlds.locations.RelativeLocation;
import fr.multimc.api.spigot.tools.worlds.schematics.Schematic;
import fr.multimc.api.spigot.tools.worlds.schematics.SchematicOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public record InstanceSettings(Schematic schematic, SchematicOptions schematicOptions, GameType gameType, int duration,
                               List<RelativeLocation> spawnPoints, List<MmcEntity> entities,
                               HashMap<String, Object> customSettings, int tickDelay) {

    /**
     * Constructor of InstanceSettings
     *
     * @param schematic      File for the game schematic
     * @param gameType       GameType of the game
     * @param duration       Duration of the game
     * @param spawnPoints    List of spawn points
     * @param entities       List of entities to spawn
     * @param customSettings Custom settings
     * @param tickDelay      Delay in game tick between two calls of Instance's tick method
     */
    public InstanceSettings(@NotNull Schematic schematic,
                            @NotNull SchematicOptions schematicOptions,
                            @NotNull GameType gameType,
                            int duration,
                            @NotNull List<RelativeLocation> spawnPoints,
                            @Nullable List<MmcEntity> entities,
                            @Nullable HashMap<String, Object> customSettings,
                            int tickDelay) {
        this.schematic = schematic;
        this.schematicOptions = schematicOptions;
        this.gameType = gameType;
        this.duration = duration;
        this.spawnPoints = spawnPoints;
        this.entities = entities == null ? new ArrayList<>() : entities;
        this.customSettings = customSettings == null ? new HashMap<>() : customSettings;
        this.tickDelay = tickDelay;
    }


}
