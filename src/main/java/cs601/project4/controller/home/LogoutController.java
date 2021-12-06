package cs601.project4.controller.home;

import cs601.project4.server.LoginServerConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LogoutController {
    @GetMapping("/logout")
    public String logout(Model model, HttpServletRequest req) {

        // check if the user is not logged in
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);
        if (clientInfoObj == null) {
            req.getSession().setAttribute(LoginServerConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        req.getSession().invalidate();
        return "redirect:/";
    }
}
