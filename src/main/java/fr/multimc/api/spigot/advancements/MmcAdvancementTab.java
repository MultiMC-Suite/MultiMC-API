package fr.multimc.api.spigot.advancements;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import fr.multimc.api.spigot.advancements.utils.BackgroundLoader;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public class MmcAdvancementTab {

    private final AdvancementTab advancementTab;
    private final RootAdvancement rootAdvancement;
    private final Set<BaseAdvancement> advancements;

    private boolean registered = false;

    /**
     * Creates a new MmcAdvancementTab instance with the specified key, background material and root display.
     * @param api the {@link UltimateAdvancementAPI} instance
     * @param key the key of {@link AdvancementTab}
     * @param background the background {@link Material} of the {@link AdvancementTab}
     * @param rootDisplay the root {@link AdvancementDisplay} of the {@link AdvancementTab}
     * @throws IllegalArgumentException if the key is invalid
     */
    public MmcAdvancementTab(UltimateAdvancementAPI api, String key, Material background, AdvancementDisplay rootDisplay) {
        if(!key.matches("^[a-z0-9_.-]{1,127}$"))
            throw new IllegalArgumentException("Invalid key: " + key);
        this.advancementTab = api.createAdvancementTab(key);
        this.rootAdvancement = new RootAdvancement(this.advancementTab, "root", rootDisplay, BackgroundLoader.getBackground(background));
        this.advancements = new HashSet<>();
    }

    /**
     * Creates a new {@link MmcAdvancementTab} instance with the specified key and root {@link AdvancementDisplay}, with a default background {@link Material} of Material.STONE.
     * @param api the {@link UltimateAdvancementAPI} instance
     * @param key the key of the {@link AdvancementTab}
     * @param rootDisplay the root {@link AdvancementDisplay} of the {@link AdvancementTab}
     */
    public MmcAdvancementTab(UltimateAdvancementAPI api, String key, AdvancementDisplay rootDisplay) {
        this(api, key, Material.STONE, rootDisplay);
    }

    /**
     * Adds {@link BaseAdvancement} to the {@link AdvancementTab}.
     * @param advancements the {@link BaseAdvancement} to be added
     * @throws IllegalStateException if the {@link AdvancementTab} has already been registered
     */
    public void addAdvancement(BaseAdvancement... advancements) {
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
        this.advancementTab.registerAdvancements(this.rootAdvancement, this.advancements);
        this.registered = true;
    }

    public AdvancementTab getAdvancementTab() {
        return advancementTab;
    }
    public RootAdvancement getRootAdvancement() {
        return rootAdvancement;
    }
    public Set<BaseAdvancement> getAdvancements() {
        return advancements;
    }
}
