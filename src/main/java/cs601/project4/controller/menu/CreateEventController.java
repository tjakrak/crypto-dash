package cs601.project4.controller.menu;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@Controller
public class CreateEventController {

    @GetMapping("/event/create")
    public String getCreateEvent(Model model) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        model.addAttribute("eventName", "coding bootcamp");
        model.addAttribute("startDate", timestamp);
        model.addAttribute("endDate", timestamp);
        model.addAttribute("ticketPrice", 20);
        model.addAttribute("description", "coding bootcamp special");

        return "create-event";
    }

    @PostMapping("/event/create")
    public String postCreateEvent(
            @RequestParam("event-name") String eventName,
            @RequestParam("start-date") Timestamp startDate,
            @RequestParam("end-date") Timestamp endDate,
            @RequestParam("ticket-price") String ticketPrice,
            @RequestParam("description") String description,
            HttpServletRequest req) {

        System.out.println(startDate.toString());
        return "create-event";
    }
}
