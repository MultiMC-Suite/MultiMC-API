package fr.multimc.api.spigot.common.advancements;

import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A builder class for {@link AdvancementDisplay}.
 */
@SuppressWarnings("unused")
public class AdvancementBuilder {

    private Material icon = Material.STONE;
    private String title = "Title";
    private List<String> description = List.of("Description");
    private AdvancementFrameType frameType = AdvancementFrameType.TASK;
    private float x = 0;
    private float y = 0;
    private boolean showToast = true;
    private boolean announceToChat = true;

    /**
     * Sets the icon of the {@link AdvancementDisplay}.
     *
     * @param material the {@link Material} to use as the icon.
     * @return this builder instance.
     */
    public AdvancementBuilder setIcon(@NotNull final Material material){
        this.icon = material;
        return this;
    }

    /**
     * Sets the title of the {@link AdvancementDisplay}.
     *
     * @param title the title of the {@link AdvancementDisplay}.
     * @return this builder instance.
     */
    public AdvancementBuilder setTitle(@NotNull final String title){
        this.title = title;
        return this;
    }

    /**
     * Sets the description of the {@link AdvancementDisplay}.
     *
     * @param description the description of the {@link AdvancementDisplay}.
     * @return this builder instance.
     */
    public AdvancementBuilder setDescription(@NotNull final List<String> description){
        this.description = description;
        return this;
    }

    /**
     * Sets the frame type of the {@link AdvancementDisplay}.
     *
     * @param frameType the {@link AdvancementFrameType} of the {@link AdvancementDisplay}.
     * @return this builder instance.
     */
    public AdvancementBuilder setFrameType(@NotNull final AdvancementFrameType frameType){
        this.frameType = frameType;
        return this;
    }

    /**
     * Set the advancement location
     *
     * @param x the x coordinate of the {@link AdvancementDisplay}.
     * @param y the y coordinate of the {@link AdvancementDisplay}.
     * @return this builder instance.
     */
    public AdvancementBuilder setLocation(float x, float y){
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Sets whether the {@link AdvancementDisplay} should show a toast.
     *
     * @param showToast `true` if the {@link AdvancementDisplay} should show a toast, `false` otherwise.
     * @return this builder instance.
     */
    public AdvancementBuilder setShowToast(final boolean showToast){
        this.showToast = showToast;
        return this;
    }

    /**
     * Specifies whether the advancement should be announced in the chat.
     *
     * @param announceToChat true if the advancement should be announced in the chat, false otherwise.
     * @return the current {@link AdvancementBuilder} instance.
     */
    public AdvancementBuilder setAnnounceToChat(final boolean announceToChat){
        this.announceToChat = announceToChat;
        return this;
    }

    /**
     * Builds and returns the {@link AdvancementDisplay} object based on the previously set properties.
     *
     * @return a new instance of {@link AdvancementDisplay}.
     */
    public AdvancementDisplay build(){
        return new AdvancementDisplay(icon, title, frameType, showToast, announceToChat, x, y, description);
    }

}
