package fr.multimc.api.spigot.advancements;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.function.Consumer;

public class TriggeredAdvancement extends BaseAdvancement {
    public <E extends Event> TriggeredAdvancement(String key, AdvancementDisplay display, Advancement parent, int maxProgression, Class<E> eventClass, Consumer<E> consumer) {
        super(key, display, parent, maxProgression);
        registerEvent(eventClass, consumer);

        registerEvent(BlockBreakEvent.class, e -> {
            Player player = e.getPlayer();
            if (isVisible(player) && e.getBlock().getType() == Material.STONE) {
                incrementProgression(player);
            }
        });
    }
}
