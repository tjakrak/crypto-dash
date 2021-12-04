package cs601.project4.controller.home;

import com.google.gson.Gson;
import cs601.project4.server.NoStayHomeAppServer;
import cs601.project4.server.LoginServerConstants;
import cs601.project4.utilities.*;
import cs601.project4.utilities.gson.ClientInfo;
import cs601.project4.utilities.gson.SlackConfigApi;
//import org.springframework.data.repository.query.Param;
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
    public String getindex(Model model, HttpServletRequest req) throws FileNotFoundException {
        // retrieve the ID of this session
        String sessionId = req.getSession(true).getId();
        System.out.println(sessionId);

        // determine whether the user is already authenticated
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);
        if (clientInfoObj != null) {
            return "redirect:/home ";
        }

        // to be passed to slackAPI for oauth
        String state = sessionId;
        String nonce = LoginUtilities.generateNonce(state);

        // adding client_key, secret_key and redirect uri information to the session attribute (so it can be shared across different controller)
        // store it as json formatted string (GSON -> json)
        SlackConfigApi config = NoStayHomeAppServer.getConfig();
        req.getSession().setAttribute(LoginServerConstants.CONFIG_KEY, new Gson().toJson(config));

        // Generate url to send a request to SlackApi
        String url = LoginUtilities.generateSlackAuthorizeURL(config.getClient_id(),
                state,
                nonce,
                config.getRedirect_url());

        // adding the url to model so it can be read by thymeleaf html
        model.addAttribute("url", url);

        return "index";
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // retrieve the ID of this session
        String sessionId = req.getSession(true).getId();
//        System.out.println("session " + sessionId);
//        System.out.println(req.getCookies()[0].getName());

        // determine whether the user is already authenticated
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);
        if(clientInfoObj != null) {
            // already authed, no need to log in
            return "redirect:/home ";
        }

        // retrieve the config info from the session attribute and convert it to Config object
        Gson gson = new Gson();
        SlackConfigApi slackConfigApi = gson.fromJson((String) req.getSession().getAttribute(LoginServerConstants.CONFIG_KEY), SlackConfigApi.class);

        // getting the "code" parameter from the SLACK response
        String code = req.getParameter(LoginServerConstants.CODE_KEY);

        // generate string url for slack from the config: client_id, client_secret, redirect_url
        String url = LoginUtilities.generateSlackTokenURL(slackConfigApi.getClient_id(), slackConfigApi.getClient_secret(), code, slackConfigApi.getRedirect_url());

        // send get response to slack with the url generated above
        String responseString = HTTPFetcher.doGet(url, null);

        // get the slack json response and put it to hashmap as key and value
        Map<String, Object> response = LoginUtilities.jsonStrToMap(responseString);

        // verifying the response from slack API
        ClientInfo clientInfo = LoginUtilities.verifyTokenResponse(response, sessionId);

        // if the user is not verified
        if(clientInfo == null) {
            return "redirect: /";
        // if the user is verified
        } else {
            req.getSession().setAttribute(LoginServerConstants.CLIENT_INFO_KEY, new Gson().toJson(clientInfo));
            return "redirect:/home";
        }

    }

    @GetMapping("/home")
    public String home(Model model, HttpServletRequest req) {
        System.out.println("HOME "+ req.getSession().getId());
        Gson gson = new Gson();
        ClientInfo clientInfo = gson.fromJson((String) req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY), ClientInfo.class);
        //model.addAttribute("email"), clientInfo.get);
        model.addAttribute("name", clientInfo.getName());
        return "home";
    }

    @GetMapping("/logout")
    public String logout(Model model, HttpServletRequest req) {
        req.getSession().invalidate();
        return "redirect:/";
    }

//    @GetMapping("/all-events/{pageNum}")
//    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
//                             @Param("sortField") String sortField,
//                             HttpServletRequest req) {
//        return "/";
//    }
}
