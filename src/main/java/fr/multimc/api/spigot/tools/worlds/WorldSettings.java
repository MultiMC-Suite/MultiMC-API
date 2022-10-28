package fr.multimc.api.spigot.tools.worlds;

import fr.multimc.api.spigot.tools.schematics.Schematic;
import fr.multimc.api.spigot.tools.locations.RelativeLocation;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class WorldSettings {

    private String worldName;
    private Schematic schematic;
    private RelativeLocation spawn;
    private boolean preventPvp, preventDamages, PreventBuild, PreventFoodLoss, preventTimeFlow, preventWeather, preventPortalUse;

    public WorldSettings(@NotNull String worldName) {
        this.worldName = worldName;
        this.schematic = null;
        this.spawn = new RelativeLocation(0.5, 100, 0.5);
        this.preventDamages = false;
        this.preventPvp = false;
        this.PreventBuild = false;
        this.PreventFoodLoss = false;
        this.preventTimeFlow = false;
        this.preventWeather = false;
        this.preventPortalUse = false;
    }

    public WorldSettings(@NotNull String worldName, Schematic schematic) {
        this.worldName = worldName;
        this.schematic = schematic;
        this.spawn = new RelativeLocation(0.5, 100, 0.5);
        this.preventDamages = false;
        this.preventPvp = false;
        this.PreventBuild = false;
        this.PreventFoodLoss = false;
        this.preventTimeFlow = false;
        this.preventWeather = false;
        this.preventPortalUse = false;
    }

    public WorldSettings(@NotNull String world_name, Schematic schematic, boolean prevent_damages, boolean prevent_pvp, boolean prevent_build, boolean prevent_food, boolean preventTimeFlow, boolean preventWeather, boolean preventPortalUse) {
        this.worldName = world_name;
        this.schematic = schematic;
        this.spawn = new RelativeLocation(0.5, 100, 0.5);
        this.preventDamages = prevent_damages;
        this.preventPvp = prevent_pvp;
        this.PreventBuild = prevent_build;
        this.PreventFoodLoss = prevent_food;
        this.preventTimeFlow = preventTimeFlow;
        this.preventWeather = preventWeather;
        this.preventPortalUse = preventPortalUse;
    }

    public WorldSettings(@NotNull String world_name, Schematic schematic, RelativeLocation spawn, boolean prevent_damages, boolean prevent_pvp, boolean prevent_build, boolean prevent_food, boolean preventTimeFlow, boolean preventWeather, boolean preventPortalUse) {
        this.worldName = world_name;
        this.schematic = schematic;
        this.spawn = spawn;
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
    public Schematic getSchematic() {
        return schematic;
    }
    public RelativeLocation getSpawn() {
        return spawn;
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
