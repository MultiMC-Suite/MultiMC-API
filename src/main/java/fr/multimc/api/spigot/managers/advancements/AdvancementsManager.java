package fr.multimc.api.spigot.managers.advancements;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * The advancement manager helps you to create and manage your advancements and advancements tab through the UltimateAdvancement API
 * @author Tom CZEKAJ
 * @version 1.0
 * @since 03/10/2022
 */
@SuppressWarnings({"unused", "BusyWait"})
public class AdvancementsManager implements Listener {

    private final JavaPlugin plugin;
    UltimateAdvancementAPI advancementAPI;
    HashMap<String, AdvancementTab> advancementTabs;
    HashMap<String, BaseAdvancement> baseAdvancements;

    /**
     * Constructor of the AdvancementsManager class
     * @param plugin Instance of the plugin
     * @param removeAll If vanilla advancements need to be removed
     */
    public AdvancementsManager(JavaPlugin plugin, boolean removeAll){
        this.plugin = plugin;
        this.advancementAPI = UltimateAdvancementAPI.getInstance(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        advancementTabs = new HashMap<>();
        baseAdvancements = new HashMap<>();
        if(removeAll){
            this.removeAllAdvancements();
        }
    }

    /**
     * Remove all vanilla advancements
     */
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
                    tab.automaticallyGrantRootAdvancement();
                }
            }.runTaskAsynchronously(this.plugin);
        }
        this.advancementTabs.put(tabName, tab);
        return tab;
    }

    /**
     * Add the root advancements of a tab
     * @param advancementTab The advancement tab instance
     * @param rootAdvancement The root advancement
     */
    public void addAdvancement(AdvancementTab advancementTab, RootAdvancement rootAdvancement){
        advancementTab.registerAdvancements(rootAdvancement);
    }

    /**
     * Add an advancement to an advancement tab
     * @param advancementTab The advancement tab instance
     * @param advancement The advancement
     */
    public void addAdvancement(AdvancementTab advancementTab, BaseAdvancement advancement){
        baseAdvancements.put(advancement.getKey().getKey(), advancement);
        advancementTab.registerAdvancements(advancementTab.getRootAdvancement(), advancement);
    }

    /**
     * Add one or more advancements to an advancement tab
     * @param advancementTab The advancement tab instance
     * @param rootAdvancement The root advancement
     * @param advancements One or more advancements
     */
    public void addAdvancement(AdvancementTab advancementTab, RootAdvancement rootAdvancement, BaseAdvancement... advancements){
        for(BaseAdvancement advancement : advancements){
            baseAdvancements.put(advancement.getKey().getKey(), advancement);
        }
        advancementTab.registerAdvancements(rootAdvancement, advancements);
    }

    /**
     * Get an advancement tab from it name
     * @param tabName Name of the advancement tab
     * @return The advancement tab instance
     */
    public AdvancementTab getAdvancementTab(String tabName){
        return this.advancementTabs.get(tabName);
    }

    /**
     * Get an advancement from it name
     * @param name Name of the advancement
     * @return The advancement instance
     */
    public BaseAdvancement getBaseAdvancement(String name){
        return this.baseAdvancements.get(AdvancementBuilder.getAdvancementKey(name));
    }
}
