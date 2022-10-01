package fr.multimc.api.spigot;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import fr.multimc.api.commons.advancements.AdvancementType;
import fr.multimc.api.commons.advancements.AdvancementsManager;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerApi extends JavaPlugin {

    private static JavaPlugin instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        AdvancementsManager advancementsManager = new AdvancementsManager(this, true);
        AdvancementDisplay advancementDisplay = advancementsManager.getAdvancementDisplay(Material.DIAMOND, "Test Advancement", AdvancementFrameType.CHALLENGE, 5, 0, new String[]{"Description", ":p"});
        AdvancementTab tab = advancementsManager.addAdvancementTab("test", true);
        advancementsManager.addAdvancement(AdvancementType.ROOT, "test", tab, advancementDisplay, "textures/block/cobblestone.png");
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}
