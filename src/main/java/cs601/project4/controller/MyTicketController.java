package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.server.LoginServerConstants;
import cs601.project4.utilities.gson.ClientInfo;
import cs601.project4.utilities.gson.Event;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Controller
public class MyTicketController {

    @GetMapping("/ticket")
    public String getHome(Model model, HttpServletRequest req) {
        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginServerConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginServerConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

//        try (Connection connection = DBCPDataSource.getConnection()){
//            List<Event> listEvents = DataFetcherManager.getEvents(connection, 5, null, 0);
//            if (listEvents.size() > 5) {
//                model.addAttribute("showMore", "true");
//            }
//            model.addAttribute("listEvents", listEvents);
//            model.addAttribute("name", clientInfo.getName());
//        } catch(SQLException e) {
//            e.printStackTrace();
//        }

        return "my-ticket";
    }


    @GetMapping("/ticket/{id}")
    public String getTicket(@PathVariable (value = "id") int id, Model model, HttpServletRequest req) {

        return "ticket";
    }
}
