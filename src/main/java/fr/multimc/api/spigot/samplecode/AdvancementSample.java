package fr.multimc.api.spigot.samplecode;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.advancement.multiParents.MultiParentsAdvancement;
import com.fren_gor.ultimateAdvancementAPI.events.advancement.ProgressionUpdateEvent;
import fr.multimc.api.spigot.advancements.AdvancementBuilder;
import fr.multimc.api.spigot.advancements.AdvancementsManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class AdvancementSample implements SampleCode, Listener {

    private static AdvancementsManager advancementsManager;

    @Override
    public void run(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        advancementsManager = new AdvancementsManager(plugin, true);
        AdvancementTab tab = advancementsManager.addAdvancementTab("test", true);

        RootAdvancement advancement = new AdvancementBuilder("Builder test")
                .setItem(Material.STICK)
                .setAdvancementFrameType(AdvancementFrameType.CHALLENGE)
                .setDescription(new String[]{"Description", ":p"})
                .getRootAdvancement(tab);
        BaseAdvancement advancement1 = new AdvancementBuilder("Son2")
                .setItem(Material.AXOLOTL_SPAWN_EGG)
                .setPosition(1, 0)
                .getAdvancement(advancement);
        BaseAdvancement advancement2 = new AdvancementBuilder("Son3")
                .setItem(Material.DIAMOND)
                .setPosition(2, 0)
                .setDescription(new String[]{"Get 5 diamonds"})
                .getTriggeredAdvancement(advancement1, 5, EntityPickupItemEvent.class, (EntityPickupItemEvent e) -> {
                    if(e.getEntity() instanceof Player player){
                        if(e.getItem().getItemStack().getType() == Material.DIAMOND){
                            for(int i = 0; i < e.getItem().getItemStack().getAmount(); i++){
                                advancementsManager.getBaseAdvancement("son3").incrementProgression(player);
                            }
                        }
                    }
                });
        MultiParentsAdvancement advancement3 = new AdvancementBuilder("Son4")
                .setItem(Material.EMERALD)
                .setPosition(2, 1)
                .getMultiParentAdvancement(advancement1, advancement2);
        advancementsManager.addAdvancement(tab, advancement, advancement1, advancement2, advancement3);
    }

    @EventHandler
    public void onProgressionUpdated(ProgressionUpdateEvent e){
        UUID uuid = e.getTeamProgression().getAMember();
        if(uuid != null){
            Player player =  Bukkit.getPlayer(uuid);
            if(player != null){
                player.sendMessage(String.format("Progression updated: %d on %d", e.getNewProgression(), advancementsManager.getBaseAdvancement(e.getAdvancementKey().getKey()).getMaxProgression()));
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        advancementsManager.getBaseAdvancement("son2").grant(e.getPlayer());
    }
}
