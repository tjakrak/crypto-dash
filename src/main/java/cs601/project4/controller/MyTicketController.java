package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.database.DataUpdaterManager;
import cs601.project4.tableobject.Event;
import cs601.project4.utilities.LoginConstants;
import cs601.project4.tableobject.ClientInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MyTicketController {

    @GetMapping("/ticket/my-ticket")
    public String getHome(Model model, HttpServletRequest req) {
        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        List<Event> listEvents = getUserEventList(clientInfo.getUniqueId());
        if (listEvents != null) {
            model.addAttribute("listEvents", listEvents);
        }

        return "my-ticket";
    }


    @GetMapping("/ticket/{id}")
    public String getTicket(@PathVariable (value = "id") int id, Model model, HttpServletRequest req) {

        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }


        return "ticket";
    }

    private List<Event> getUserEventList(String userId) {
        List<Event> eventList = new ArrayList<>();
        try (Connection connection = DBCPDataSource.getConnection()){
             eventList = DataFetcherManager.getUserEventsInfo(connection, userId);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return eventList;
    }


}
