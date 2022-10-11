package fr.multimc.api.commons.managers.game.instances;

import fr.multimc.api.commons.managers.game.CustomEntity;
import fr.multimc.api.commons.managers.game.CustomLocation;
import fr.multimc.api.commons.managers.game.GameType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class InstanceSettings {

    private final String schematicName;
    private final GameType gameType;
    private final int duration;
    private final List<CustomLocation> spawnPoints;
    private final List<CustomEntity> entities;
    private final HashMap<String, String> customSettings;

    public InstanceSettings(){
        this.schematicName = "schematicName";
        this.gameType = GameType.TEAM_VS_TEAM;
        this.duration = 600;
        this.spawnPoints = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.customSettings = new HashMap<>();
    }

    public InstanceSettings(String schematicName, GameType gameType, int duration, List<CustomLocation> spawnPoints, List<CustomEntity> entities, HashMap<String, String> customSettings) {
        this.schematicName = schematicName;
        this.gameType = gameType;
        this.duration = duration;
        this.spawnPoints = spawnPoints;
        this.entities = entities;
        this.customSettings = customSettings;
    }

    public String getSchematicName() {
        return schematicName;
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
}
