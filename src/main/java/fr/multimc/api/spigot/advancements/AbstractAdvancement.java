package fr.multimc.api.spigot.advancements;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.events.advancement.AdvancementProgressionUpdateEvent;
import fr.multimc.api.spigot.entities.player.MmcPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractAdvancement extends BaseAdvancement implements Listener {

    public AbstractAdvancement(Plugin plugin, String key, AdvancementDisplay display, Advancement parent, @Range(from = 1L, to = 2147483647L) int maxProgression) {
        super(key, display, parent, maxProgression);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onAdvancementProgressionUpdate(AdvancementProgressionUpdateEvent e){
        if(e.getAdvancement().getKey().equals(this.getKey())){
            UUID playerUUID = e.getTeamProgression().getAMember();
            if(Objects.nonNull(playerUUID)){
                MmcPlayer player = new MmcPlayer(playerUUID);
                this.progressionUpdateCallback(e, player);
                if(e.getAdvancement().getProgression(e.getTeamProgression()) == e.getAdvancement().getMaxProgression())
                    this.progressionCompleteCallback(e, player);
            }
        }
    }

    public abstract void progressionUpdateCallback(AdvancementProgressionUpdateEvent e, MmcPlayer player);

    public abstract void progressionCompleteCallback(AdvancementProgressionUpdateEvent e, MmcPlayer player);
}
