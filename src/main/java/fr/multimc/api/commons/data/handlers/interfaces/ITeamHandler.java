package fr.multimc.api.commons.data.handlers.interfaces;

import java.util.List;
import java.util.Map;

/**
 * @author Xen0Xys
 * @version 1.0
 * @see fr.multimc.api.commons.data.handlers.DatabaseHandler
 * @see fr.multimc.api.commons.data.handlers.RestHandler
 */
public interface ITeamHandler {
    /**
     * Get all the players sorted by team code
     * @return {@link Map}<{@link String}, {@link List}<{@link String}>> with team code as key and player usernames as value
     */
    Map<String, List<String>> getPlayersByTeam();

    /**
     * Get all the team names sorted by team code
     * @return {@link Map}<{@link String}, {@link String}> with team code as key and team name as value
     */
    Map<String, String> getTeamNamesByTeam();

    /**
     * Get all the team scores sorted by team code
     * @return {@link Map}<{@link String}, {@link Integer}> with team code as key and team score as value
     */
    Map<String, Integer> getScores();

    /**
     * Set all the team scores
     * @param scores {@link Map}<{@link String}, {@link Integer}> with team code as key and team score as value
     */
    void setScores(Map<String, Integer> scores);
}
