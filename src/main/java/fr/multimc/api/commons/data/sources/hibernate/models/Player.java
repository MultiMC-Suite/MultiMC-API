package fr.multimc.api.commons.data.sources.hibernate.models;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @Column(unique = true, nullable = false)
    private final String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_code", referencedColumnName = "code")
    private final Team team;

    public Player(){
        this("");
    }

    public Player(@NotNull final String username){
        this(username, null);
    }

    public Player(@NotNull final String username, @Nullable final Team team){
        this.username = username;
        this.team = team;
    }

    public String getUsername() {
        return username;
    }

    public Team getTeam() {
        return team;
    }
}
