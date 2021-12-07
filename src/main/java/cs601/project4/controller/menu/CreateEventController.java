package cs601.project4.controller.menu;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

public class CreateEventController {
    @GetMapping("/event/create")
    public String postEvent(Model model, HttpServletRequest req) {
        return "event";
    }
}
