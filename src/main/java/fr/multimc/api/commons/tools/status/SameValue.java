package fr.multimc.api.commons.tools.status;

import fr.multimc.api.commons.tools.enums.Status;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class SameValue extends Status {
    public SameValue() {
        super("Error: Same Value.");
    }

    public SameValue(@Nonnull String clause) {
        super(String.format("Error: %s", clause));
    }

    public SameValue(@Nonnull String clause, @Nonnull Object... objects) {
        super(String.format("Error: %s", clause), objects);
    }
}
