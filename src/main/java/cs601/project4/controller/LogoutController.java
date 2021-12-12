package cs601.project4.controller;

import cs601.project4.utilities.LoginConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LogoutController {

    /**
     * A method to handle GET logout request
     *
     * @param req servletRequest contains that contains sessionId and its attributes
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest req) {

        // check if the user is not logged in
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        if (clientInfoObj == null) {
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        req.getSession().invalidate();
        return "redirect:/";
    }
}
