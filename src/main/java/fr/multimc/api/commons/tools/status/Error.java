package fr.multimc.api.commons.tools.status;

import fr.multimc.api.commons.tools.enums.Status;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class Error extends Status {
    public Error() {
        super("Error: An error occured!");
    }
    public Error(@Nonnull String clause) {
        super(String.format("Error: %s", clause));
    }

    public Error(@Nonnull String clause, @Nonnull Object... objects) {
        super(String.format("Error: %s", clause), objects);
    }
}
