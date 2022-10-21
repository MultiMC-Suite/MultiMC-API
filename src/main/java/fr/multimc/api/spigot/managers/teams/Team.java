package fr.multimc.api.spigot.managers.teams;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class Team {

    private final String name;
    private final String teamCode;
    private final List<Player> players;

    public Team(String name, String teamCode, Player... localPlayers){
        this.name = name;
        this.teamCode = teamCode;
        this.players = new ArrayList<>();
        this.players.addAll(Arrays.asList(localPlayers));
    }

    public List<Player> getPlayers() {
        return players;
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

    public boolean isPlayerInTeam(Player player) {
        for(Player _player: this.players){
            if(_player.getName().equals(player.getName())){
                return true;
            }
        }
        return false;
    }
}
