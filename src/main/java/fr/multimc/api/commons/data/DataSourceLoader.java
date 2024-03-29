package fr.multimc.api.commons.data;

import fr.multimc.api.commons.data.sources.IDataSource;
import fr.multimc.api.commons.data.sources.database.Database;
import fr.multimc.api.commons.data.sources.rest.RestAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.logging.Logger;

public class DataSourceLoader {

    private final FileConfiguration config;
    private final File dataFolder;
    private final Logger logger;

    /**
     * Create a new DataSourceLoader
     * @param config {@link FileConfiguration} of the plugin
     * @param dataFolder {@link File} that is the data folder of the plugin
     * @param logger {@link Logger} of the plugin
     */
    public DataSourceLoader(@NotNull FileConfiguration config, File dataFolder, Logger logger){
        this.config = config;
        this.dataFolder = dataFolder;
        this.logger = logger;
    }

    /**
     * Load the data source from the config file
     * @return {@link IDataSource} and throw an error if unknown data source type
     */
    @SuppressWarnings("DataFlowIssue")
    @Nullable
    public IDataSource loadDataSource() {
        IDataSource dataSource;
        switch (this.config.getString("DataSource.Type")){
            case "SQLITE" -> dataSource = this.loadSQLiteDataSource();
            case "MYSQL" -> dataSource = this.loadMySQLDataSource();
            case "REST_API" -> dataSource = this.loadRestAPIDataSource();
            default -> throw new IllegalArgumentException("DataSource.Type must be SQLITE, MYSQL or REST_API");
        }
        return dataSource;
    }

    @NotNull
    private IDataSource loadSQLiteDataSource(){
        String path = "%s/%s".formatted(this.dataFolder.getPath(), this.config.getString("DataSource.SQLite.FileName"));
        return new Database(new File(path), this.logger);

    }

    @NotNull
    private IDataSource loadMySQLDataSource(){
        String host = this.config.getString("DataSource.MySQL.HostName");
        int port = this.config.getInt("DataSource.MySQL.Port");
        String username = this.config.getString("DataSource.MySQL.Username");
        String password = this.config.getString("DataSource.MySQL.Password");
        String database = this.config.getString("DataSource.MySQL.Database");
        return new Database(host, port, username, password, database, this.logger);
    }

    @NotNull
    private IDataSource loadRestAPIDataSource(){
        String url = this.config.getString("DataSource.RestApi.Url");
        String username = this.config.getString("DataSource.RestApi.Username");
        String password = this.config.getString("DataSource.RestApi.Password");
        RestAPI api = new RestAPI(this.logger, url, username, password);
        if(!api.login()) throw new IllegalArgumentException("Cannot login to RestAPI");
        return api;
    }
}
