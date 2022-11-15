package fr.multimc.api.commons.old_database.query;

/**
 * The Query is a custom SQL object that allow you to put a string with a QueryType
 * @author Tom CZEKAJ
 * @version 1.0
 * @since 07/10/2022
 *
 * @param queryType Custom QueryType
 * @param query SQL query String
 */
public record Query(QueryType queryType, String query) {

}
