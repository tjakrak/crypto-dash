package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.database.DataInsertionManager;
import cs601.project4.database.DataUpdaterManager;
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
    public String postTicketPurchase(@PathVariable (value = "id") int id,
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

        double price = 0.00;
        Event event = getEventFromDatabase(id);
        if (event != null) {
            price = event.getTicketPrice();
        }

        price = price * Integer.parseInt(numOfTickets);
        String.valueOf(price);

        model.addAttribute("numTickets", numOfTickets);
        model.addAttribute("price", price);
        model.addAttribute("id", id);

        return "ticket-purchase";
    }

    @PostMapping("/ticket/{id}/verified")
    public String postTicketVerified(@PathVariable (value = "id") int id,
                                     @RequestParam("num-of-ticket") String numOfTicketsStr,
                                     @RequestParam("ticket-price") String ticketPriceStr,
                                     Model model, HttpServletRequest req) {

        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginServerConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        int numOfTicket = Integer.parseInt(numOfTicketsStr);
        double ticketPrice = Double.parseDouble(ticketPriceStr);

        Event event = getEventFromDatabase(id);

        if (event != null && event.getTicketAvailable() >= numOfTicket) {
            int ticketSold = event.getTicketSold() + numOfTicket;
            int ticketAvailable = event.getTicketAvailable() - numOfTicket;
            updateEventDatabase(id, ticketSold, ticketAvailable);

            String adminId = "00000000000000000000";
            String buyerId = clientInfo.getUniqueId();
            insertTransactionDatabase(ticketPrice, id, buyerId, adminId);
        }

        return ("ticket-verified");
    }


    private Event getEventFromDatabase(int id) {
        Event event = null;
        try (Connection connection = DBCPDataSource.getConnection()){
            List<Event> listEvents = DataFetcherManager.getEvents(connection, 0, null, id);
            if (listEvents.size() == 1) {
                event = listEvents.get(0);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return event;
    }


    private void updateEventDatabase(int eventId, int ticketSold, int ticketAvailable) {
        try (Connection connection = DBCPDataSource.getConnection()){
            DataUpdaterManager.updateEvent(connection, eventId, ticketSold, ticketAvailable);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }


    private void insertTransactionDatabase(double ticketPrice, int eventId, String buyerId, String sellerId) {
        try (Connection connection = DBCPDataSource.getConnection()){
            DataInsertionManager.insertToTransaction(connection, ticketPrice, eventId, buyerId, sellerId);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
}
