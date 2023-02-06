package fr.multimc.api.spigot.pre_made.samplecode.advancements;

import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.events.PlayerLoadingCompletedEvent;
import fr.multimc.api.spigot.advancements.AdvancementBuilder;
import fr.multimc.api.spigot.advancements.MmcAdvancementTab;
import fr.multimc.api.spigot.pre_made.samplecode.SampleCode;
import fr.multimc.api.spigot.pre_made.samplecode.advancements.advancement.GetItemAdvancement;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class AdvancementSampleCode implements SampleCode, Listener {

    private static MmcAdvancementTab advancementTab;

    @Override
    public void run(JavaPlugin plugin) {
        // Init the UltimateAdvancementAPI instance
        UltimateAdvancementAPI api = UltimateAdvancementAPI.getInstance(plugin);
        // Create the root advancement display
        AdvancementDisplay rootDisplay = new AdvancementBuilder()
                .setTitle("Sample")
                .setDescription(List.of("Sample root", "description"))
                .setIcon(Material.NETHERITE_INGOT)
                .setFrameType(AdvancementFrameType.CHALLENGE)
                .setLocation(0, 0)
                .setShowToast(false)
                .setAnnounceToChat(false)
                .build();
        // Create the advancement tab
        advancementTab = new MmcAdvancementTab(api, "sample", Material.DIAMOND_BLOCK, rootDisplay);
        // Create a new advancement (here a custom advancement)
        GetItemAdvancement advancement = new GetItemAdvancement(plugin, "apple_pie", advancementTab.getRootAdvancement());
        // Add the advancement to the tab
        advancementTab.addAdvancement(advancement);
        // Register the tab
        advancementTab.register();
        // Register events
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        api.disableVanillaAdvancements();
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoadingCompletedEvent e){
        advancementTab.getAdvancements().forEach(a -> a.revoke(e.getPlayer()));
        advancementTab.getRootAdvancement().grant(e.getPlayer());
    }
}
