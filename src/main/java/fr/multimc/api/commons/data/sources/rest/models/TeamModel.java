package fr.multimc.api.commons.data.sources.rest.models;

import org.jetbrains.annotations.NotNull;

public record TeamModel(@NotNull String code, @NotNull String name, int score, int ownerId, @NotNull UserModel[] members) {
}
