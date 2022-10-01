package fr.multimc.api.spigot.tools.chat;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import javax.annotation.Nonnull;
import java.util.Arrays;

@Deprecated
public class ClickableMessageBuilder {
    private final TextComponent component;

    /**
     *
     */
    public ClickableMessageBuilder() {
        this.component = new TextComponent();
    }

    /**
     *
     * @param message
     */
    public ClickableMessageBuilder(@Nonnull String message) {
        this.component = new TextComponent(message);
    }

    /**
     *
     * @param component
     */
    public ClickableMessageBuilder(@Nonnull TextComponent component) {
        this.component = component;
    }

    /**
     *
     * @param action
     * @param value
     * @return
     */
    public ClickableMessageBuilder setClick(@Nonnull ClickEvent.Action action, @Nonnull String value) {
        this.component.setClickEvent(new ClickEvent(action, value));
        return this;
    }

    /**
     *
     * @param action
     * @param value
     * @return
     */
    public ClickableMessageBuilder setHover(@Nonnull HoverEvent.Action action, @Nonnull String value) {
        this.component.setHoverEvent(new HoverEvent(action, new ComponentBuilder(value).create()));
        return this;
    }

    /**
     *
     * @param extra
     * @return
     */
    public ClickableMessageBuilder addExtra(@Nonnull Object extra) {
        if (extra instanceof String) this.component.addExtra((String) extra);
        if (extra instanceof TextComponent) this.component.addExtra((TextComponent) extra);
        if (extra instanceof ClickableMessageBuilder) this.component.addExtra(((ClickableMessageBuilder) extra).build());
        return this;
    }

    /**
     *
     * @param extras
     * @return
     */
    public ClickableMessageBuilder addExtras(@Nonnull Object... extras) {
        Arrays.asList(extras).forEach(this::addExtra);
        return this;
    }

    /**
     *
     * @return
     */
    public TextComponent build() {
        return this.component;
    }
}
