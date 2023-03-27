package fr.multimc.api.sample.spigot.advancements.advancement;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.events.advancement.AdvancementProgressionUpdateEvent;
import fr.multimc.api.spigot.common.advancements.AbstractAdvancement;
import fr.multimc.api.spigot.common.advancements.AdvancementBuilder;
import fr.multimc.api.spigot.common.advancements.enums.AdvancementProperty;
import fr.multimc.api.spigot.common.entities.player.MmcPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetItemAdvancement extends AbstractAdvancement {

    public GetItemAdvancement(@NotNull final Plugin plugin, @NotNull final Advancement parent) {
        super(plugin,
                "apple_pie",
                new AdvancementBuilder().setTitle("Apple pie")
                        .setDescription(List.of("Get 3 apples"))
                        .setIcon(Material.APPLE)
                        .setLocation(1.5f, 0)
                        .build(),
                parent,
                3,
                AdvancementProperty.RESET_ON_JOIN);
    }

    @Override
    public void progressionUpdateCallback(@NotNull final AdvancementProgressionUpdateEvent e, @NotNull final MmcPlayer player) {
        player.sendActionBar(Component.text("Apple pie : %d/%d".formatted(e.getAdvancement().getProgression(e.getTeamProgression()), e.getAdvancement().getMaxProgression())));
    }

    @Override
    public void progressionCompleteCallback(@NotNull final AdvancementProgressionUpdateEvent e, @NotNull final MmcPlayer player) {
        player.sendTitle(
                Component.text("Apple pie !"),
                Component.text("Congratulations !"));
    }

    @EventHandler
    public void onPlayerGetItem(@NotNull final PlayerAttemptPickupItemEvent e){
        if(e.getItem().getItemStack().getType() == Material.APPLE)
            this.incrementProgression(e.getPlayer(), e.getItem().getItemStack().getAmount());
    }
}
