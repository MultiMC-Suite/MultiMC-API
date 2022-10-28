package fr.multimc.api.spigot.samplecode.advancements;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import fr.multimc.api.spigot.managers.advancements.TriggeredAdvancement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class CustomAdvancementSample extends TriggeredAdvancement {

    public CustomAdvancementSample(String key, AdvancementDisplay display, Advancement parent, int maxProgression) {
        super(key, display, parent, maxProgression);

        registerNewEvent(EntityPickupItemEvent.class, (EntityPickupItemEvent e) -> {
            if(e.getEntity() instanceof Player player){
                if(e.getItem().getItemStack().getType() == Material.DIAMOND){
                    for(int i = 0; i < e.getItem().getItemStack().getAmount(); i++){
                        incrementProgression(player);
                    }
                }
            }
        });
    }

}
