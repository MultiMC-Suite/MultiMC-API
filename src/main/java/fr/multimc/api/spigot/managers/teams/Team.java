package fr.multimc.api.spigot.managers.teams;

import java.util.*;

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
