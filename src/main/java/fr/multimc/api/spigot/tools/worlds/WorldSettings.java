package fr.multimc.api.spigot.tools.worlds;

import fr.multimc.api.spigot.tools.worlds.locations.RelativeLocation;
import fr.multimc.api.spigot.tools.worlds.schematics.Schematic;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class WorldSettings {

    private String worldName;
    private Schematic schematic;
    private RelativeLocation spawn;
    private Difficulty difficulty;
    private GameMode gameMode;
    private boolean preventPvp, preventDamages, PreventBuild, PreventFoodLoss, preventTimeFlow, preventWeather, preventPortalUse;

    public WorldSettings(@NotNull String worldName, boolean defaultSettingsValue) {
        this.worldName = worldName;
        this.schematic = null;
        this.spawn = new RelativeLocation(0.5, 100, 0.5);
        this.difficulty = Difficulty.NORMAL;
        this.gameMode = null;
        this.preventDamages = defaultSettingsValue;
        this.preventPvp = defaultSettingsValue;
        this.PreventBuild = defaultSettingsValue;
        this.PreventFoodLoss = defaultSettingsValue;
        this.preventTimeFlow = defaultSettingsValue;
        this.preventWeather = defaultSettingsValue;
        this.preventPortalUse = defaultSettingsValue;
    }

    public WorldSettings(@NotNull String worldName, @Nullable Schematic schematic, boolean defaultSettingsValue) {
        this.worldName = worldName;
        this.schematic = schematic;
        this.spawn = new RelativeLocation(0.5, 100, 0.5);
        this.difficulty = Difficulty.NORMAL;
        this.gameMode = null;
        this.preventDamages = defaultSettingsValue;
        this.preventPvp = defaultSettingsValue;
        this.PreventBuild = defaultSettingsValue;
        this.PreventFoodLoss = defaultSettingsValue;
        this.preventTimeFlow = defaultSettingsValue;
        this.preventWeather = defaultSettingsValue;
        this.preventPortalUse = defaultSettingsValue;
    }

    public WorldSettings(@NotNull String worldName, @Nullable Schematic schematic, @Nullable RelativeLocation spawn, boolean defaultSettingsValue) {
        this.worldName = worldName;
        this.schematic = schematic;
        this.spawn = spawn == null ? new RelativeLocation(0.5, 100, 0.5) : spawn;
        this.difficulty = Difficulty.NORMAL;
        this.gameMode = null;
        this.preventDamages = defaultSettingsValue;
        this.preventPvp = defaultSettingsValue;
        this.PreventBuild = defaultSettingsValue;
        this.PreventFoodLoss = defaultSettingsValue;
        this.preventTimeFlow = defaultSettingsValue;
        this.preventWeather = defaultSettingsValue;
        this.preventPortalUse = defaultSettingsValue;
    }

    public WorldSettings(@NotNull String worldName, @Nullable Schematic schematic, @Nullable RelativeLocation spawn, @NotNull Difficulty difficulty, @Nullable GameMode gameMode, boolean defaultSettingsValue) {
        this.worldName = worldName;
        this.schematic = schematic;
        this.spawn = spawn == null ? new RelativeLocation(0.5, 100, 0.5) : spawn;
        this.difficulty = difficulty;
        this.gameMode = gameMode;
        this.preventDamages = defaultSettingsValue;
        this.preventPvp = defaultSettingsValue;
        this.PreventBuild = defaultSettingsValue;
        this.PreventFoodLoss = defaultSettingsValue;
        this.preventTimeFlow = defaultSettingsValue;
        this.preventWeather = defaultSettingsValue;
        this.preventPortalUse = defaultSettingsValue;
    }

    public WorldSettings(@NotNull String world_name, @Nullable Schematic schematic, @Nullable RelativeLocation spawn, @NotNull Difficulty difficulty, @Nullable GameMode gameMode, boolean prevent_damages, boolean prevent_pvp, boolean prevent_build, boolean prevent_food, boolean preventTimeFlow, boolean preventWeather, boolean preventPortalUse) {
        this.worldName = world_name;
        this.schematic = schematic;
        this.spawn = spawn == null ? new RelativeLocation(0.5, 100, 0.5) : spawn;
        this.difficulty = difficulty;
        this.gameMode = gameMode;
        this.preventDamages = prevent_damages;
        this.preventPvp = prevent_pvp;
        this.PreventBuild = prevent_build;
        this.PreventFoodLoss = prevent_food;
        this.preventTimeFlow = preventTimeFlow;
        this.preventWeather = preventWeather;
        this.preventPortalUse = preventPortalUse;
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
    public boolean isPreventPvp() {
        return preventPvp;
    }
    public boolean isPreventDamages() {
        return preventDamages;
    }
    public boolean isPreventBuild() {
        return PreventBuild;
    }
    public boolean isPreventFoodLoss() {
        return PreventFoodLoss;
    }
    public boolean isPreventTimeFlow() {
        return preventTimeFlow;
    }
    public boolean isPreventWeather() {
        return preventWeather;
    }
    public boolean isPreventPortalUse() {
        return preventPortalUse;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
    public void setSchematic(Schematic schematic) {
        this.schematic = schematic;
    }
    public void setSpawn(RelativeLocation spawn) {
        this.spawn = spawn;
    }
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
    public void setPreventPvp(boolean preventPvp) {
        this.preventPvp = preventPvp;
    }
    public void setPreventDamages(boolean preventDamages) {
        this.preventDamages = preventDamages;
    }
    public void setPreventBuild(boolean preventBuild) {
        PreventBuild = preventBuild;
    }
    public void setPreventFoodLoss(boolean preventFoodLoss) {
        PreventFoodLoss = preventFoodLoss;
    }
    public void setPreventTimeFlow(boolean preventTimeFlow) {
        this.preventTimeFlow = preventTimeFlow;
    }
    public void setPreventWeather(boolean preventWeather) {
        this.preventWeather = preventWeather;
    }
    public void setPreventPortalUse(boolean preventPortalUse) {
        this.preventPortalUse = preventPortalUse;
    }
}
