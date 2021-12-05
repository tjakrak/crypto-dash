package cs601.project4.controller.home;

import com.google.gson.Gson;
import cs601.project4.server.LoginServerConstants;
import cs601.project4.utilities.gson.ClientInfo;
//import org.springframework.data.repository.query.Param;
import cs601.project4.utilities.gson.Event;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String getHome(Model model, HttpServletRequest req) {
//        System.out.println("HOME SESSION: "+ req.getSession().getId());
        Gson gson = new Gson();
        ClientInfo clientInfo = gson.fromJson((String) req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY), ClientInfo.class);

        // check if the user is not logged in
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);
        if (clientInfoObj == null) {
            return "redirect:/login ";
        }

        List<Event> eventList = new ArrayList<>();
        Event e1 = new Event();
        e1.setEventName("haha");

        Event e2 = new Event();
        e2.setEventName("bbbbb");

        eventList.add(e1);
        eventList.add(e2);

        model.addAttribute("listEvents", eventList);

        model.addAttribute("name", clientInfo.getName());

        return "home";
    }

//    @GetMapping("/all-events/{pageNum}")
//    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
//                             @Param("sortField") String sortField,
//                             HttpServletRequest req) {
//        return "/";
//    }
}
