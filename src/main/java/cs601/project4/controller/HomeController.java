package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.server.AppEvent;
import cs601.project4.server.LoginServerConstants;
import cs601.project4.utilities.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model, HttpServletRequest req) throws FileNotFoundException {
        // retrieve the ID of this session
        String sessionId = req.getSession(true).getId();

        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);

        if (clientInfoObj != null) {
            System.out.printf("Client with session ID %s already exists.\n", sessionId);
            return "redirect:/home ";
        }
        String state = sessionId;
        String nonce = LoginUtilities.generateNonce(state);

        // retrieve the config info from the context
        Config config = (Config) req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);

        System.out.println(config);

        // Generate url for request to Slack

        String url = LoginUtilities.generateSlackAuthorizeURL("2464212157.2668274745974",
                state,
                nonce,
                "https://14ae-99-174-175-222.ngrok.io/login");

        model.addAttribute("url", url);

        req.getSession().invalidate();

        return "index";
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // retrieve the ID of this session
        String sessionId = req.getSession(true).getId();
        System.out.println(sessionId);

        // determine whether the user is already authenticated
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);
        if(clientInfoObj != null) {
            // already authed, no need to log in
            return "error";
        }

        // retrieve the config info from the context
        Config con = AppEvent.getConfig();
        req.getSession().setAttribute(LoginServerConstants.CONFIG_KEY, new Gson().toJson(con));
        //Config config = (Config) req.getSession().getServletContext().getAttribute(LoginServerConstants.CONFIG_KEY);
        //Config config = (Config) req.getSession().getAttribute(LoginServerConstants.CONFIG_KEY);

        Gson gson = new Gson();
        Config config = gson.fromJson((String) req.getSession().getAttribute(LoginServerConstants.CONFIG_KEY), Config.class);
        //Config conf = (Config) req.getSession().getAttribute(LoginServerConstants.CONFIG_KEY);
        System.out.println(config.toString());

        String code = req.getParameter(LoginServerConstants.CODE_KEY);

        String url = LoginUtilities.generateSlackTokenURL(config.getClient_id(), config.getClient_secret(), code, config.getRedirect_url());

        String responseString = HTTPFetcher.doGet(url, null);

        Map<String, Object> response = LoginUtilities.jsonStrToMap(responseString);

        ClientInfo clientInfo = LoginUtilities.verifyTokenResponse(response, sessionId);

        if(clientInfo == null) {
            return "login";
        } else {
            return "login";
        }

    }

    @GetMapping("/home")
    public String home(Model model, HttpServletRequest request) {
        System.out.println("HOME "+ request.getSession().getId());
        model.addAttribute("name", "rg");
        return "home";
    }

}
