package fr.multimc.api.commons.database.query;

import fr.multimc.api.commons.database.enums.DatabaseStatus;

import java.sql.ResultSet;

public record QueryResult(QueryType queryType, DatabaseStatus queryStatus, ResultSet resultSet) {

}
