package fr.multimc.api.spigot.managers.games.settings;

import fr.multimc.api.commons.tools.messages.MessagesFactory;
import fr.multimc.api.spigot.managers.games.GameInstance;
import fr.multimc.api.spigot.scoreboards.interfaces.IScoreBoard;
import fr.multimc.api.spigot.worlds.MmcWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record GamesManagerSettings(
        @NotNull GameSettings gameSettings,
        @NotNull Class<? extends GameInstance> instanceClass,
        @NotNull MmcWorld lobbyWorld,
        @NotNull MmcWorld gameWorld,
        @Nullable MessagesFactory messagesFactory,
        @Nullable IScoreBoard globalScoreBoard) {
}
