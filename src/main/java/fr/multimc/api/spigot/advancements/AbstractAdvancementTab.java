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
public abstract class AbstractAdvancementTab {

    private final String key;
    private final AdvancementTab advancementTab;
    private final RootAdvancement rootAdvancement;
    private final Set<BaseAdvancement> advancements;

    public AbstractAdvancementTab(UltimateAdvancementAPI api, String key, Material background, AdvancementDisplay rootDisplay) {
        if(!this.checkKey(key))
            throw new IllegalArgumentException("Invalid key: " + key);
        this.key = key;
        this.advancementTab = api.createAdvancementTab(key);
        this.rootAdvancement = new RootAdvancement(this.advancementTab, "%s:root".formatted(key), rootDisplay, BackgroundLoader.getBackground(background));
        this.advancements = new HashSet<>();
    }

    public AbstractAdvancementTab(UltimateAdvancementAPI api, String key, AdvancementDisplay rootDisplay) {
        this(api, key, Material.STONE, rootDisplay);
    }

    private boolean checkKey(String advancementKey){
        return advancementKey.matches("^[a-z0-9_.-]{1,127}:[/a-z0-9_.-]{1,127}$");
    }

    public void addAdvancement(BaseAdvancement advancement) {
        this.advancements.add(advancement);
    }

    public void addAdvancement(BaseAdvancement... advancements) {
        this.advancements.addAll(Set.of(advancements));
    }

    public void register() {
        this.advancementTab.registerAdvancements(this.rootAdvancement, this.advancements);
    }

    public String getRegisterKey(String advancementKey){
        String key = "%s:%s".formatted(this.key, advancementKey);
        if(!this.checkKey(key))
            throw new IllegalArgumentException("Invalid key: " + key);
        return key;
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
