package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.utilities.LoginConstants;
import cs601.project4.tableobject.ClientInfo;
import cs601.project4.tableobject.Event;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Controller
public class EventDetailsController {

    /**
     * A GET method for specific event id
     */
    @GetMapping("/event/{id}")
    public String getEvent(@PathVariable(value = "id") int eventId,
                           Model model, HttpServletRequest req) {

        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        Event event = getEventByIdFromDatabase(eventId);

        model.addAttribute("event", event);

        return("event-details");
    }

    /**
     * A helper method to get event information from the database
     *
     * @param eventId unique id of an event
     */
    public Event getEventByIdFromDatabase(int eventId) {
        Event event = new Event();

        try (Connection connection = DBCPDataSource.getConnection()){
            List<Event> listEvents = DataFetcherManager.getEvents(connection,
                    null, null, eventId, false, 0, 0);
            if (listEvents.size() == 1) {
                event = listEvents.get(0);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return event;
    }

}
