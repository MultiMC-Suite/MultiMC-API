package fr.multimc.api.spigot;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.advancement.multiParents.MultiParentsAdvancement;
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
                .setPosition(1, 0)
                .getAdvancement(advancement);
        BaseAdvancement advancement2 = new AdvancementBuilder("Son3")
                .setItem(Material.EMERALD)
                .setPosition(2, 0)
                .getAdvancement(advancement1);
        MultiParentsAdvancement advancement3 = new AdvancementBuilder("Son4")
                .setItem(Material.EMERALD)
                .setPosition(2, 1)
                .getMultiParentAdvancement(advancement1, advancement2);
        tab.registerAdvancements(advancement, advancement1, advancement2, advancement3);

        System.out.println("---------------------");
        System.out.println(advancement1.getParent());
        System.out.println(advancement2.getParent());
        System.out.println(advancement3.getParents());
        System.out.println("---------------------");
    }

    public static JavaPlugin getInstance() {
        return instance;
    }
}
