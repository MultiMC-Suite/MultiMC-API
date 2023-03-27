package fr.multimc.api.spigot.common.worlds.settings;

import fr.multimc.api.spigot.common.worlds.locations.RelativeLocation;
import fr.multimc.api.spigot.common.worlds.schematics.Schematic;
import fr.multimc.api.spigot.common.worlds.settings.enums.GameRuleSet;
import fr.multimc.api.spigot.common.worlds.settings.enums.WorldPrevention;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnusedReturnValue")
public class WorldSettings {

    private final String worldName;
    private final Schematic schematic;
    private final RelativeLocation spawn;
    private Difficulty difficulty;
    private GameMode gameMode;
    private final List<WorldPrevention> preventions = new ArrayList<>();
    private final Map<GameRule<?>, Object> gameRules = new HashMap<>();

    public WorldSettings(@NotNull String worldName) {
        this(worldName, null, null, Difficulty.NORMAL, null);
    }

    public WorldSettings(@NotNull String worldName, @Nullable Schematic schematic, @Nullable RelativeLocation spawn, @NotNull Difficulty difficulty) {
        this(worldName, schematic, spawn, difficulty, null);
    }

    public WorldSettings(@NotNull String worldName, @Nullable Schematic schematic, @Nullable RelativeLocation spawn, @NotNull Difficulty difficulty, @Nullable GameMode gameMode) {
        this.worldName = worldName;
        this.schematic = schematic;
        this.spawn = spawn == null ? new RelativeLocation(0.5, 100, 0.5) : spawn;
        this.difficulty = difficulty;
        this.gameMode = gameMode;
    }

    public String getWorldName() {
        return worldName;
    }
    @Nullable
    public Schematic getSchematic() {
        return schematic;
    }
    public RelativeLocation getSpawn() {
        return spawn;
    }
    public Difficulty getDifficulty() {
        return difficulty;
    }
    @Nullable
    public GameMode getGameMode() {
        return gameMode;
    }
    public List<WorldPrevention> getPreventions() {
        return preventions;
    }
    public Map<GameRule<?>, Object> getGameRules() {
        return gameRules;
    }

    public WorldSettings addGameRule(GameRule<?> gameRule, Object value) {
        gameRules.put(gameRule, value);
        return this;
    }
    public WorldSettings addGameRuleSet(GameRuleSet gameRuleSet){
        this.gameRules.putAll(gameRuleSet.getGameRules());
        return this;
    }
    public WorldSettings addPrevention(WorldPrevention prevention) {
        preventions.add(prevention);
        return this;
    }
    public WorldSettings setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        return this;
    }
    public WorldSettings setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
        return this;
    }
}
