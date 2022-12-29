package fr.multimc.api.commons.data.handlers;

import fr.multimc.api.commons.data.handlers.interfaces.ITeamHandler;
import fr.multimc.api.commons.data.sources.rest.RestAPI;
import fr.multimc.api.commons.data.sources.rest.models.TeamModel;
import fr.multimc.api.commons.data.sources.rest.models.UserModel;
import fr.multimc.api.commons.data.sources.rest.models.requests.SetScoreRequestModel;
import fr.multimc.api.commons.tools.json.JSONUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;

public class RestHandler implements ITeamHandler {

    private final RestAPI api;

    public RestHandler(RestAPI api) {
        this.api = api;
    }

    @Override
    public Map<String, List<String>> getPlayersByTeam() {
        Map<String, List<String>> playersByTeam = new HashMap<>();
        TeamModel[] teams = this.getTeams();
        for(TeamModel team : teams) {
            List<String> players = Arrays.stream(team.members()).map(UserModel::username).toList();
            playersByTeam.put(team.code(), players);
        }
        return playersByTeam;
    }

    @Override
    public Map<String, String> getTeamNamesByTeam() {
        Map<String, String> teamNames = new HashMap<>();
        TeamModel[] teams = this.getTeams();
        for(TeamModel team : teams) {
            teamNames.put(team.code(), team.name());
        }
        return teamNames;
    }

    @Override
    public Map<String, Integer> getScores() {
        Map<String, Integer> scores = new HashMap<>();
        TeamModel[] teams = this.getTeams();
        for(TeamModel team : teams) {
            scores.put(team.code(), team.score());
        }
        return scores;
    }

    public TeamModel[] getTeams() {
        try {
            HttpResponse response = this.api.sendGetRequest("/api/teams?complete=users", true);
            String jsonResponse = EntityUtils.toString(response.getEntity());
            System.out.println(jsonResponse);
            return JSONUtils.fromJson(TeamModel[].class, jsonResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setScores(Map<String, Integer> scores) {
        scores.forEach(this::setScore);
    }

    private void setScore(String team, int score) {
        String jsonContent = JSONUtils.toJson(new SetScoreRequestModel(team, score));
        try {
            HttpResponse response = this.api.sendPostRequest("/api/teams/score", jsonContent, true);
            if(response.getStatusLine().getStatusCode() != 200){
                throw new RuntimeException("Failed to set score for team %s, error %d".formatted(team, response.getStatusLine().getStatusCode()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}