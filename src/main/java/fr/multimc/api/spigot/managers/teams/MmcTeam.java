package fr.multimc.api.spigot.managers.teams;

import fr.multimc.api.spigot.tools.entities.player.MmcPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class MmcTeam {

    private final String name;
    private final String teamCode;
    private final List<MmcPlayer> players;

    public MmcTeam(@NotNull String name, @NotNull String teamCode, @NotNull MmcPlayer... mmcPlayers){
        this.name = name;
        this.teamCode = teamCode;
        this.players = new ArrayList<>();
        this.players.addAll(Arrays.asList(mmcPlayers));
    }

    public void addPlayer(MmcPlayer mmcPlayer){
        this.players.add(mmcPlayer);
    }

    public void removePlayer(MmcPlayer mmcPlayer){
        this.players.remove(mmcPlayer);
    }

    public boolean isPlayerInTeam(@NotNull UUID uuid){
        for(MmcPlayer player: this.players){
            if(player.getUUID() == uuid){
                return true;
            }
        }
        return false;
    }

    public boolean isPlayerInTeam(@NotNull MmcPlayer mmcPlayer){
        for(MmcPlayer player: this.players){
            if(player.equals(mmcPlayer)){
                return true;
            }
        }
        return false;
    }

    public void sendMessage(@NotNull Component message){
        for (MmcPlayer player: this.players) player.sendMessage(message);
    }

    public void sendTitle(@Nullable Component title, @Nullable Component subtitle){
        for (MmcPlayer player: this.players) player.sendTitle(title, subtitle, Duration.ofMillis(250), Duration.ofMillis(500), Duration.ofMillis(150));
    }

    public void sendActionBar(@NotNull Component bar){
        for (MmcPlayer player: this.players) player.sendActionBar(bar);
    }

    public void playSound(@NotNull Sound sound){
        for(MmcPlayer mmcPlayer : this.players){
            Player player = mmcPlayer.getPlayer();
            if(player != null){
                player.playSound(player.getLocation(), sound, 1, 1);
            }
        }
    }

    public String getName() {
        return name;
    }
    public String getTeamCode() {
        return teamCode;
    }
    public int getTeamSize() {
        return players.size();
    }
    public List<MmcPlayer> getPlayers() {
        return players;
    }
}
