package fr.multimc.api.commons.tools.enums;

import javax.annotation.Nonnull;

public class Status {
    private final String message;

    public Status(@Nonnull String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
