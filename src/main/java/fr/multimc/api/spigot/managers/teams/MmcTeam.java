package fr.multimc.api.spigot.managers.teams;

import fr.multimc.api.spigot.tools.entities.player.MmcPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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


    public MmcTeam(@NotNull String name, @NotNull String teamCode, @NotNull MmcPlayer... localPlayers){
        this.name = name;
        this.teamCode = teamCode;
        this.players = new ArrayList<>();
        this.players.addAll(Arrays.asList(localPlayers));
    }

    public boolean isPlayerInTeam(@NotNull UUID uuid){
        for(MmcPlayer player: this.players){
            if(player.getUUID() == uuid){
                return true;
            }
        }
        return false;
    }

    public boolean isPlayerInTeam(@NotNull MmcPlayer player){
        for(MmcPlayer _player: this.players){
            if(_player.equals(player)){
                return true;
            }
        }
        return false;
    }

    public void sendMessage(@Nonnull Component message){
        for (MmcPlayer player: this.players) player.sendMessage(message);
    }

    public void sendTitle(@Nullable Component title, @Nullable Component subtitle){
        for (MmcPlayer player: this.players) player.sendTitle(title, subtitle, Duration.ofMillis(250), Duration.ofMillis(500), Duration.ofMillis(150));
    }

    public void sendActionBar(@Nonnull Component bar){
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
