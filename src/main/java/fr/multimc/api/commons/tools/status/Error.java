package fr.multimc.api.commons.tools.status;

import fr.multimc.api.commons.tools.enums.Status;

import javax.annotation.Nonnull;

public class Error extends Status {
    /**
     *
     */
    public Error() {
        super("Error: An error occured!");
    }

    /**
     *
     * @param clause
     */
    public Error(@Nonnull String clause) {
        super(String.format("Error: %s", clause));
    }
}
