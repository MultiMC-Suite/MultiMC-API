package fr.multimc.api.spigot.tools.chat;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class ClickableMessageBuilder {
    private Component component;

    /**
     *
     */
    public ClickableMessageBuilder() {
        this.component = Component.empty();
    }

    /**
     * Create a custom message with a base.
     *
     * @param message
     */
    public ClickableMessageBuilder(@Nonnull String message) {
        this.component = Component.text(ChatColor.translateAlternateColorCodes('&', message));
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
     * @param event
     * @return
     */
    public ClickableMessageBuilder setClick(@Nonnull ClickEvent event) {
        this.component = this.component.clickEvent(event);
        return this;
    }

    /**
     *
     * @param event
     * @return
     */
    public ClickableMessageBuilder setHover(@Nonnull HoverEvent event) {
        this.component = this.component.hoverEvent(event);
        return this;
    }

    /**
     *
     * @param extra
     * @return
     */
    public ClickableMessageBuilder addExtra(@Nonnull Object extra) {
        if (extra instanceof String) this.component = this.component.append(Component.text(ChatColor.translateAlternateColorCodes('&', (String) extra)));
        if (extra instanceof Component) this.component = this.component.append((Component) extra);
        if (extra instanceof ClickableMessageBuilder) this.component = this.component.append(((ClickableMessageBuilder) extra).build());
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
    public Component build() {
        return this.component;
    }
}
