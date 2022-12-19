package fr.multimc.api.spigot.managers.teams.handlers;

import java.util.List;
import java.util.Map;

public interface TeamHandler {
    void addTeam(String teamCode, String teamName, String... playersName);
    Map<String, List<String>> getPlayersByTeam();
    Map<String, String> getTeamNamesByTeam();
    Map<String, Integer> getScores();
    void setScores(Map<String, Integer> scores);
}
