package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.database.DataInsertionManager;
import cs601.project4.database.DataUpdaterManager;
import cs601.project4.tableobject.Event;
import cs601.project4.tableobject.Ticket;
import cs601.project4.utilities.LoginConstants;
import cs601.project4.tableobject.ClientInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TicketTransferController {

    /**
     * A method to handle GET request to list all the tickets the user has
     *
     * @return my-ticket.html page
     */
    @GetMapping("/ticket/my-ticket")
    public String getMyTicket(Model model, HttpServletRequest req) {
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

    /**
     * A method to handle GET request to transfer ticket transfer of a specific event
     *
     * @return ticket-transfer.html page
     */
    @GetMapping("/ticket/{id}/transfer")
    public String getTicketTransfer(@PathVariable(value = "id") int eventId, Model model, HttpServletRequest req) {
        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        String userId = clientInfo.getUniqueId();
        int maxTicketAvailable = getTicketCountFromDatabase(userId, eventId);

        model.addAttribute("maxTix", maxTicketAvailable);
        model.addAttribute("eventId", eventId);

        return "ticket-transfer";
    }

    /**
     * A method to handle POST request to handle ticket transfer request from the user.
     *
     * @return ticket-transfer-verified.html page
     */
    @PostMapping("/ticket/{id}/transfer")
    public String postTicketTransfer(
            @PathVariable(value = "id") int eventId,
            @RequestParam(value = "ticket-qty") String ticketQtyStr,
            @RequestParam(value = "recipient-email") String recipientEmail,
            Model model, HttpServletRequest req) {
        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        int ticketQty = Integer.parseInt(ticketQtyStr);
        ClientInfo recipientInfo = getRecipientIdByEmailFromDatabase(recipientEmail);
        Event event = getEventFromDatabaseByEventId(eventId);
        String eventOrganizerId = getOrganizerId(eventId);
        String senderId = clientInfo.getUniqueId();
        String recipientId = recipientInfo.getUniqueId();

        List<Ticket> ticketList = getAvailableTickets(senderId, eventId, ticketQty);

        updateTicketAndTransaction(ticketList, recipientId, senderId);

        // if the owner of the event transfer their own ticket need to adjust ticketSold & ticketAvailable
        if (eventOrganizerId != null && eventOrganizerId.equals(senderId)) {
            int updatedTicketSold = event.getTicketSold() + ticketQty;
            int updatedTicketAvailable = event.getTicketAvailable() - ticketQty;
            updateEventInDatabase(eventId, updatedTicketSold, updatedTicketAvailable);
        }

        String responseMsg = "Ticket has been transferred";
        boolean isSuccess = true;
        model.addAttribute("responseMsg", responseMsg);
        model.addAttribute("isSuccess", isSuccess);

        return "ticket-transfer-verified";
    }

    /**
     * A helper method to get the number of ticket that the user has for a particular event
     *
     * @param userId    unique id for the user
     * @return          the list of events of a specific user
     */
    private List<Event> getUserEventList(String userId) {
        List<Event> eventList = new ArrayList<>();
        try (Connection connection = DBCPDataSource.getConnection()){
             eventList = DataFetcherManager.getUserEventsInfo(connection, userId);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return eventList;
    }

    /**
     * A helper method to get the number of ticket that the user has for a particular event
     *
     * @param userId    unique id for the user
     * @param eventId   unique id for the event
     * @return          the number of tickets the user has for a particular event id
     */
    private int getTicketCountFromDatabase(String userId, int eventId) {
        int ticketCount = 0;
        try (Connection connection = DBCPDataSource.getConnection()){
            ticketCount = DataFetcherManager.getTicketCount(connection, userId, eventId);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return ticketCount;
    }


    /**
     * A helper method to get the user id from their email address
     *
     * @param email     unique email of the recipient
     * @return          unique id of the recipient
     */
    private ClientInfo getRecipientIdByEmailFromDatabase(String email) {
        ClientInfo clientInfo = new ClientInfo();
        try (Connection connection = DBCPDataSource.getConnection()){
            clientInfo = DataFetcherManager.getClientIdByEmail(connection, email);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return clientInfo;
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
