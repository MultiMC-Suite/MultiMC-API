package fr.multimc.api.spigot.advancements;

import com.fren_gor.ultimateAdvancementAPI.AdvancementTab;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.RootAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.advancement.multiParents.MultiParentsAdvancement;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class AdvancementBuilder {

    private ItemStack item;
    private final String name;
    private AdvancementFrameType advancementFrameType;
    private float posX;
    private float posY;
    private String[] description;
    private boolean showToast;
    private boolean announceChat;
    private String backgroundTexture;

    /**
     * Advancement builder constructor
     * @param name Advancement name
     */
    public AdvancementBuilder(String name){
        this.name = name;
        this.item = new ItemStack(Material.DIRT);
        this.advancementFrameType = AdvancementFrameType.TASK;
        this.posX = 0;
        this.posY = 0;
        this.description = new String[]{};
        this.announceChat = true;
        this.showToast = true;
        this.backgroundTexture = "textures/block/dirt.png";
    }

    /**
     * Get root advancement from builder
     * @param advancementTab Advancement tab
     * @return Root advancement
     */
    public RootAdvancement getRootAdvancement(AdvancementTab advancementTab){
        return new RootAdvancement(advancementTab, this.getAdvancementKey(), this.getAdvancementDisplay(), this.backgroundTexture);
    }

    /**
     * Get advancement from builder
     * @param parentAdvancement Parent advancement
     * @return Advancement
     */
    public BaseAdvancement getAdvancement(BaseAdvancement parentAdvancement){
        return new BaseAdvancement(this.getAdvancementKey(), this.getAdvancementDisplay(), parentAdvancement);
    }
    public <E extends Event> BaseAdvancement getTriggeredAdvancement(BaseAdvancement parentAdvancement, int maxProgression, Class<E> eventClass, Consumer<E> consumer){
        return new TriggeredAdvancement(this.getAdvancementKey(), this.getAdvancementDisplay(), parentAdvancement, maxProgression, eventClass, consumer);
    }
    /**
     * Get advancement from builder
     * @param parentAdvancement Parent advancement
     * @return Advancement
     */
    public BaseAdvancement getAdvancement(RootAdvancement parentAdvancement){
        return new BaseAdvancement(this.getAdvancementKey(), this.getAdvancementDisplay(), parentAdvancement);
    }

    public MultiParentsAdvancement getMultiParentAdvancement(BaseAdvancement... parentAdvancements){
        return new MultiParentsAdvancement(this.getAdvancementKey(), this.getAdvancementDisplay(), parentAdvancements);
    }



    private AdvancementDisplay getAdvancementDisplay(){
        return new AdvancementDisplay(item, name, advancementFrameType, this.showToast, this.announceChat, posX, posY, description);
    }

    /**
     * Get advancement key from advancement name (base name is lowercase and spaces are replaced by underscores)
     * @return Advancement key
     */
    private String getAdvancementKey(){
        return this.name.toLowerCase().replace(' ', '_');
    }

    public AdvancementBuilder setItem(ItemStack item){
        this.item = item;
        return this;
    }
    public AdvancementBuilder setItem(Material material){
        this.item = new ItemStack(material);
        return this;
    }
    public AdvancementBuilder setAdvancementFrameType(AdvancementFrameType advancementFrameType){
        this.advancementFrameType = advancementFrameType;
        return this;
    }
    public AdvancementBuilder setPosition(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
        return this;
    }
    public AdvancementBuilder setDescription(String[] description) {
        this.description = description;
        return this;
    }
    public AdvancementBuilder setShowToast(boolean showToast) {
        this.showToast = showToast;
        return this;
    }
    public AdvancementBuilder setAnnounceChat(boolean announceChat) {
        this.announceChat = announceChat;
        return this;
    }
    public AdvancementBuilder setBackgroundTexture(String backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
        return this;
    }
}
