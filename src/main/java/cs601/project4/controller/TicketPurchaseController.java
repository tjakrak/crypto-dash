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
public class TicketPurchaseController {

    /**
     * A method to handle POST ticket purchase by id request
     */
    @PostMapping("/ticket/{id}/purchase")
    public String postTicketPurchase(@PathVariable (value = "id") int eventId,
                                     @RequestParam("number-of-tickets") String numOfTickets,
                                     Model model, HttpServletRequest req) {

        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // If the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        double price = 0.0;
        Event event = getEventFromDatabaseByEventId(eventId); // Get event information from the event id
        if (event != null) {
            price = event.getTicketPrice();
        }

        price = price * Integer.parseInt(numOfTickets);
        String.valueOf(price);

        model.addAttribute("numTickets", numOfTickets);
        model.addAttribute("price", price);
        model.addAttribute("id", eventId);

        return "ticket-purchase";
    }

    /**
     * A method to handle POST request of verification of the purchased ticket
     */
    @PostMapping("/ticket/{id}/verified")
    public String postTicketVerified(@PathVariable (value = "id") int eventId,
                                     @RequestParam("num-of-ticket") String ticketQtyStr,
                                     Model model, HttpServletRequest req) {

        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // If the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        int ticketQty = Integer.parseInt(ticketQtyStr);
        Event event = getEventFromDatabaseByEventId(eventId);

        String responseMsg = "";    // Response message that will be sent to the user
        boolean isSuccess = false;  // True if transaction is completed

        if (event != null) {
            responseMsg = "Sorry, there are only " + event.getTicketAvailable() + " tickets left";
        }

        if (event != null && event.getTicketAvailable() >= ticketQty) { // Check if the tickets are not sold out
            int updatedTicketSold = event.getTicketSold() + ticketQty;
            int updatedTicketAvailable = event.getTicketAvailable() - ticketQty;

            String sellerId = getOrganizerId(eventId);
            String buyerId = clientInfo.getUniqueId();

            if (sellerId.equals(buyerId)) { // Making sure the organizer is not buying their own event ticket
                responseMsg = "Sorry, cannot process the transaction because you are the organizer of this event.";
            } else {
                updateEventInDatabase(eventId, updatedTicketSold, updatedTicketAvailable);
                List<Ticket> ticketList = getAvailableTickets(sellerId, eventId, ticketQty);
                updateTicketAndTransaction(ticketList, buyerId, sellerId);

                responseMsg = "Thank you for purchasing with us! Enjoy your upcoming event!";
                isSuccess = true;
            }
        }

        model.addAttribute("responseMsg", responseMsg);
        model.addAttribute("isSuccess", isSuccess);

        return ("ticket-purchase-verified");
    }

    /**
     * Helper method to get event from database based on the event id
     *
     * @param eventId unique id of an event
     * @return        event information that is contained in an Event object
     */
    private Event getEventFromDatabaseByEventId(int eventId) {
        Event event = null;
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

    /**
     * Helper method to update the number of ticket sold, ticket available, ticket total
     * in the database.
     *
     * @param eventId unique id of an event
     */
    private void updateEventInDatabase(int eventId, int ticketSold, int ticketAvailable) {
        try (Connection connection = DBCPDataSource.getConnection()){
            DataUpdaterManager.updateEvent(connection, eventId, ticketSold, ticketAvailable);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method to get organizer/seller id through event id.
     *
     * @param eventId unique id of an event
     * @return        unique id of the seller/organizer of the event
     */
    private String getOrganizerId(int eventId) {
        Event event;
        try (Connection connection = DBCPDataSource.getConnection()){
            List<Event> listEvents = DataFetcherManager.getEvents(
                    connection, null, null, eventId, false, 0, 0);
            if (listEvents.size() == 1) {
                event = listEvents.get(0);
                return event.getOrganizerId();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Helper method to locate a list of ticket id that can be sold to the user
     *
     * @param userId  unique id of a user
     * @param eventId unique id of an event
     * @param size    the number of ticket that will get bought
     * @return        list of ticket id that can be bought
     */
    private List<Ticket> getAvailableTickets(String userId, int eventId, int size) {
        List<Ticket> listTickets = null;
        try (Connection connection = DBCPDataSource.getConnection()){
            listTickets = DataFetcherManager.getTickets(connection, userId, eventId, false, size);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return listTickets;
    }

    /**
     * Helper method to update the ticket ownership in the database and
     * record the transaction of each ticket to the transaction table.
     *
     * @param ticketList  the list of ticket id that are available to sell/transfer
     * @param recipientId the user id of the person who buy/get the ticket
     * @param senderId    the user id of the person who sell/send the ticket
     */
    private void updateTicketAndTransaction(List<Ticket> ticketList, String recipientId, String senderId) {
        try (Connection connection = DBCPDataSource.getConnection()){
            for (int i = 0; i < ticketList.size(); i++) {
                int ticketId = ticketList.get(i).getTicketId();
                DataUpdaterManager.updateTicket(connection, ticketId, recipientId);
                DataInsertionManager.insertToTransaction(connection, ticketId, recipientId, senderId);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
