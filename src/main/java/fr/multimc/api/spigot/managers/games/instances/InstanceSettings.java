package fr.multimc.api.spigot.managers.games.instances;

import fr.multimc.api.spigot.customs.CustomEntity;
import fr.multimc.api.spigot.managers.schematics.Schematic;
import fr.multimc.api.spigot.managers.schematics.SchematicOptions;
import fr.multimc.api.spigot.tools.locations.RelativeLocation;
import fr.multimc.api.spigot.managers.games.GameType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class InstanceSettings {

    private final Schematic schematic;
    private final SchematicOptions schematicOptions;
    private final GameType gameType;
    private final int duration;
    private final List<RelativeLocation> spawnPoints;
    private final List<CustomEntity> entities;
    private final HashMap<String, Object> customSettings;
    private final int tickDelay;
    private final String worldsPrefix;

    public InstanceSettings(){
        this.schematic = null;
        this.schematicOptions = null;
        this.gameType = GameType.ONLY_TEAM;
        this.duration = 600;
        this.spawnPoints = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.customSettings = new HashMap<>();
        this.tickDelay = 1;
        this.worldsPrefix = "multimc";
    }

    /**
     * Constructor of InstanceSettings
     * @param schematic File for the game schematic
     * @param gameType GameType of the game
     * @param duration Duration of the game
     * @param spawnPoints List of spawn points
     * @param entities List of entities to spawn
     * @param customSettings Custom settings
     * @param tickDelay Delay in game tick between two calls of Instance's tick method
     * @param worldsPrefix Prefix of the worlds
     */
    public InstanceSettings(Schematic schematic,
                            SchematicOptions schematicOptions,
                            GameType gameType,
                            int duration,
                            List<RelativeLocation> spawnPoints,
                            List<CustomEntity> entities,
                            HashMap<String, Object> customSettings,
                            int tickDelay,
                            String worldsPrefix) {
        this.schematic = schematic;
        this.schematicOptions = schematicOptions;
        this.gameType = gameType;
        this.duration = duration;
        this.spawnPoints = spawnPoints;
        this.entities = entities;
        this.customSettings = customSettings;
        this.tickDelay = tickDelay;
        this.worldsPrefix = worldsPrefix;
    }

    public Schematic getSchematic() {
        return schematic;
    }

    public SchematicOptions getSchematicOptions() {
        return schematicOptions;
    }

    public GameType getGameType() {
        return gameType;
    }

    public int getDuration() {
        return duration;
    }

    public List<RelativeLocation> getSpawnPoints() {
        return spawnPoints;
    }

    public List<CustomEntity> getEntities() {
        return entities;
    }

    public HashMap<String, Object> getCustomSettings() {
        return customSettings;
    }

    public int getTickDelay() {
        return tickDelay;
    }

    public String getWorldsPrefix() {
        return this.worldsPrefix;
    }
}
