package cs601.project4.controller;

import cs601.project4.utilities.LoginConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class LandingPageController {

    /**
     * A method to handle GET landing page request
     *
     * @param req servletRequest contains: session id, attribute, and other information from slack response
     */
    @GetMapping("/")
    public String getLanding(HttpServletRequest req) {
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        if (clientInfoObj != null) {
            return "redirect:/home";
        } else {
            return "redirect:/login";
        }
    }
}
