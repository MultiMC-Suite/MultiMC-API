package fr.multimc.api.commons.old_database.query;

/**
 * The QueryBuilder allow you to create a query from a QueryType and a query String
 * @author Tom CZEKAJ
 * @version 1.0
 * @since 07/10/2022
 */
@SuppressWarnings("unused")
public class QueryBuilder {

    private QueryType queryType;
    private String query;

    /**
     * Constructor of the QueryBuilder
     * @param queryType Custom QueryType
     * @param query SQL query String
     */
    public QueryBuilder(QueryType queryType, String query){
        this.queryType = queryType;
        this.query = query;
    }

    /**
     * Default constructor of the QueryBuilder
     */
    public QueryBuilder(){
        this.queryType = QueryType.SELECT;
        this.query = "";
    }

    /**
     * Get a Query object from the QueryBuilder
     * @return Query object
     */
    public Query getQuery(){
        return new Query(this.queryType, this.query);
    }

    /**
     * Set the QueryType
     * @param queryType Custom QueryType
     */
    public QueryBuilder setQueryType(QueryType queryType) {
        this.queryType = queryType;
        return this;
    }

    /**
     * Set the query String
     * @param query SQL query String
     */
    public QueryBuilder setQuery(String query) {
        this.query = query;
        return this;
    }

}
