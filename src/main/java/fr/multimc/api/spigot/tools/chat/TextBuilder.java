package fr.multimc.api.spigot.tools.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@SuppressWarnings("unused")
public class TextBuilder {
    private Component rs;

    public TextBuilder() {
        this.rs = Component.empty();
    }

    public TextBuilder(@Nonnull Component component) {
        this.rs = component;
    }

    public TextBuilder(@Nullable String text) {
        this.rs = Objects.isNull(text) ? new TextBuilder().build() : LegacyComponentSerializer.legacy('&').deserialize(text);
    }

    public TextBuilder text(@Nonnull String text) {
        this.rs = this.rs.append(Component.text(ChatColor.translateAlternateColorCodes('&', text)));
        return this;
    }

    public Component build() {
        return this.rs;
    }
}
