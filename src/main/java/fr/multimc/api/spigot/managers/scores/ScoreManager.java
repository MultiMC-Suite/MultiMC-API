package fr.multimc.api.spigot.managers.scores;

import fr.multimc.api.spigot.managers.teams.MmcTeam;
import fr.multimc.api.spigot.managers.teams.TeamManager;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ScoreManager {

    private final Map<MmcTeam, Integer> scores = new HashMap<>();
    private final TeamManager teamManager;

    /**
     * Constructor of ScoreManager object
     * @param teamManager TeamManager object
     */
    public ScoreManager(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    /**
     * Set a new score to a team
     * @param team MmcTeam object
     * @param scoreChange The score to add
     */
    public void updateScore(MmcTeam team, int scoreChange) {
        scores.put(team, scores.get(team) + scoreChange);
    }

    /**
     * Add a new score to a team
     * @param team MmcTeam object
     * @param score The score to set
     */
    public void setScore(MmcTeam team, int score) {
        scores.put(team, score);
    }

    /**
     * Remove a score to a team
     * @param team MmcTeam object
     */
    public void removeScore(MmcTeam team) {
        scores.remove(team);
    }

    /**
     * Push the scores to the database
     */
    public void pushScore() {
        teamManager.pushTeamScores(this.scores);
    }

    /**
     * Get the score of a team
     * @param team MmcTeam object
     * @return The score of the team
     */
    public int getScores(MmcTeam team) {
        return scores.get(team);
    }
}
