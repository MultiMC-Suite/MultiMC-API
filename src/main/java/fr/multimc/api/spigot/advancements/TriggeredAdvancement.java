package fr.multimc.api.spigot.advancements;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import org.bukkit.event.Event;

import java.util.function.Consumer;

/**
 * The TriggeredAdvancement object helps you to create custom advancements with custom triggers
 * @author Tom CZEKAJ
 * @version 1.0
 * @since 04/10/2022
 */
public class TriggeredAdvancement extends BaseAdvancement {

    /**
     * Constructor of the TriggeredAdvancement class
     * @param key Advancement key
     * @param display Custom AdvancementDisplay instance
     * @param parent Parent advancement
     * @param maxProgression Max progression
     */
    public TriggeredAdvancement(String key, AdvancementDisplay display, Advancement parent, int maxProgression) {
        super(key, display, parent, maxProgression);
    }

    /**
     * Used to add an event listener to the advancement
     * @param eventClass org.bukkit.event.Event class
     * @param consumer Action done when the event is called
     * @param <E> Custom org.bukkit.event.Event class
     */
    public <E extends Event> void registerNewEvent(Class<E> eventClass, Consumer<E> consumer) {
        registerEvent(eventClass, consumer);
    }
}
