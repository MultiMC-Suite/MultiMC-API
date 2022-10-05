package fr.multimc.api.commons.database.query;

public class QueryBuilder {

    private QueryType queryType;
    private String query;

    public QueryBuilder(QueryType queryType, String query){
        this.queryType = queryType;
        this.query = query;
    }

    public QueryBuilder(){
        this.queryType = QueryType.SELECT;
        this.query = "";
    }

    public Query getQuery(){
        return new Query(this.queryType, this.query);
    }


    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public void setQuery(String query) {
        this.query = query;
    }

}
