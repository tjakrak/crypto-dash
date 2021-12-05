package cs601.project4.controller.home;

import cs601.project4.server.LoginServerConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class LandingPageController {
    @GetMapping("/")
    public String getLanding(Model model, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);
        if (clientInfoObj != null) {
            return "redirect:/home";
        } else {
            return "redirect:/login";
        }
    }
}
