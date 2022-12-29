package fr.multimc.api.spigot.managers;

import fr.multimc.api.commons.data.handlers.RestHandler;
import fr.multimc.api.commons.data.sources.database.Database;
import fr.multimc.api.commons.data.sources.rest.RestAPI;
import fr.multimc.api.spigot.teams.MmcTeam;
import fr.multimc.api.commons.data.handlers.DatabaseHandler;
import fr.multimc.api.spigot.entities.player.MmcPlayer;
import fr.multimc.api.commons.data.handlers.interfaces.ITeamHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class TeamManager {

    private final ITeamHandler teamHandler;
    private final List<MmcTeam> mmcTeams = new ArrayList<>();

    public TeamManager(@NotNull Database database) {
        this.teamHandler = new DatabaseHandler(database);
    }

    public TeamManager(@NotNull RestAPI api) {
        this.teamHandler = new RestHandler(api);
    }

    public void addTeam(@NotNull String teamCode, @NotNull String name, @NotNull String... players){
        if(!(this.teamHandler instanceof DatabaseHandler)) throw new UnsupportedOperationException("This method is only available with a database handler");
        ((DatabaseHandler) this.teamHandler).addTeam(teamCode, name, players);
    }

    public List<MmcTeam> loadTeams(){
        // Create teams
        this.mmcTeams.clear();
        Map<String, List<String>> playersByTeam = this.teamHandler.getPlayersByTeam();
        Map<String, String> teamNames = this.teamHandler.getTeamNamesByTeam();
        // Iterate by team code
        for(String teamCode: playersByTeam.keySet()){
            List<String> playersName = playersByTeam.get(teamCode);
            List<MmcPlayer> players = new ArrayList<>();
            // Add all players to team
            for(String playerName: playersName){
                MmcPlayer player = new MmcPlayer(playerName);
                players.add(player);
            }
            // Add team object to list
            MmcTeam mmcTeam = new MmcTeam(teamNames.get(teamCode), teamCode, players.toArray(new MmcPlayer[0]));
            this.mmcTeams.add(mmcTeam);
        }
        return this.mmcTeams;
    }

    public List<MmcTeam> getTeams() {
        return this.mmcTeams;
    }

    public MmcTeam getTeamFromCode(@NotNull String teamCode){
        for(MmcTeam mmcTeam : this.mmcTeams){
            if(mmcTeam.getTeamCode().equals(teamCode)){
                return mmcTeam;
            }
        }
        return null;
    }

    public MmcTeam getTeamFromPlayer(@NotNull MmcPlayer player){
        for(MmcTeam mmcTeam : this.mmcTeams){
            if(mmcTeam.isPlayerInTeam(player)){
                return mmcTeam;
            }
        }
        return null;
    }

    public void pushScores(@NotNull Map<String, Integer> localScores){
        Map<String, Integer> currentScores = this.teamHandler.getScores();
        Map<String, Integer> newScores = new HashMap<>();
        localScores.forEach((teamCode, score) -> newScores.put(teamCode, currentScores.getOrDefault(teamCode, 0) + score));
        this.teamHandler.setScores(newScores);
    }

    public void pushTeamScores(@NotNull Map<MmcTeam, Integer> localScores){
        Map<String, Integer> currentScores = new HashMap<>();
        localScores.forEach((team, score) -> currentScores.put(team.getTeamCode(), score));
        this.pushScores(currentScores);
    }
}
