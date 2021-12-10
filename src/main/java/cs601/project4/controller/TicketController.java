package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.database.DataInsertionManager;
import cs601.project4.database.DataUpdaterManager;
import cs601.project4.tableobject.Ticket;
import cs601.project4.utilities.LoginConstants;
import cs601.project4.tableobject.ClientInfo;
import cs601.project4.tableobject.Event;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
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
    public String postTicketVerified(@PathVariable (value = "id") int eventId,
                                     @RequestParam("num-of-ticket") String numOfTicketsStr,
                                     Model model, HttpServletRequest req) {

        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        int numOfTicket = Integer.parseInt(numOfTicketsStr);
        Event event = getEventFromDatabase(eventId);
        String responseMsg = "Sorry, there are only " + event.getTicketAvailable() + " tickets left";

        if (event != null && event.getTicketAvailable() >= numOfTicket) { // check if the tickets are not sold out
            int ticketSold = event.getTicketSold() + numOfTicket;
            int ticketAvailable = event.getTicketAvailable() - numOfTicket;
            updateEventInDatabase(eventId, ticketSold, ticketAvailable); // update the ticket amount in db

            String sellerId = getSellerId(eventId);
            String buyerId = clientInfo.getUniqueId();

            if (sellerId != null) {
                List<Ticket> ticketList = getAvailableTickets(sellerId, numOfTicket);
                for (int i = 0; i < numOfTicket; i++) {
                    insertTransactionDatabase(eventId, buyerId, sellerId); // record transaction in the db
                    updateTicketInDatabase(ticketList.get(i).getTicketId(), buyerId); // update the ticket owner
                }
            }
            responseMsg = "Thank you for purchasing with us! Enjoy your upcoming event!";
        }

        model.addAttribute("responseMsg", responseMsg);

        return ("ticket-verified");
    }

    private Event getEventFromDatabase(int id) {
        Event event = null;
        try (Connection connection = DBCPDataSource.getConnection()){
            List<Event> listEvents = DataFetcherManager.getEvents(connection,
                    null, null, id, false, 0);
            if (listEvents.size() == 1) {
                event = listEvents.get(0);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return event;
    }

    private void updateEventInDatabase(int eventId, int ticketSold, int ticketAvailable) {
        try (Connection connection = DBCPDataSource.getConnection()){
            DataUpdaterManager.updateEvent(connection, eventId, ticketSold, ticketAvailable);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private String getSellerId(int eventId) {
        Event event = null;
        try (Connection connection = DBCPDataSource.getConnection()){
            List<Event> listEvents = DataFetcherManager.getEvents(connection,
                    null, null, eventId, false, 0);
            if (listEvents.size() == 1) {
                event = listEvents.get(0);
                return event.getOrganizer();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void insertTransactionDatabase(int ticketId, String buyerId, String sellerId) {
        try (Connection connection = DBCPDataSource.getConnection()){
            DataInsertionManager.insertToTransaction(connection, ticketId, buyerId, sellerId);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Ticket> getAvailableTickets(String userId, int size) {
        List<Ticket> listTickets = null;
        try (Connection connection = DBCPDataSource.getConnection()){
            listTickets = DataFetcherManager.getTickets(connection, userId, false, size);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return listTickets;
    }

    private void updateTicketInDatabase(int ticketId, String userId) {
        try (Connection connection = DBCPDataSource.getConnection()){
            DataUpdaterManager.updateTicket(connection, ticketId, userId);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
}
