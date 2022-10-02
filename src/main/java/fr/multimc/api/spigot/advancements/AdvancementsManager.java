package fr.multimc.api.spigot.advancements;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

@SuppressWarnings({"unused", "BusyWait"})
public class AdvancementsManager implements Listener {

    UltimateAdvancementAPI advancementAPI;
    private final JavaPlugin plugin;
    HashMap<String, AdvancementTab> advancementTabs;
    HashMap<String, BaseAdvancement> baseAdvancements;

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

    public AdvancementTab getAdvancementTab(String tabName){
        return this.advancementTabs.get(tabName);
    }

    public void addAdvancement(AdvancementTab advancementTab, RootAdvancement rootAdvancement){
        advancementTab.registerAdvancements(rootAdvancement);
    }

    public void addAdvancement(AdvancementTab advancementTab, BaseAdvancement advancement){
        baseAdvancements.put(advancement.getKey().getKey(), advancement);
        advancementTab.registerAdvancements(advancementTab.getRootAdvancement(), advancement);
    }

    public void addAdvancement(AdvancementTab advancementTab, RootAdvancement rootAdvancement, BaseAdvancement... advancements){
        for(BaseAdvancement advancement : advancements){
            baseAdvancements.put(advancement.getKey().getKey(), advancement);
        }
        advancementTab.registerAdvancements(rootAdvancement, advancements);
    }

    public BaseAdvancement getBaseAdvancement(String key){
        return this.baseAdvancements.get(key);
    }
}
