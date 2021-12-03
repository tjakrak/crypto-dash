package cs601.project4.server;

import com.google.gson.Gson;
import cs601.project4.utilities.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileNotFoundException;
import java.io.FileReader;

@SpringBootApplication(scanBasePackages = "cs601.project4.controller")
public class AppEvent {
    public static void main(String[] args) throws FileNotFoundException {
        // read the client id and secret from a config file
        SpringApplication app = new SpringApplication(AppEvent.class);
        app.run(args);
    }

    public static Config getConfig() throws FileNotFoundException {
        Gson gson = new Gson();
        Config config = gson.fromJson(new FileReader("Config.json"), Config.class);
        return config;
    }

}

