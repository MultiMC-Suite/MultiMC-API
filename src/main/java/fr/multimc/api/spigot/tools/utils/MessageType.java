package fr.multimc.api.spigot.tools.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public enum MessageType {
    PREFIXED(null, "PLUGIN_NAME", NamedTextColor.WHITE),
    GAME("!", "SHOUT", NamedTextColor.GRAY),
    TEAM(null, "TEAM", NamedTextColor.GRAY);

    private final String chatPrefix;
    private final String typeName;
    private final TextColor color;

    MessageType(@Nullable String chatPrefix, @NotNull String display, @NotNull TextColor color) {
        this.chatPrefix = chatPrefix;
        this.typeName = display;
        this.color = color;
    }

    @NotNull
    public Component getComponent(){
        return format(this.typeName, this.color);
    }

    @NotNull
    public String getValue(){
        return this.typeName;
    }

    @Nullable
    public String getChatPrefix() {
        return chatPrefix;
    }

    public static Component format(String text, TextColor color){
        return Component.text(text, color, TextDecoration.BOLD);
    }
}
