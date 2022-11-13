package fr.multimc.api.spigot.tools.messages;

import fr.multimc.api.spigot.tools.messages.enums.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "ReassignedVariable"})
public class MessagesFactory {

    private final Component prefix;

    public MessagesFactory(String prefix) {
        this.prefix = new ComponentBuilder(prefix).build();
    }

    public MessagesFactory(Component prefix) {
        this.prefix = prefix;
    }

    public Component getMessage(@NotNull MessageType messageType, @NotNull String playerName, @NotNull String message, @Nullable String teamName){
        return this.getMessage(messageType, new ComponentBuilder(playerName).build(), new ComponentBuilder(message).build(), new ComponentBuilder(teamName).build());
    }

    public Component getMessage(@NotNull MessageType messageType, @NotNull Component playerName, @NotNull Component message, @Nullable Component teamName){
        if(teamName == null)
            teamName = Component.empty();
        switch (messageType){
            case GAME -> {
                return Component.text()
                        .append(MessageType.GAME.getComponent())
                        .append(Component.text(" | ", NamedTextColor.GRAY, TextDecoration.BOLD))
                        .append(this.getTeamedMessage(message, playerName, teamName))
                        .build();
            }
            case TEAM -> {
                return Component.text()
                        .append(MessageType.TEAM.getComponent())
                        .append(Component.text(" | ", NamedTextColor.GRAY, TextDecoration.BOLD))
                        .append(this.getPlayerMessage(message, playerName))
                        .build();
            }
            default -> {
                return Component.text()
                        .append(prefix.decorate(TextDecoration.BOLD))
                        .append(Component.text(" | ", NamedTextColor.GRAY, TextDecoration.BOLD))
                        .append(this.getPlayerMessage(message, playerName))
                        .build();
            }
        }
    }

    private Component getPlayerMessage(Component message, Component playerName){
        return Component.text()
                .append(playerName)
                .append(Component.text(" » ", NamedTextColor.GRAY, TextDecoration.BOLD))
                .append(message)
                .build();
    }

    private Component getTeamedMessage(@NotNull Component message, @NotNull Component playerName, @NotNull Component teamName){
        return Component.text()
                .append(teamName.decorate(TextDecoration.ITALIC))
                .append(Component.text(" "))
                .append(this.getPlayerMessage(message, playerName))
                .build();
    }

}
