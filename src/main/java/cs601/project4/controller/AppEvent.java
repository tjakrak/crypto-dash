package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.utilities.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.FileNotFoundException;
import java.io.FileReader;

@SpringBootApplication
public class AppEvent {
    public static void main(String[] args) throws FileNotFoundException {
        // read the client id and secret from a config file
        SpringApplication app = new SpringApplication(AppEvent.class);
        app.run(args);
    }

    @ModelAttribute("searchObject")
    public static Config getConfig() throws FileNotFoundException {
        Gson gson = new Gson();
        Config config = gson.fromJson(new FileReader("Config.json"), Config.class);
        return config;
    }

}

