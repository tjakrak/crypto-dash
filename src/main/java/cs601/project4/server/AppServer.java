package cs601.project4.server;
import com.google.gson.Gson;
import cs601.project4.servlets.LandingServlet;
import cs601.project4.servlets.LoginServlet;
import cs601.project4.servlets.LogoutServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import cs601.project4.utilities.Config;

import java.io.FileReader;

public class AppServer {
    public static final int PORT = 8080;
    private static final String configFilename = "config.json";

    /**
     * A helper method to start the server.
     * @throws Exception -- generic Exception thrown by server start method
     */
    public static void startup() throws Exception {

        // read the client id and secret from a config file
        Gson gson = new Gson();
        Config config = gson.fromJson(new FileReader(configFilename), Config.class);

        // create a new server
        Server server = new Server(PORT);

        // make the config information available across servlets by setting an
        // attribute in the context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setAttribute(LoginServerConstants.CONFIG_KEY, config);

        // the default path will direct to a landing page with
        // "Login with Slack" button
        context.addServlet(LandingServlet.class, "/");

        // Once authenticated, Slack will redirect the user
        // back to /login
        context.addServlet(LoginServlet.class, "/login");

        // handle logout
        context.addServlet(LogoutServlet.class, "/logout");

        // start it up!
        server.setHandler(context);
        server.start();
    }

    public static void main(String[] args) {
        try {
            startup();
        } catch(Exception e) {
            // catch generic Exception as that is what is thrown by server start method
            e.printStackTrace();
        }
    }
}
