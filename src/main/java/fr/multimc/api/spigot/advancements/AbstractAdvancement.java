package fr.multimc.api.spigot.advancements;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.events.PlayerLoadingCompletedEvent;
import com.fren_gor.ultimateAdvancementAPI.events.advancement.AdvancementProgressionUpdateEvent;
import fr.multimc.api.spigot.advancements.enums.AdvancementProperty;
import fr.multimc.api.spigot.entities.player.MmcPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractAdvancement extends BaseAdvancement implements Listener {

    private final Set<AdvancementProperty> properties;

    public AbstractAdvancement(@NotNull final Plugin plugin,
                               @NotNull final String key,
                               @NotNull final AdvancementDisplay display,
                               @NotNull final Advancement parent,
                               @Range(from = 1L, to = 2147483647L) final int maxProgression,
                               @NotNull final AdvancementProperty... properties) {
        super(key, display, parent, maxProgression);
        this.properties = Set.of(properties);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onAdvancementProgressionUpdate(@NotNull final AdvancementProgressionUpdateEvent e){
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

    @EventHandler
    public void onPlayerJoin(@NotNull final PlayerLoadingCompletedEvent e){
        if(this.properties.contains(AdvancementProperty.RESET_ON_JOIN))
            this.revoke(e.getPlayer());
        if(this.properties.contains(AdvancementProperty.GRANT_ON_JOIN))
            this.grant(e.getPlayer());
    }

    public abstract void progressionUpdateCallback(@NotNull final AdvancementProgressionUpdateEvent e, @NotNull final MmcPlayer player);

    public abstract void progressionCompleteCallback(@NotNull final AdvancementProgressionUpdateEvent e, @NotNull final MmcPlayer player);
}
