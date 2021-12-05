package cs601.project4.controller.home;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LogoutController {
    @GetMapping("/logout")
    public String logout(Model model, HttpServletRequest req) {
        req.getSession().invalidate();
        return "redirect:/";
    }
}
