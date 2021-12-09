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
public class EventController {

    @GetMapping("/event/{id}")
    public String getEvent(@PathVariable(value = "id") int id, Model model, HttpServletRequest req) {

        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginServerConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        try (Connection connection = DBCPDataSource.getConnection()){
            List<Event> listEvents = DataFetcherManager.getEvents(connection, 0, null, id);
            if (listEvents.size() == 1) {
                Event event = listEvents.get(0);
                model.addAttribute("event", event);
            }
            model.addAttribute("name", clientInfo.getName());
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return("event-details");
    }
}
