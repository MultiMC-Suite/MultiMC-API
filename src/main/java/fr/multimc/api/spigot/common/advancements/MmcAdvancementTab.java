package fr.multimc.api.spigot.common.advancements;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.events.PlayerLoadingCompletedEvent;
import fr.multimc.api.spigot.common.advancements.enums.AdvancementProperty;
import fr.multimc.api.spigot.common.advancements.utils.BackgroundLoader;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public class MmcAdvancementTab implements Listener {

    private final AdvancementTab advancementTab;
    private final RootAdvancement rootAdvancement;
    private final Set<AbstractAdvancement> advancements;
    private final Set<AdvancementProperty> properties;

    private boolean registered = false;

    /**
     * Creates a new MmcAdvancementTab instance with the specified key, background material and root display.
     * @param api the {@link UltimateAdvancementAPI} instance
     * @param key the key of {@link AdvancementTab}
     * @param background the background {@link Material} of the {@link AdvancementTab}
     * @param rootDisplay the root {@link AdvancementDisplay} of the {@link AdvancementTab}
     * @throws IllegalArgumentException if the key is invalid
     */
    public MmcAdvancementTab(@NotNull final Plugin plugin,
                             @NotNull final UltimateAdvancementAPI api,
                             @NotNull final String key,
                             @NotNull final Material background,
                             @NotNull final AdvancementDisplay rootDisplay,
                             @NotNull final AdvancementProperty... properties) {
        if(!key.matches("^[a-z0-9_.-]{1,127}$"))
            throw new IllegalArgumentException("Invalid key: " + key);
        this.advancementTab = api.createAdvancementTab(key);
        this.rootAdvancement = new RootAdvancement(this.advancementTab, "root", rootDisplay, BackgroundLoader.getBackground(background));
        this.advancements = new HashSet<>();
        this.properties = Set.of(properties);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Creates a new {@link MmcAdvancementTab} instance with the specified key and root {@link AdvancementDisplay}, with a default background {@link Material} of Material.STONE.
     * @param api the {@link UltimateAdvancementAPI} instance
     * @param key the key of the {@link AdvancementTab}
     * @param rootDisplay the root {@link AdvancementDisplay} of the {@link AdvancementTab}
     */
    public MmcAdvancementTab(@NotNull final Plugin plugin,
                             @NotNull final UltimateAdvancementAPI api,
                             @NotNull final String key,
                             @NotNull final AdvancementDisplay rootDisplay,
                             @NotNull final AdvancementProperty... properties) {
        this(plugin, api, key, Material.STONE, rootDisplay, properties);
    }

    /**
     * Adds {@link BaseAdvancement} to the {@link AdvancementTab}.
     * @param advancements the {@link BaseAdvancement} to be added
     * @throws IllegalStateException if the {@link AdvancementTab} has already been registered
     */
    public void addAdvancement(@NotNull final AbstractAdvancement... advancements) {
        if(this.registered)
            throw new IllegalStateException("AdvancementTab already registered");
        this.advancements.addAll(Set.of(advancements));
    }

    /**
     * Registers the {@link AdvancementTab}.
     * @throws IllegalStateException if the {@link AdvancementTab} has already been registered
     */
    public void register() {
        if(this.registered)
            throw new IllegalStateException("AdvancementTab already registered");
        this.advancementTab.registerAdvancements(this.rootAdvancement, new HashSet<>(this.advancements));
        this.registered = true;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(@NotNull final PlayerLoadingCompletedEvent e){
        // Put the REMOVE_ON_LEAVE here because of API limitations
        if(this.properties.contains(AdvancementProperty.RESET_ON_JOIN)){
            this.rootAdvancement.revoke(e.getPlayer());
            if(this.properties.contains(AdvancementProperty.APPLY_ALL))
                this.advancements.forEach(advancement -> advancement.revoke(e.getPlayer()));
        }
        if(this.properties.contains(AdvancementProperty.GRANT_ON_JOIN)){
            this.rootAdvancement.grant(e.getPlayer());
            if(this.properties.contains(AdvancementProperty.APPLY_ALL))
                this.advancements.forEach(advancement -> advancement.grant(e.getPlayer()));
        }
    }

    public AdvancementTab getAdvancementTab() {
        return advancementTab;
    }
    public RootAdvancement getRootAdvancement() {
        return rootAdvancement;
    }
    public Set<AbstractAdvancement> getAdvancements() {
        return advancements;
    }
}
