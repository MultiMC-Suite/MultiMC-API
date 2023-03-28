package fr.multimc.api.spigot.games.settings;

import fr.multimc.api.spigot.common.entities.MmcEntity;
import fr.multimc.api.spigot.common.scoreboards.interfaces.IScoreBoard;
import fr.multimc.api.spigot.common.worlds.locations.RelativeLocation;
import fr.multimc.api.spigot.common.worlds.schematics.Schematic;
import fr.multimc.api.spigot.games.enums.GameType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public record GameSettings(
        Schematic gameSchematic,
        GameType gameType,
        Class<? extends IScoreBoard> scoreboard,
        List<RelativeLocation> spawnPoints,
        List<MmcEntity> entities,
        HashMap<String, Object> customSettings,
        int duration,
        int minPlayers,
        int tickDelay) {

    /**
     * Constructor of InstanceSettings
     * @param gameSchematic File for the game gameSchematic
     * @param gameType GameType of the game
     * @param duration Duration of the game
     * @param spawnPoints List of spawn points
     * @param entities List of entities to spawn
     * @param customSettings Custom gameSettings
     * @param tickDelay Delay in game tick between two calls of Instance's tick method
     */
    public GameSettings(@NotNull Schematic gameSchematic,
                        @NotNull GameType gameType,
                        @Nullable Class<? extends IScoreBoard> scoreboard,
                        @NotNull List<RelativeLocation> spawnPoints,
                        @Nullable List<MmcEntity> entities,
                        @Nullable HashMap<String, Object> customSettings,
                        int duration,
                        int minPlayers,
                        int tickDelay) {
        this.gameSchematic = gameSchematic;
        this.gameType = gameType;
        this.scoreboard = scoreboard;
        this.spawnPoints = spawnPoints;
        this.entities = entities == null ? new ArrayList<>() : entities;
        this.customSettings = customSettings == null ? new HashMap<>() : customSettings;
        this.duration = duration;
        this.minPlayers = minPlayers;
        this.tickDelay = tickDelay;
    }
}
