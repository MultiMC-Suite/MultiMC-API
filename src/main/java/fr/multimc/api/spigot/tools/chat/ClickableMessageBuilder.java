package fr.multimc.api.spigot.tools.chat;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEventSource;

import javax.annotation.Nonnull;
import java.util.Arrays;

@SuppressWarnings("unused")
public class ClickableMessageBuilder {
    private Component component;

    public ClickableMessageBuilder() {
        this.component = Component.empty();
    }

    public ClickableMessageBuilder(@Nonnull String message) {
        this.component = new TextBuilder(message).build();
    }

    public ClickableMessageBuilder(@Nonnull TextComponent component) {
        this.component = component;
    }

    public ClickableMessageBuilder setClick(@Nonnull ClickEvent event) {
        this.component = this.component.clickEvent(event);
        return this;
    }

    public ClickableMessageBuilder setHover(@Nonnull HoverEventSource<?> event) {
        this.component = this.component.hoverEvent(event);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ClickableMessageBuilder addExtra(@Nonnull Object extra) {
        if (extra instanceof String) this.component = this.component.append(new TextBuilder((String) extra).build());
        if (extra instanceof Component) this.component = this.component.append((Component) extra);
        if (extra instanceof ClickableMessageBuilder) this.component = this.component.append(((ClickableMessageBuilder) extra).build());
        return this;
    }

    public ClickableMessageBuilder addExtras(@Nonnull Object... extras) {
        Arrays.asList(extras).forEach(this::addExtra);
        return this;
    }

    public Component build() {
        return this.component;
    }
}
