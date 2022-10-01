package fr.multimc.api.commons.tools.status;

import fr.multimc.api.commons.tools.enums.Status;

import javax.annotation.Nonnull;

public class Success extends Status {
    /**
     *
     */
    public Success() {
        super("Success: Operation successfully performed.");
    }

    /**
     *
     * @param clause
     */
    public Success(@Nonnull String clause) {
        super(String.format("Success: %s", clause));
    }
}
