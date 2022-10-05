package fr.multimc.api.commons.database.query;

public class Query {

    private QueryType queryType;
    private String query;

    public Query(QueryType queryType, String query){
        this.queryType = queryType;
        this.query = query;
    }

    public QueryType getQueryType() {
        return queryType;
    }
    public String getQuery() {
        return query;
    }
}
