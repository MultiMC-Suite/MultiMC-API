package fr.multimc.api.commons.tools.status;

import fr.multimc.api.commons.tools.enums.Status;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class Success extends Status {
    public Success() {
        super("Success: Operation successfully performed.");
    }
    public Success(@Nonnull String clause) {
        super(String.format("Success: %s", clause));
    }
    public Success(@Nonnull String clause, @Nonnull Object... objects) {
        super(String.format("Success: %s", clause), objects);
    }
}
