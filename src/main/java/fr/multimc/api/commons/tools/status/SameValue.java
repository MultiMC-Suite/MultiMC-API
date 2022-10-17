package fr.multimc.api.commons.tools.status;

import fr.multimc.api.commons.tools.enums.Status;

import javax.annotation.Nonnull;

public class SameValue extends Status {
    /**
     *
     */
    public SameValue() {
        super("Error: Same Value.");
    }

    /**
     *
     * @param clause
     */
    public SameValue(@Nonnull String clause) {
        super(String.format("Error: %s", clause));
    }
}
