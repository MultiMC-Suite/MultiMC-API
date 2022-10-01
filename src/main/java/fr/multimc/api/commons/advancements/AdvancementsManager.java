package fr.multimc.api.commons.advancements;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class AdvancementsManager implements Listener {

    UltimateAdvancementAPI advancementAPI;
    private final JavaPlugin plugin;
    List<AdvancementTab> advancementTabs;

    public AdvancementsManager(JavaPlugin plugin, boolean removeAll){
        this.plugin = plugin;
        this.advancementAPI = UltimateAdvancementAPI.getInstance(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        advancementTabs = new ArrayList<>();
        if(removeAll){
            this.removeAllAdvancements();
        }
    }

    private void removeAllAdvancements(){
        advancementAPI.disableVanillaAdvancements();
    }

    public AdvancementTab addAdvancementTab(String tabName, boolean autoGrantRoot){
        AdvancementTab tab = this.advancementAPI.createAdvancementTab(tabName);
        if(autoGrantRoot){
            new BukkitRunnable() {
                @Override
                public void run() {
                    while (!tab.isInitialised()){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println("Granting root advancement");
                    tab.automaticallyGrantRootAdvancement();
                }
            }.runTaskAsynchronously(this.plugin);
        }
        this.advancementTabs.add(tab);
        return tab;
    }

    public AdvancementTab getAdvancementTab(String tabName){
        return this.advancementAPI.getAdvancementTab(tabName);
    }

    public AdvancementDisplay getAdvancementDisplay(Material material, String name, AdvancementFrameType advancementFrameType, float posX, float posY, String[] description){
        return new AdvancementDisplay(material, name, advancementFrameType, true, true, posX, posY, description);
    }

    public void addAdvancement(AdvancementType advancementType, String advancementId, AdvancementTab advancementTab, AdvancementDisplay advancementDisplay, String texture){
        switch (advancementType) {
            case ROOT -> {
                RootAdvancement rootAdvancement = new RootAdvancement(advancementTab, advancementId, advancementDisplay, texture);
                advancementTab.registerAdvancements(rootAdvancement);
            }
            case CLASSIC -> {
                // TODO
            }
        }
    }
}
