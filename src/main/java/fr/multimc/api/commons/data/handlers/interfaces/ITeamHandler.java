package fr.multimc.api.commons.data.handlers.interfaces;

import java.util.List;
import java.util.Map;

public interface ITeamHandler {
    Map<String, List<String>> getPlayersByTeam();
    Map<String, String> getTeamNamesByTeam();
    Map<String, Integer> getScores();
    void setScores(Map<String, Integer> scores);
}
