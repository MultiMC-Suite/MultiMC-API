package fr.multimc.api.spigot.pre_made.samplecode.advancements.advancement;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.events.advancement.AdvancementProgressionUpdateEvent;
import fr.multimc.api.spigot.advancements.AbstractAdvancement;
import fr.multimc.api.spigot.advancements.AdvancementBuilder;
import fr.multimc.api.spigot.entities.player.MmcPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class GetItemAdvancement extends AbstractAdvancement {

    public GetItemAdvancement(Plugin plugin, String key, Advancement parent) {
        super(plugin,
                key,
                new AdvancementBuilder().setTitle("Apple pie")
                        .setDescription(List.of("Get 3 apples"))
                        .setIcon(Material.APPLE)
                        .setLocation(1.5f, 0)
                        .build(),
                parent,
                3);
    }

    @Override
    public void progressionUpdateCallback(AdvancementProgressionUpdateEvent e, MmcPlayer player) {
        player.sendActionBar(Component.text("Apple pie : %d/%d".formatted(e.getAdvancement().getProgression(e.getTeamProgression()), e.getAdvancement().getMaxProgression())));
    }

    @Override
    public void progressionCompleteCallback(AdvancementProgressionUpdateEvent e, MmcPlayer player) {
        player.sendTitle(
                Component.text("Apple pie !"),
                Component.text("Congratulations !"));
    }

    @EventHandler
    public void onPlayerGetItem(PlayerAttemptPickupItemEvent e){
        if(e.getItem().getItemStack().getType() == Material.APPLE)
            this.incrementProgression(e.getPlayer(), e.getItem().getItemStack().getAmount());
    }
}
