package fr.multimc.api.commons.tools.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("unused")
public class ComponentBuilder {
    private Component component;

    public ComponentBuilder() {
        this.component = Component.empty();
    }

    public ComponentBuilder(final @NotNull Component component) {
        this.component = component;
    }

    public ComponentBuilder(final @Nullable String text) {
        this.component = Objects.isNull(text) ? Component.empty() : LegacyComponentSerializer.legacy('&').deserialize(text);
    }

    public ComponentBuilder hover(final @NotNull HoverEventSource<?> event) {
        this.component = this.component.hoverEvent(event);
        return this;
    }

    public ComponentBuilder click(final @NotNull ClickEvent event) {
        this.component = this.component.clickEvent(event);
        return this;
    }

    public ComponentBuilder extra(final @NotNull Component component) {
        this.component = this.component.append(component);
        return this;
    }

    public Component build() {
        return this.component;
    }
}
