package fr.multimc.api.commons.data.sources.database.interfaces;

public interface IConstraint {

    /**
     * Get a SQL constraint from an object that implement IConstraint
     * @return A String that contains the SQL for constraint
     */
    String getConstraint();
}
