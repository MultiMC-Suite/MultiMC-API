package fr.multimc.api.spigot.scoreboards;

import fr.multimc.api.spigot.entities.player.MmcPlayer;
import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public abstract class MmcSidebar implements Listener {

    private final Sidebar sidebar;
    private final List<MmcPlayer> players = new ArrayList<>();

    public MmcSidebar(Plugin plugin, ScoreboardManager scoreboardManager, int lineCount) {
        this.sidebar = scoreboardManager.sidebar(lineCount);
        this.init();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    public void addPlayer(MmcPlayer mmcPlayer) {
        this.players.add(mmcPlayer);
        Player player = mmcPlayer.getPlayer();
        if(Objects.nonNull(player)) {
            this.sidebar.addPlayer(player);
        }
    }
    public void removePlayer(MmcPlayer mmcPlayer) {
        this.players.remove(mmcPlayer);
        Player player = mmcPlayer.getPlayer();
        if(Objects.nonNull(player)) {
            this.sidebar.removePlayer(player);
        }
    }
    public void setVisibility(boolean visible) {
        this.sidebar.visible(visible);
    }

    public Sidebar getSidebar() {
        return sidebar;
    }

    public abstract void init();
    public abstract void update();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        MmcPlayer player = new MmcPlayer(event.getPlayer());
        for(MmcPlayer mmcPlayer : this.players){
            if(mmcPlayer.equals(player)){
                this.sidebar.addPlayer(event.getPlayer());
            }
        }
    }
}
