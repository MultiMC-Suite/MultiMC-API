package fr.multimc.api.commons.data.handlers;

import fr.multimc.api.commons.data.handlers.interfaces.ITeamHandler;
import fr.multimc.api.commons.data.sources.hibernate.Hibernate;
import fr.multimc.api.commons.data.sources.hibernate.models.Player;
import fr.multimc.api.commons.data.sources.hibernate.models.Team;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HibernateHandler implements ITeamHandler {

    private final Hibernate hibernate;

    public HibernateHandler(Hibernate hibernate) {
        this.hibernate = hibernate;
    }

    @Override
    public Map<String, List<String>> getPlayersByTeam() {
        Map<String, List<String>> playersByTeam = new HashMap<>();
        try(Session session = this.hibernate.getSessionFactory().openSession()) {
            List<Team> teams = session.createQuery("FROM Team", Team.class).list();
            for(Team team: teams)
                playersByTeam.put(team.getCode(), team.getPlayers().stream().map(Player::getUsername).toList());
        }
        return playersByTeam;
    }

    @Override
    public Map<String, String> getTeamNamesByTeam() {
        Map<String, String> teamNamesByTeam = new HashMap<>();
        try(Session session = this.hibernate.getSessionFactory().openSession()) {
            List<Team> teams = session.createQuery("FROM Team", Team.class).list();
            for(Team team: teams)
                teamNamesByTeam.put(team.getCode(), team.getName());
        }
        return teamNamesByTeam;
    }

    @Override
    public Map<String, Integer> getScores() {
        Map<String, Integer> scores = new HashMap<>();
        try(Session session = this.hibernate.getSessionFactory().openSession()) {
            List<Team> teams = session.createQuery("FROM Team", Team.class).list();
            for(Team team: teams)
                scores.put(team.getCode(), team.getScore());
        }
        return scores;
    }

    @Override
    public void setScores(Map<String, Integer> scores) {
        try(Session session = this.hibernate.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            for(Map.Entry<String, Integer> entry: scores.entrySet()){
                Team team = session.get(Team.class, entry.getKey());
                team.setScore(entry.getValue());
                session.merge(team);
            }
            transaction.commit();
        }
    }

    public void addTeam(String teamCode, String name, String[] players) {
        try(Session session = this.hibernate.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Team team = new Team(teamCode, name);
            Team unknown = new Team("IDK", name);
            session.persist(team);
            for(String playerName: players){
                Player player = new Player(playerName, unknown);
                session.persist(player);
            }
            transaction.commit();
        }
    }
}
