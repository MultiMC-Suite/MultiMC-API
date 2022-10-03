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

/**
 * The advancement builder helps you to create your advancements through the UltimateAdvancement API
 * @author Tom CZEKAJ
 * @version 1.0
 * @since 03/10/2022
 */
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
     * Constructor of the AdvancementBuilder class
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
     * Get RootAdvancement instance from builder
     * @param advancementTab AdvancementTab instance
     * @return RootAdvancement instance
     */
    public RootAdvancement getRootAdvancement(AdvancementTab advancementTab){
        return new RootAdvancement(advancementTab, getAdvancementKey(this.name), this.getAdvancementDisplay(), this.backgroundTexture);
    }

    /**
     * Get a BaseAdvancement instance from builder
     * @param parentAdvancement Parent BaseAdvancement instance
     * @return BaseAdvancement instance
     */
    public BaseAdvancement getAdvancement(BaseAdvancement parentAdvancement){
        return new BaseAdvancement(getAdvancementKey(this.name), this.getAdvancementDisplay(), parentAdvancement);
    }

    /**
     * Get a BaseAdvancement instance from builder
     * @param parentAdvancement Parent RootAdvancement instance
     * @return BaseAdvancement instance
     */
    public BaseAdvancement getAdvancement(RootAdvancement parentAdvancement){
        return new BaseAdvancement(getAdvancementKey(this.name), this.getAdvancementDisplay(), parentAdvancement);
    }

    /**
     * Get a MultiParentsAdvancement instance from builder
     * @param parentAdvancements Parents BaseAdvancement instances
     * @return MultiParentAdvancement instance
     */
    public MultiParentsAdvancement getMultiParentAdvancement(BaseAdvancement... parentAdvancements){
        return new MultiParentsAdvancement(getAdvancementKey(this.name), this.getAdvancementDisplay(), parentAdvancements);
    }

    /**
     * Get BaseAdvancement instance with custom action trigger from builder
     * @param parentAdvancement Parent BaseAdvancement instance
     * @param maxProgression Max progression of the defined action
     * @param eventClass Trigger event class
     * @param consumer Trigger event action (lambda)
     * @return BaseAdvancement instance
     * @param <E> A class that extends org.bukkit.event.Event
     */
    public <E extends Event> BaseAdvancement getTriggeredAdvancement(BaseAdvancement parentAdvancement, int maxProgression, Class<E> eventClass, Consumer<E> consumer){
        return new TriggeredAdvancement(getAdvancementKey(this.name), this.getAdvancementDisplay(), parentAdvancement, maxProgression, eventClass, consumer);
    }

    /**
     * Get an AdvancementDisplay instance from builder's variables
     * @return An AdvancementDisplay instance
     */
    private AdvancementDisplay getAdvancementDisplay(){
        return new AdvancementDisplay(item, name, advancementFrameType, this.showToast, this.announceChat, posX, posY, description);
    }

    /**
     * Set advancement display item
     * @param item ItemStack instance
     * @return Local instance of AdvancementBuilder
     */
    public AdvancementBuilder setItem(ItemStack item){
        this.item = item;
        return this;
    }

    /**
     * Set advancement display item
     * @param material Material enum value
     * @return Local instance of AdvancementBuilder
     */
    public AdvancementBuilder setItem(Material material){
        this.item = new ItemStack(material);
        return this;
    }

    /**
     * Set advancement frame type (can be TASK, CHALLENGE or GOAL)
     * @param advancementFrameType AdvancementFrameType enum value
     * @return Local instance of AdvancementBuilder
     */
    public AdvancementBuilder setAdvancementFrameType(AdvancementFrameType advancementFrameType){
        this.advancementFrameType = advancementFrameType;
        return this;
    }

    /**
     * Set advancement display position on the advancement tab
     * @param posX X position
     * @param posY Y position
     * @return Local instance of AdvancementBuilder
     */
    public AdvancementBuilder setPosition(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
        return this;
    }

    /**
     * Set advancement description
     * @param description Description
     * @return Local instance of AdvancementBuilder
     */
    public AdvancementBuilder setDescription(String[] description) {
        this.description = description;
        return this;
    }

    /**
     * Set if the advancement need to be displayed as a toast when obtained by a player
     * @param showToast True if the advancement need to be displayed as a toast
     * @return Local instance of AdvancementBuilder
     */
    public AdvancementBuilder setShowToast(boolean showToast) {
        this.showToast = showToast;
        return this;
    }

    /**
     * Set if the advancement need to be announced to the chat when obtained by a player
     * @param announceChat True if the advancement need to be announced to the chat
     * @return Local instance of AdvancementBuilder
     */
    public AdvancementBuilder setAnnounceChat(boolean announceChat) {
        this.announceChat = announceChat;
        return this;
    }

    /**
     * Set advancement tab background texture for advancement tab (only needed for root advancement)
     * @param backgroundTexture Background texture location (for dirt: textures/block/dirt.png)
     * @return Local instance of AdvancementBuilder
     */
    public AdvancementBuilder setBackgroundTexture(String backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
        return this;
    }

    /**
     * Get advancement key from advancement name (base name is lowercase and spaces are replaced by underscores)
     * @return Advancement key
     */
    public static String getAdvancementKey(String advancementName){
        return advancementName.toLowerCase().replace(' ', '_');
    }
}
