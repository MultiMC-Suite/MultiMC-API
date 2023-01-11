package fr.multimc.api.commons.data.sources.database;

import fr.multimc.api.commons.data.sources.database.enums.DatabaseType;
import fr.multimc.api.commons.data.sources.database.enums.QueryType;
import fr.multimc.api.commons.data.sources.database.enums.SQLState;
import fr.multimc.api.commons.data.sources.database.queries.QueryResult;
import fr.multimc.api.commons.data.sources.DataSourceType;
import fr.multimc.api.commons.data.sources.IDataSource;

import java.io.File;
import java.sql.*;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class Database implements IDataSource {

    private String ip;
    private int port;
    private String userName;
    private String password;
    private String databaseName;

    private File databaseFile;
    private final Logger logger;
    private final DatabaseType databaseType;

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
        databaseFile.getParentFile().mkdirs();
        this.connect();
    }

    /**
     * Initialize the connection object with a new connection to the database
     * @return Custom SQLState, can be SUCCESS or SQLERROR
     */
    public SQLState connect(){
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
                return SQLState.SUCCESS;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return SQLState.SQLERROR;
    }

    /**
     * Reset the connection object and reconnect it to the database
     * @return Custom SQLState, can be SUCCESS or SQLERROR
     */
    @SuppressWarnings("UnusedReturnValue")
    public SQLState reconnect(){
        if(this.connection != null){
            SQLState status = this.disconnect();
            return status == SQLState.SUCCESS ? this.connect() : status;
        }
        return this.connect();
    }

    /**
     * Close the connection with the database
     * @return Custom SQLState, can be SUCCESS or SQLERROR
     */
    public SQLState disconnect(){
        try {
            this.connection.close();
            return SQLState.SUCCESS;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return SQLState.SQLERROR;
    }

    /**
     * Get a statement to execute operations with the database
     * @return SQL Statement object
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
     * Execute a given query
     * @param query SQL Query to execute
     * @param queryType QueryType of the query (SELECT or UPDATE)
     * @return QueryResult object that contain a SQLState and a ResultSet if the query is a SELECT
     */
    public QueryResult executeQuery(String query, QueryType queryType){
        SQLState queryStatus = SQLState.SQLERROR;
        ResultSet resultSet = null;
        try {
            Statement statement = this.getStatement();
            if(statement != null) {
                if(queryType == QueryType.SELECT) {
                    resultSet = statement.executeQuery(query);
                    queryStatus = SQLState.SUCCESS;
                }else if(queryType == QueryType.UPDATE) {
                    statement.executeUpdate(query);
                    queryStatus = SQLState.SUCCESS;
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
     * @return Custom SQLState, can be TABLE_EXIST, TABLE_NOT_EXIST or SQLERROR
     */
    public SQLState isTableExist(String tableName){
        QueryResult queryResult;
        if(this.databaseType == DatabaseType.MYSQL){
            queryResult = this.executeQuery("SELECT * FROM information_schema.TABLES WHERE TABLE_SCHEMA='%s' AND TABLE_NAME='%s';".formatted(this.databaseName, tableName), QueryType.SELECT);
        }else{
            queryResult = this.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='%s';".formatted(tableName), QueryType.SELECT);
        }
        if(queryResult.queryStatus() == SQLState.SUCCESS){
            try (ResultSet rs = queryResult.resultSet()) {
                if (rs.next()) {
                    return SQLState.TABLE_EXIST;
                } else {
                    return SQLState.TABLE_NOT_EXIST;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return SQLState.SQLERROR;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.DATABASE;
    }
}
