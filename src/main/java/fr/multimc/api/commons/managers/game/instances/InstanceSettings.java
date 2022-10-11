package fr.multimc.api.commons.managers.game.instances;

import fr.multimc.api.commons.managers.game.CustomEntity;
import fr.multimc.api.commons.managers.game.CustomLocation;
import fr.multimc.api.commons.managers.game.GameType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class InstanceSettings {

    private final File schematicFile;
    private final GameType gameType;
    private final int duration;
    private final List<CustomLocation> spawnPoints;
    private final List<CustomEntity> entities;
    private final HashMap<String, String> customSettings;
    private final int tickTime;
    private String worldsPrefix;

    public InstanceSettings(){
        this.schematicFile = new File("");
        this.gameType = GameType.ONLY_TEAM;
        this.duration = 600;
        this.spawnPoints = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.customSettings = new HashMap<>();
        this.tickTime = 1;
        this.worldsPrefix = "multimc";
    }

    public InstanceSettings(File schematicFile,
                            GameType gameType,
                            int duration,
                            List<CustomLocation> spawnPoints,
                            List<CustomEntity> entities,
                            HashMap<String, String> customSettings,
                            int tickTime,
                            String worldsPrefix) {
        this.schematicFile = schematicFile;
        this.gameType = gameType;
        this.duration = duration;
        this.spawnPoints = spawnPoints;
        this.entities = entities;
        this.customSettings = customSettings;
        this.tickTime = tickTime;
        this.worldsPrefix = worldsPrefix;
    }

    public File getSchematicFile() {
        return schematicFile;
    }

    public GameType getGameType() {
        return gameType;
    }

    public int getDuration() {
        return duration;
    }

    public List<CustomLocation> getSpawnPoints() {
        return spawnPoints;
    }

    public List<CustomEntity> getEntities() {
        return entities;
    }

    public HashMap<String, String> getCustomSettings() {
        return customSettings;
    }

    public int getTickTime() {
        return tickTime;
    }

    public String getWorldsPrefix() {
        return this.worldsPrefix;
    }
}
