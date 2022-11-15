package fr.multimc.api.commons.old_database;

import fr.multimc.api.commons.database.enums.DatabaseType;
import fr.multimc.api.commons.old_database.enums.DatabaseStatus;
import fr.multimc.api.commons.old_database.query.Query;
import fr.multimc.api.commons.old_database.query.QueryBuilder;
import fr.multimc.api.commons.old_database.query.QueryResult;
import fr.multimc.api.commons.old_database.query.QueryType;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings({"unused"})
public class Database {

    private String ip;
    private int port;
    private String userName;
    private String password;
    private String databaseName;

    private File databaseFile;
    private final Logger logger;
    private final DatabaseType databaseType;
    private final HashMap<String, Table> tables;

    private Connection connection;

    /**
     * Database constructor for MYSQL
     * @param ip Database IP
     * @param port Database port
     * @param userName Database username
     * @param password Database password
     * @param databaseName Database name
     * @param logger Plugin logger
     */
    public Database(String ip, int port, String userName, String password, String databaseName, Logger logger){
        this.ip = ip;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.databaseName = databaseName;
        this.logger = logger;
        this.databaseType = DatabaseType.MYSQL;
        this.tables = new HashMap<>();
        this.connect();
    }

    /**
     * Database constructor for SQLITE
     * @param databaseFile Database file
     * @param logger Plugin logger
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Database(File databaseFile, Logger logger){
        this.databaseFile = databaseFile;
        this.logger = logger;
        this.databaseType = DatabaseType.SQLITE;
        this.tables = new HashMap<>();
        databaseFile.getParentFile().mkdirs();
        this.connect();
    }

    /**
     * Initialize the connection object with a new connection to the database
     * @return Custom DatabaseStatus, can be SUCCESS or SQLERROR
     */
    public DatabaseStatus connect(){
        DriverManager.setLoginTimeout(2);
        try{
            if(this.databaseType == DatabaseType.MYSQL){
                this.connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s?autoReconnect=true",
                        this.ip,
                        this.port,
                        this.databaseName),
                        this.userName,
                        this.password);
            }else{
                this.connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", this.databaseFile.getPath()));
            }
            if(this.connection != null){
                return DatabaseStatus.SUCCESS;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return DatabaseStatus.SQLERROR;
    }

    /**
     * Reset the connection object and reconnect it to the database
     * @return Custom DatabaseStatus, can be SUCCESS or SQLERROR
     */
    @SuppressWarnings("UnusedReturnValue")
    public DatabaseStatus reconnect(){
        if(this.connection != null){
            DatabaseStatus status = this.disconnect();
            return status == DatabaseStatus.SUCCESS ? this.connect() : status;
        }
        return this.connect();
    }

