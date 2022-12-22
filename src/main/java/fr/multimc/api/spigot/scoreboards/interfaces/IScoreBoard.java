package fr.multimc.api.spigot.scoreboards.interfaces;

import fr.multimc.api.spigot.entities.player.MmcPlayer;
import fr.multimc.api.spigot.scoreboards.enums.ScoreBoardType;

public interface IScoreBoard {
    ScoreBoardType getType();
    void addPlayer(MmcPlayer player);
    void removePlayer(MmcPlayer player);
}
