package fr.multimc.api.commons.database;

import fr.multimc.api.commons.database.enums.DatabaseStatus;
import fr.multimc.api.commons.database.query.QueryBuilder;
import fr.multimc.api.commons.database.query.QueryResult;
import fr.multimc.api.commons.database.query.QueryType;
import org.jetbrains.annotations.NotNull;

public class Table {

    private Database database;
    private String name;
    private boolean initialized;


    public Table(@NotNull Database database, String name){
        this.database = database;
        this.name = name;
        this.initialized = this.database.isTableExist(this.name) == DatabaseStatus.TABLE_EXIST;
    }

    public Table(@NotNull Database database, String name, String... fields){
        StringBuilder query = new StringBuilder();
        for(int i = 0; i < fields.length; i++){
            query.append(fields[i]);
            if(i != fields.length - 1){
                query.append(", ");
            }
        }
        QueryResult queryResult = this.database.executeQuery(
                new QueryBuilder(QueryType.UPDATE,
                        String.format("CREATE TABLE IF NOT EXISTS %s (%s)", name, query.toString()))
                        .getQuery());
        this.initialized = queryResult.queryStatus() == DatabaseStatus.SUCCESS;
    }

    public Table(@NotNull Database database, String name, String fields){
        this.database = database;
        this.name = name;
        QueryResult queryResult = this.database.executeQuery(
                new QueryBuilder(QueryType.UPDATE,
                        String.format("CREATE TABLE IF NOT EXISTS %s (%s)", name, fields))
                        .getQuery());
        this.initialized = queryResult.queryStatus() == DatabaseStatus.SUCCESS;
    }

    public String getTableName(){
        return this.name;
    }
    public Database getDatabase(){
        return this.database;
    }
    public boolean isInitialized(){
        return this.initialized;
    }

}
