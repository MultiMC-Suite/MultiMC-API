package fr.multimc.api.spigot;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import fr.multimc.api.spigot.advancements.AdvancementBuilder;
import fr.multimc.api.spigot.advancements.AdvancementsManager;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
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
        AdvancementTab tab = advancementsManager.addAdvancementTab("test", true);

        RootAdvancement advancement = new AdvancementBuilder("Builder test")
                .setItem(Material.STICK)
                .setAdvancementFrameType(AdvancementFrameType.CHALLENGE)
                .setDescription(new String[]{"Description", ":p"})
                .getRootAdvancement(tab);
        BaseAdvancement advancement1 = new AdvancementBuilder("Son2")
                .setItem(Material.DIAMOND)
                .setPosition(0, 1)
                .getAdvancement(advancement);
        BaseAdvancement advancement2 = new AdvancementBuilder("Son3")
                .setItem(Material.EMERALD)
                .setPosition(0, 2)
                .getAdvancement(advancement1);
        tab.registerAdvancements(advancement, advancement1, advancement2);
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}
