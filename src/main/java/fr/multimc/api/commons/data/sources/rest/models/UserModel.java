package fr.multimc.api.commons.data.sources.rest.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public record UserModel(int id, @NotNull String username, int groupId, @Nullable String teamCode) {

    public UserModel(int id, @NotNull String username, @Nullable String teamCode){
        this(id, username, 0, teamCode);
    }

    public UserModel(int id, @NotNull String username){
        this(id, username, 0, null);
    }

}
