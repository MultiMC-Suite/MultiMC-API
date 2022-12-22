package fr.multimc.api.spigot.scoreboards;

import fr.multimc.api.spigot.entities.player.MmcPlayer;
import fr.multimc.api.spigot.scoreboards.enums.ScoreBoardType;
import fr.multimc.api.spigot.scoreboards.interfaces.IScoreBoard;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.entity.Player;

import java.util.Objects;

@SuppressWarnings("unused")
public class MmcSidebar implements IScoreBoard {

    private final ScoreBoardType type = ScoreBoardType.SIDEBAR;
    private final Sidebar sidebar;

    public MmcSidebar(ScoreboardLibrary scoreboardLibrary, int lineCount) {
        this.sidebar = scoreboardLibrary.createSidebar(lineCount);
    }

    @Override
    public void addPlayer(MmcPlayer mmcPlayer) {
        System.out.println("Adding player " + mmcPlayer.getName() + " to the sidebar");
        Player player = mmcPlayer.getPlayer();
        if(Objects.nonNull(player)) {
            this.sidebar.addPlayer(player);
        }
    }
    @Override
    public void removePlayer(MmcPlayer mmcPlayer) {
        System.out.println("Remove player " + mmcPlayer.getName() + " to the sidebar");
        Player player = mmcPlayer.getPlayer();
        if(Objects.nonNull(player)) {
            this.sidebar.removePlayer(player);
        }
    }

    @Override
    public ScoreBoardType getType() {
        return type;
    }
    public Sidebar getSidebar() {
        return sidebar;
    }
}
