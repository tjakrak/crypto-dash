package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
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
public class MyTicketController {

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

    @PostMapping("/ticket/{id}/transfer")
    public String postTicketTransfer(
            @PathVariable(value = "id") int eventId,
            @RequestParam(value = "ticket-qty") String ticketQtyStr,
            @RequestParam(value = "email") String recipientEmail,
            Model model, HttpServletRequest req) {
        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        String userId = clientInfo.getUniqueId();
        int ticketQty = Integer.parseInt(ticketQtyStr);
        ClientInfo recipientInfo = getRecipientId(recipientEmail);
        String recipientId = recipientInfo.getUniqueId();

        List<Ticket> ticketList = getAvailableTickets(userId, ticketQty);
        updateTicket(ticketList, recipientId);

        String responseMsg = "Ticket has been transfered";
        boolean isSuccess = true;
        model.addAttribute("responseMsg", responseMsg);
        model.addAttribute("isSuccess", isSuccess);

        return "ticket-transfer-verified";
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

    private int getTicketCountFromDatabase(String userId, int eventId) {
        int ticketCount = 0;
        try (Connection connection = DBCPDataSource.getConnection()){
            ticketCount = DataFetcherManager.getTicketCount(connection, userId, eventId);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return ticketCount;
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

    private ClientInfo getRecipientId(String email) {
        ClientInfo clientInfo = new ClientInfo();
        try (Connection connection = DBCPDataSource.getConnection()){
            clientInfo = DataFetcherManager.getClientInfo(connection, null, email);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return clientInfo;
    }

    private void updateTicket(List<Ticket> ticketList, String recipientId) {
        try (Connection connection = DBCPDataSource.getConnection()){
            for (int i = 0; i < ticketList.size(); i++) {
                Ticket currTicket = ticketList.get(i);
                DataUpdaterManager.updateTicket(connection, currTicket.getTicketId(), recipientId);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

}
