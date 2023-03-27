package fr.multimc.api.spigot.games.settings;

import fr.multimc.api.commons.tools.messages.MessagesFactory;
import fr.multimc.api.spigot.common.worlds.MmcWorld;
import fr.multimc.api.spigot.games.GameInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record GamesManagerSettings(
        @NotNull GameSettings gameSettings,
        @NotNull Class<? extends GameInstance> instanceClass,
        @NotNull MmcWorld lobbyWorld,
        @NotNull MmcWorld gameWorld,
        @Nullable MessagesFactory messagesFactory) {
}
