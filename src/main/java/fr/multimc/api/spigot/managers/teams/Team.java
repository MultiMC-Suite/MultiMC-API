package fr.multimc.api.spigot.managers.teams;

import net.kyori.adventure.text.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class Team {

    private final String name;
    private final String teamCode;
    private final List<APIPlayer> players;


    public Team(String name, String teamCode, APIPlayer... localPlayers){
        this.name = name;
        this.teamCode = teamCode;
        this.players = new ArrayList<>();
        this.players.addAll(Arrays.asList(localPlayers));
    }

    public boolean isPlayerInTeam(UUID uuid){
        for(APIPlayer player: this.players){
            if(player.getUUID() == uuid){
                return true;
            }
        }
        return false;
    }

    public boolean isPlayerInTeam(APIPlayer player){
        for(APIPlayer _player: this.players){
            if(_player.equals(player)){
                return true;
            }
        }
        return false;
    }

    public void sendMessage(@Nonnull String message){
        for (APIPlayer player: this.players) player.sendMessage(message);
    }

    public void sendMessage(@Nonnull Component message){
        for (APIPlayer player: this.players) player.sendMessage(message);
    }

    public void sendTitle(@Nullable String title, @Nullable String subtitle){
        for (APIPlayer player: this.players) player.sendTitle(title, subtitle, Duration.ofMillis(250), Duration.ofMillis(500), Duration.ofMillis(150));
    }

    public void sendTitle(@Nullable Component title, @Nullable Component subtitle){
        for (APIPlayer player: this.players) player.sendTitle(title, subtitle, Duration.ofMillis(250), Duration.ofMillis(500), Duration.ofMillis(150));
    }

    public void sendActionBar(@Nonnull String bar){
        for (APIPlayer player: this.players) player.sendActionBar(bar);
    }

    public void sendActionBar(@Nonnull Component bar){
        for (APIPlayer player: this.players) player.sendActionBar(bar);
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

    public List<APIPlayer> getPlayers() {
        return players;
    }
}
