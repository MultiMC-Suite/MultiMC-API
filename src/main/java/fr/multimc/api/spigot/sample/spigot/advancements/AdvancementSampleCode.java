package fr.multimc.api.spigot.sample.spigot.advancements;

import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import fr.multimc.api.spigot.sample.spigot.SampleCode;
import fr.multimc.api.spigot.sample.spigot.advancements.advancement.GetItemAdvancement;
import fr.multimc.api.spigot.common.advancements.AdvancementBuilder;
import fr.multimc.api.spigot.common.advancements.MmcAdvancementTab;
import fr.multimc.api.spigot.common.advancements.enums.AdvancementProperty;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@SuppressWarnings("unused")
public class AdvancementSampleCode implements SampleCode, Listener {

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
        MmcAdvancementTab advancementTab = new MmcAdvancementTab(plugin, api, "sample", Material.DIAMOND_BLOCK, rootDisplay, AdvancementProperty.GRANT_ON_JOIN, AdvancementProperty.RESET_ON_JOIN);
        // Create a new advancement (here a custom advancement)
        GetItemAdvancement advancement = new GetItemAdvancement(plugin, advancementTab.getRootAdvancement());
        // Add the advancement to the tab
        advancementTab.addAdvancement(advancement);
        // Register the tab
        advancementTab.register();
        // Register events
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        // Disable vanilla advancements
        api.disableVanillaAdvancements();
    }
}
