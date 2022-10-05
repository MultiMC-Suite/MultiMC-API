package fr.multimc.api.commons.database;

import fr.multimc.api.commons.database.enums.DatabaseStatus;
import fr.multimc.api.commons.database.enums.DatabaseType;
import fr.multimc.api.commons.database.query.Query;
import fr.multimc.api.commons.database.query.QueryBuilder;
import fr.multimc.api.commons.database.query.QueryResult;
import fr.multimc.api.commons.database.query.QueryType;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class Database {

    private String ip;
    private int port;
    private String userName;
    private String password;
    private String databaseName;

    private File databaseFile;
    private Logger logger;
    private DatabaseType databaseType;
    private HashMap<String, Table> tables;

    private Connection connection;

    public Database(String ip, int port, String userName, String password, String databaseName, Logger logger){
        this.ip = ip;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.databaseName = databaseName;
        this.logger = logger;
        this.databaseType = DatabaseType.MYSQL;
        this.tables = new HashMap<>();
    }

    public Database(File databaseFile, Logger logger){
        this.databaseFile = databaseFile;
        this.logger = logger;
        this.databaseType = DatabaseType.SQLITE;
        this.tables = new HashMap<>();
    }

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

    public DatabaseStatus reconnect(){
        if(this.connection != null){
            DatabaseStatus status = this.disconnect();
            return status == DatabaseStatus.SUCCESS ? this.connect() : status;
        }
        return this.connect();
    }

    public DatabaseStatus disconnect(){
        try {
            this.connection.close();
            return DatabaseStatus.SUCCESS;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return DatabaseStatus.SQLERROR;
    }

    private Statement getStatement(){
        try {
            return this.connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            this.reconnect();
        }
        return null;
    }

    public QueryResult executeQuery(String query, boolean update){
        return this.executeQuery(new QueryBuilder(update ? QueryType.UPDATE : QueryType.SELECT, query).getQuery());
    }

    public QueryResult executeQuery(Query query){
        String preparedQuery = this.changeQuerySyntax(query.getQuery());

        DatabaseStatus queryStatus = DatabaseStatus.SQLERROR;
        ResultSet resultSet = null;
        try {
            Statement statement = this.getStatement();
            if(statement != null) {
                if(query.getQueryType() == QueryType.SELECT) {
                    resultSet = statement.executeQuery(preparedQuery);
                    queryStatus = DatabaseStatus.SUCCESS;
                }else if(query.getQueryType() == QueryType.UPDATE) {
                    statement.executeUpdate(preparedQuery);
                    queryStatus = DatabaseStatus.SUCCESS;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new QueryResult(query.getQueryType(), queryStatus, resultSet);
    }

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
            ResultSet rs = this.executeQuery(query).resultSet();
            try {
                if(rs.next()){
                    return DatabaseStatus.TABLE_EXIST;
                }else{
                    return DatabaseStatus.TABLE_NOT_EXIST;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return DatabaseStatus.SQLERROR;
    }

    private String changeQuerySyntax(String inputString){
        if(this.databaseType == DatabaseType.SQLITE){
            List<String> fieldNames = new ArrayList<>();
            StringBuilder outputString = new StringBuilder(inputString);
            outputString = new StringBuilder(outputString.toString().replace(" INT ", " INTEGER "));
            outputString = new StringBuilder(outputString.toString().replace(" INT,", " INTEGER,"));
            outputString = new StringBuilder(outputString.toString().replace("UNSIGNED", ""));
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

}
