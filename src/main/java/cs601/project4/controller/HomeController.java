package cs601.project4.controller;

import cs601.project4.server.LoginServerConstants;
import cs601.project4.utilities.LoginUtilities;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        // retrieve the ID of this session
        String sessionId = request.getSession(true).getId();

        Object clientInfoObj = request.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);

        if (clientInfoObj != null) {
            System.out.printf("Client with session ID %s already exists.\n", sessionId);
            return "redirect:/home ";
        }
        String state = sessionId;
        String nonce = LoginUtilities.generateNonce(state);

        // Generate url for request to Slack
//        String url = LoginUtilities.generateSlackAuthorizeURL(config.getClient_id(),
//                state,
//                nonce,
//                config.getRedirect_url());



        model.addAttribute("url", url);
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model, HttpServletRequest request) {
        model.addAttribute("name", "rg");
        return "home";

    }
}
