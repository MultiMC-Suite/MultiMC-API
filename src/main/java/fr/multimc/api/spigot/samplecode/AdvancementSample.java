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

        RootAdvancement rootAdvancement = new AdvancementBuilder("Root advancement")
                .setItem(Material.NETHERITE_INGOT)
                .setAdvancementFrameType(AdvancementFrameType.CHALLENGE)
                .setDescription(new String[]{"This is the", "root advancement sample"})
                .getRootAdvancement(tab);
        BaseAdvancement advancement1 = new AdvancementBuilder("Get Wood")
                .setItem(Material.OAK_LOG)
                .setPosition(1, 0)
                .setDescription(new String[]{"This is the", "first advancement sample", "without any trigger"})
                .getAdvancement(rootAdvancement);
        BaseAdvancement advancement2 = new AdvancementBuilder("Get Diamonds")
                .setItem(Material.DIAMOND)
                .setPosition(2, 0)
                .setDescription(new String[]{"Get 5 diamonds", "for sample"})
                .getTriggeredAdvancement(advancement1, 5, EntityPickupItemEvent.class, (EntityPickupItemEvent e) -> {
                    if(e.getEntity() instanceof Player player){
                        if(e.getItem().getItemStack().getType() == Material.DIAMOND){
                            for(int i = 0; i < e.getItem().getItemStack().getAmount(); i++){
                                advancementsManager.getBaseAdvancement("Get Diamonds").incrementProgression(player);
                            }
                        }
                    }
                });
        MultiParentsAdvancement advancement3 = new AdvancementBuilder("Get emeralds")
                .setItem(Material.EMERALD)
                .setPosition(2, 1)
                .setDescription(new String[]{"Sample for multi-parent", "advancement"})
                .getMultiParentAdvancement(advancement1, advancement2);
        advancementsManager.addAdvancement(tab, rootAdvancement, advancement1, advancement2, advancement3);
    }

    @EventHandler
    public void onProgressionUpdated(ProgressionUpdateEvent e){
        UUID uuid = e.getTeamProgression().getAMember();
        if(uuid != null){
            Player player =  Bukkit.getPlayer(uuid);
            if(player != null){
                BaseAdvancement advancement = advancementsManager.getBaseAdvancement(e.getAdvancementKey().getKey());
                if(advancement != null){
                    player.sendMessage(String.format("Progression updated: %d on %d", e.getNewProgression(), advancement.getMaxProgression()));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        advancementsManager.getBaseAdvancement("Get Wood").grant(e.getPlayer());
    }
}
