package fr.multimc.api.commons.old_database;

import fr.multimc.api.commons.old_database.enums.DatabaseStatus;
import fr.multimc.api.commons.old_database.query.QueryBuilder;
import fr.multimc.api.commons.old_database.query.QueryResult;
import fr.multimc.api.commons.old_database.query.QueryType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Table {

    private final Database database;
    private final String name;
    private boolean initialized;


    /**
     * Table constructor without any initialization fields
     * @param database Database
     * @param name Table name
     */
    public Table(@NotNull Database database, String name){
        this.database = database;
        this.name = name;
        this.initialized = this.database.isTableExist(this.name) == DatabaseStatus.TABLE_EXIST;
    }

    /**
     * Table constructor for distincts fields
     * @param database Database
     * @param name Table name
     * @param fields Table fields (array of String)
     */
    public Table(@NotNull Database database, String name, String... fields){
        this.database = database;
        this.name = name;
        StringBuilder query = new StringBuilder();
        for(int i = 0; i < fields.length; i++){
            query.append(fields[i]);
            if(i != fields.length - 1){
                query.append(", ");
            }
        }
        this.initializeTable(query.toString());
    }

    /**
     * Table constructor for a unique string with all fields into
     * @param database Database
     * @param name Table name
     * @param fields Table fields formatted into a String
     */
    public Table(@NotNull Database database, String name, String fields){
        this.database = database;
        this.name = name;
        this.initializeTable(fields);
    }

    /**
     * Initialize table in the database
     * @param fields Table fields formatted into a String
     */
    private void initializeTable(String fields){
        QueryResult queryResult = this.database.executeQuery(
                new QueryBuilder(QueryType.UPDATE,
                        String.format("CREATE TABLE IF NOT EXISTS %s (%s);", name, fields))
                        .getQuery());
        this.initialized = queryResult.queryStatus() == DatabaseStatus.SUCCESS;
    }

    /**
     * Get table name
     * @return String that is the table name
     */
    public String getTableName(){
        return this.name;
    }

    /**
     * Get the database object
     * @return Database
     */
    public Database getDatabase(){
        return this.database;
    }

    /**
     * Check if the table is initialized in the database
     * @return True if the table is initialized
     */
    public boolean isInitialized(){
        return this.initialized;
    }

}
