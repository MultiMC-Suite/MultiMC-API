package fr.multimc.api.commons.database.query;

import fr.multimc.api.commons.database.enums.DatabaseStatus;

import java.sql.ResultSet;

/**
 * The QueryResult is a custom SQL object that allow you to use the result of an executed query
 * @author Tom CZEKAJ
 * @version 1.0
 * @since 07/10/2022
 *
 * @param queryType Custom QueryType
 * @param queryStatus Custom QueryStatus
 * @param resultSet SQL ResultSet, null if the QueryType is not UPDATE
 */
public record QueryResult(QueryType queryType, DatabaseStatus queryStatus, ResultSet resultSet) {

}
