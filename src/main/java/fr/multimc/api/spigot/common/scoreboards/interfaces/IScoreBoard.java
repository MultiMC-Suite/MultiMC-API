package fr.multimc.api.spigot.common.scoreboards.interfaces;

import fr.multimc.api.spigot.common.entities.player.MmcPlayer;
import fr.multimc.api.spigot.common.scoreboards.enums.ScoreBoardType;

@SuppressWarnings("unused")
public interface IScoreBoard {
    ScoreBoardType getType();
    void addPlayer(MmcPlayer player);
    void removePlayer(MmcPlayer player);
}
