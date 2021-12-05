package cs601.project4.server;

import com.google.gson.Gson;
import cs601.project4.utilities.gson.DbConfig;
import cs601.project4.utilities.gson.SlackConfigApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;
import java.io.FileReader;

@SpringBootApplication(scanBasePackages = "cs601.project4.controller")
public class NoStayHomeAppServer {

    public static final String dbConfigFileName = "db_config.json";
    public static final String slackConfigFileName = "slack_config.json";

    public static void main(String[] args) {
        // read the client id and secret from a config file
        SpringApplication app = new SpringApplication(NoStayHomeAppServer.class);
        app.run(args);
    }

    public static SlackConfigApi readSlackAuthConfig() {
        SlackConfigApi config = null;
        Gson gson = new Gson();
        try {
            config = gson.fromJson(new FileReader(slackConfigFileName), SlackConfigApi.class);
        } catch (FileNotFoundException e) {
            System.err.println("Config file config.json not found: " + e.getMessage());
        }
        return config;
    }

    /**
     * Read in the configuration file.
     * @return
     */
    public static DbConfig readDbConfig() {
        DbConfig config = null;
        Gson gson = new Gson();
        try {
            config = gson.fromJson(new FileReader(dbConfigFileName), DbConfig.class);
        } catch (FileNotFoundException e) {
            System.err.println("Config file config.json not found: " + e.getMessage());
        }
        return config;
    }
}

