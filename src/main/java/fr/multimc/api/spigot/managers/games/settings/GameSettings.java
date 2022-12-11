package fr.multimc.api.spigot.managers.games.settings;

import fr.multimc.api.spigot.managers.games.enums.GameType;
import fr.multimc.api.spigot.entities.MmcEntity;
import fr.multimc.api.spigot.worlds.locations.RelativeLocation;
import fr.multimc.api.spigot.worlds.schematics.Schematic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public record GameSettings(Schematic schematic,
                           GameType gameType,
                           List<RelativeLocation> spawnPoints,
                           List<MmcEntity> entities,
                           HashMap<String, Object> customSettings,
                           int duration,
                           int minPlayers,
                           int tickDelay) {

    /**
     * Constructor of InstanceSettings
     * @param schematic File for the game schematic
     * @param gameType GameType of the game
     * @param duration Duration of the game
     * @param spawnPoints List of spawn points
     * @param entities List of entities to spawn
     * @param customSettings Custom settings
     * @param tickDelay Delay in game tick between two calls of Instance's tick method
     */
    public GameSettings(@NotNull Schematic schematic,
                        @NotNull GameType gameType,
                        @NotNull List<RelativeLocation> spawnPoints,
                        @Nullable List<MmcEntity> entities,
                        @Nullable HashMap<String, Object> customSettings,
                        int duration,
                        int minPlayers,
                        int tickDelay) {
        this.schematic = schematic;
        this.gameType = gameType;
        this.spawnPoints = spawnPoints;
        this.entities = entities == null ? new ArrayList<>() : entities;
        this.customSettings = customSettings == null ? new HashMap<>() : customSettings;
        this.duration = duration;
        this.minPlayers = minPlayers;
        this.tickDelay = tickDelay;
    }
}