    /**
     * Close the connection with the database
     * @return Custom DatabaseStatus, can be SUCCESS or SQLERROR
     */
    public DatabaseStatus disconnect(){
        try {
            this.connection.close();
            return DatabaseStatus.SUCCESS;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return DatabaseStatus.SQLERROR;
    }

    /**
     * Get a statement to execute operations with the database
     * @return SQL Statement
     */
    private Statement getStatement(){
        try {
            return this.connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            this.reconnect();
        }
        return null;
    }

    /**
     * Execute a query from a query string and a boolean to select from execute and execute update
     * @param query SQL query String
     * @param update True if the operation need updating with the database
     * @return Custom QueryResult
     */
    public QueryResult executeQuery(String query, boolean update){
        return this.executeQuery(new QueryBuilder(update ? QueryType.UPDATE : QueryType.SELECT, query).getQuery());
    }

    /**
     * Execute a query from a Query object
     * @param query Custom Query
     * @return Custom QueryResult
     */
    public QueryResult executeQuery(Query query){
        String preparedQuery = this.changeQuerySyntax(query.query());

        DatabaseStatus queryStatus = DatabaseStatus.SQLERROR;
        ResultSet resultSet = null;
        try {
            Statement statement = this.getStatement();
            if(statement != null) {
                if(query.queryType() == QueryType.SELECT) {
                    resultSet = statement.executeQuery(preparedQuery);
                    queryStatus = DatabaseStatus.SUCCESS;
                }else if(query.queryType() == QueryType.UPDATE) {
                    statement.executeUpdate(preparedQuery);
                    queryStatus = DatabaseStatus.SUCCESS;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new QueryResult(query.queryType(), queryStatus, resultSet);
    }

    public QueryResult executeQuery(String query, QueryType queryType){
        DatabaseStatus queryStatus = DatabaseStatus.SQLERROR;
        ResultSet resultSet = null;
        try {
            Statement statement = this.getStatement();
            if(statement != null) {
                if(queryType == QueryType.SELECT) {
                    resultSet = statement.executeQuery(query);
                    queryStatus = DatabaseStatus.SUCCESS;
                }else if(queryType == QueryType.UPDATE) {
                    statement.executeUpdate(query);
                    queryStatus = DatabaseStatus.SUCCESS;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new QueryResult(queryType, queryStatus, resultSet);
    }

    /**
     * Check if a given table exists in the database
     * @param tableName Target table name
     * @return Custom DatabaseStatus, can be TABLE_EXIST, TABLE_NOT_EXIST or SQLERROR
     */
    public DatabaseStatus isTableExist(String tableName){
        Query query;
        if(this.databaseType == DatabaseType.MYSQL){
            query = new QueryBuilder(QueryType.SELECT,
                    String.format("SELECT * FROM information_schema.TABLES WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s'", this.databaseName, tableName))
                        .getQuery();
        }else{
            query = new QueryBuilder(QueryType.SELECT,
                    String.format("SELECT name FROM sqlite_master WHERE type='table' AND name='%s';", tableName))
                        .getQuery();
        }
        QueryResult queryResult = this.executeQuery(query);
        if(queryResult.queryStatus() == DatabaseStatus.SUCCESS){
            try (ResultSet rs = this.executeQuery(query).resultSet()) {
                if (rs.next()) {
                    return DatabaseStatus.TABLE_EXIST;
                } else {
                    return DatabaseStatus.TABLE_NOT_EXIST;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return DatabaseStatus.SQLERROR;
    }

    /**
     * Convert a given SQL query to be used for SQLITE
     * @param inputString SQL query String
     * @return Converted query String
     */
    private String changeQuerySyntax(String inputString){
        if(this.databaseType == DatabaseType.SQLITE){
            List<String> fieldNames = new ArrayList<>();
            StringBuilder outputString = new StringBuilder(inputString);
            outputString = new StringBuilder(outputString.toString().replace(" INT ", " INTEGER "));
            outputString = new StringBuilder(outputString.toString().replace(" INT,", " INTEGER,"));
            outputString = new StringBuilder(outputString.toString().replace("UNSIGNED", ""));
            outputString = new StringBuilder(outputString.toString().replace("AUTO_INCREMENT", "AUTOINCREMENT"));
            outputString = new StringBuilder(outputString.toString().replace("  ", " "));
            for(String temp: inputString.split("\\(")[0].split(",")){
                if(temp.contains("AUTO_INCREMENT")){
                    String fieldName = temp.split(" ")[0];
                    fieldNames.add(fieldName);
                }
            }
            if(fieldNames.size() != 0){
                outputString = new StringBuilder(outputString.toString().replace("PRIMARY KEY", ""));
                outputString = new StringBuilder(outputString.toString().replace("AUTO_INCREMENT", ""));
                outputString = new StringBuilder(outputString.toString().replace("  ", " "));
                for(String fieldName: fieldNames){
                    outputString.append(String.format(",PRIMARY KEY(\"%s\" AUTOINCREMENT)", fieldName));
                }
            }
            return outputString.toString();
        }
        return inputString;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }
}
