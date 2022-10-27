package fr.multimc.api.spigot.managers.worlds;

import fr.multimc.api.spigot.managers.schematics.Schematic;
import fr.multimc.api.spigot.tools.locations.RelativeLocation;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class WorldSettings {

    private String worldName;
    private boolean preventPvp, preventDamages, PreventBuild, PreventFoodLoss, preventTimeFlow;
    private Schematic schematic;
    private RelativeLocation spawn;

    public WorldSettings(@NotNull String worldName) {
        this.worldName = worldName;
        this.schematic = null;
        this.preventDamages = false;
        this.preventPvp = false;
        this.PreventBuild = false;
        this.PreventFoodLoss = false;
        this.preventTimeFlow = false;
        this.spawn = new RelativeLocation(0.5, 100, 0.5);
    }

    public WorldSettings(@NotNull String worldName, Schematic schematic) {
        this.worldName = worldName;
        this.schematic = schematic;
        this.preventDamages = false;
        this.preventPvp = false;
        this.PreventBuild = false;
        this.PreventFoodLoss = false;
        this.preventTimeFlow = false;
        this.spawn = new RelativeLocation(0.5, 100, 0.5);
    }

    public WorldSettings(@NotNull String world_name, Schematic schematic, boolean prevent_damages, boolean prevent_pvp, boolean prevent_build, boolean prevent_food, boolean preventTimeFlow) {
        this.worldName = world_name;
        this.preventDamages = prevent_damages;
        this.schematic = schematic;
        this.preventPvp = prevent_pvp;
        this.PreventBuild = prevent_build;
        this.PreventFoodLoss = prevent_food;
        this.preventTimeFlow = preventTimeFlow;
        this.spawn = new RelativeLocation(0.5, 100, 0.5);
    }

    public WorldSettings(@NotNull String world_name, Schematic schematic, boolean prevent_damages, boolean prevent_pvp, boolean prevent_build, boolean prevent_food, boolean preventTimeFlow, RelativeLocation spawn) {
        this.worldName = world_name;
        this.preventDamages = prevent_damages;
        this.schematic = schematic;
        this.preventPvp = prevent_pvp;
        this.PreventBuild = prevent_build;
        this.PreventFoodLoss = prevent_food;
        this.preventTimeFlow = preventTimeFlow;
        this.spawn = spawn;
    }

    public boolean isPreventTimeFlow() {
        return preventTimeFlow;
    }

    public void setPreventTimeFlow(boolean preventTimeFlow) {
        this.preventTimeFlow = preventTimeFlow;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public boolean isPreventPvp() {
        return preventPvp;
    }

    public void setPreventPvp(boolean preventPvp) {
        this.preventPvp = preventPvp;
    }

    public boolean isPreventDamages() {
        return preventDamages;
    }

    public void setPreventDamages(boolean preventDamages) {
        this.preventDamages = preventDamages;
    }

    public boolean isPreventBuild() {
        return PreventBuild;
    }

    public void setPreventBuild(boolean preventBuild) {
        PreventBuild = preventBuild;
    }

    public boolean isPreventFoodLoss() {
        return PreventFoodLoss;
    }

    public void setPreventFoodLoss(boolean preventFoodLoss) {
        PreventFoodLoss = preventFoodLoss;
    }

    public Schematic getSchematic() {
        return schematic;
    }

    public void setSchematic(Schematic schematic) {
        this.schematic = schematic;
    }

    public RelativeLocation getSpawn() {
        return spawn;
    }

    public void setSpawn(RelativeLocation spawn) {
        this.spawn = spawn;
    }
}
