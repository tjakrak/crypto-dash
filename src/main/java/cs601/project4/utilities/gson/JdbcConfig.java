package cs601.project4.utilities.gson;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * A class to store the properties of the JSON configuration file.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.datasource")
public class JdbcConfig {

    private String url;
    private String username;
    private String password;

    /**
     * Config class constructor.
     * @param url
     * @param username
     * @param password
     */
    public JdbcConfig(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Return the database property.
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * Return the username property.
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Return the password property.
     * @return
     */
    public String getPassword() {
        return password;
    }
}
