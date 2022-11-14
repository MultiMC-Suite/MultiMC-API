package fr.multimc.api.commons.tools.enums;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class Status {
    private final String message;

    public Status(@Nonnull String message) {
        this.message = message;
    }

    public Status(@Nonnull String message, @Nonnull Object... replacements) {
        this.message = String.format(message, replacements);
    }

    public String getMessage() {
        return this.message;
    }
}
