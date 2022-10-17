package fr.multimc.api.spigot.tools.chat;

import net.kyori.adventure.text.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class TextBuilder {
    private final Component rs;

    public TextBuilder() {
        this.rs = Component.empty();
    }

    public TextBuilder(@Nonnull Component component) {
        this.rs = component;
    }

    public TextBuilder(@Nullable String text) {
        this.rs = Objects.isNull(text) ? new TextBuilder().build() : Component.text(text);
    }

    public TextBuilder text(@Nonnull String text) {
        this.rs.append(Component.text(text));
        return this;
    }

    public Component build() {
        return this.rs;
    }
}
