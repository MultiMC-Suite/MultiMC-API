package fr.multimc.api.commons.managers.teammanager;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class Team {

    private final String name;
    private final int id;
    private final int teamSize;
    private final List<Player> players;

    public Team(String name, int id, Player... localPlayers){
        this.name = name;
        this.id = id;
        this.players = new ArrayList<>();
        this.players.addAll(Arrays.asList(localPlayers));
        this.teamSize = localPlayers.length;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getTeamSize() {
        return teamSize;
    }
}
