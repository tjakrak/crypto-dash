package cs601.project4.database;

import com.google.gson.Gson;
import cs601.project4.utilities.gson.DbConfig;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Example of using Apache DBCP ConnectionPool.
 * Taken from https://www.baeldung.com/java-connection-pooling
 */
public class DBCPDataSource {

    // Apache commons connection pool implementation
    private static BasicDataSource ds = new BasicDataSource();

    // This code inside the static block is executed only once: the first time the class is loaded into memory.
    // -- https://www.geeksforgeeks.org/static-blocks-in-java/
    static {
        DbConfig config = readConfig();
        // TODO: do something other than exit the whole program
        // if the config file cannot be found
        if(config == null) {
            System.exit(1);
        }
        ds.setUrl("jdbc:mysql://localhost:3306/" + config.getDatabase());
        ds.setUsername(config.getUsername());
        ds.setPassword(config.getPassword());
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
    }

    /**
     * Return a Connection from the pool.
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private DBCPDataSource(){ }

    public static final String configFileName = "db_config.json";

    /**
     * Read in the configuration file.
     * @return
     */
    public static DbConfig readConfig() {
        DbConfig config = null;
        Gson gson = new Gson();
        try {
            config = gson.fromJson(new FileReader(configFileName), DbConfig.class);
        } catch (FileNotFoundException e) {
            System.err.println("Config file config.json not found: " + e.getMessage());
        }
        return config;
    }
}