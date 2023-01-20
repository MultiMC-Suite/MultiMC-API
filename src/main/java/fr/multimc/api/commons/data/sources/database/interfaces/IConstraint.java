package fr.multimc.api.commons.data.sources.database.interfaces;

/**
 * Represents a constraint in a database.
 * @author Xen0Xys
 * @version 1.0
 * @see fr.multimc.api.commons.data.sources.database.models.constraints.ForeignKeyConstraint
 * @see fr.multimc.api.commons.data.sources.database.models.constraints.PrimaryKeyConstraint
 */
public interface IConstraint {

    /**
     * Get a SQL constraint from an {@link IConstraint} object.
     * @return {@link String} that contains the SQL constraint
     */
    String getConstraint();
}
