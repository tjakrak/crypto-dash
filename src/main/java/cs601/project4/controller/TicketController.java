package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.database.DataInsertionManager;
import cs601.project4.server.LoginServerConstants;
import cs601.project4.utilities.gson.ClientInfo;
import cs601.project4.utilities.gson.Event;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Controller
public class TicketController {

    @PostMapping("/ticket/{id}/purchase")
    public String postPurchaseTicket(@PathVariable (value = "id") int id,
                                     @RequestParam("number-of-tickets") String numOfTickets,
                                     Model model, HttpServletRequest req) {

        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginServerConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        if (numOfTickets.equals("Select")) {
            String response = "Please put a number of ticket that you would like to buy";
        }

        int price = 0;
        try (Connection connection = DBCPDataSource.getConnection()){
            List<Event> listEvents = DataFetcherManager.getEvents(connection, 0, null, id);
            if (listEvents.size() == 1) {
                Event event = listEvents.get(0);
                price = event.getTicketPrice();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        price = price * Integer.parseInt(numOfTickets);
        String.valueOf(price);
        String ticketQty = numOfTickets;

        model.addAttribute("numTickets", ticketQty);
        model.addAttribute("price", price);


        return "ticket-purchase";
    }
}
