package fr.multimc.api.commons.data.sources.hibernate.models;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @Column(unique = true, nullable = false)
    private final String code;

    @Column(unique = true, nullable = false)
    private final String name;

    @Column
    private int score;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team_code")
    private List<Player> players;

    public Team(){
        this("", "");
    }

    public Team(@NotNull final String code, @NotNull final String name){
        this(code, name, 0);
    }

    public Team(@NotNull final String code, @NotNull final String name, final int score){
        this.code = code;
        this.name = name;
        this.score = score;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setScore(final int newScore) {
        this.score = newScore;
    }
}
