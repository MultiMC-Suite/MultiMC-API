package fr.multimc.api.spigot.games.score;

import fr.multimc.api.spigot.games.teams.MmcTeam;
import fr.multimc.api.spigot.games.teams.TeamManager;

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
     * @param newScore The new score to set
     */
    public void updateScore(MmcTeam team, int newScore) {
        scores.put(team, newScore);
    }

    /**
     * Add a score to a team
     * @param team MmcTeam object
     * @param scoreAddition The score to add
     */
    public void addScore(MmcTeam team, int scoreAddition) {
        scores.put(team, scores.get(team) + scoreAddition);
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
