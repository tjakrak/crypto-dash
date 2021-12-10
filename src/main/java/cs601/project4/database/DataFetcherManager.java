package cs601.project4.database;

import cs601.project4.tableobject.ClientInfo;
import cs601.project4.tableobject.Event;
import cs601.project4.tableobject.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataFetcherManager {

    public static Boolean isUserIdExist(Connection con, String userId) throws SQLException {
        String selectEmailSql = "SELECT user_id FROM user WHERE user_id = '" + userId + "';";
        PreparedStatement selectEmailStmt = con.prepareStatement(selectEmailSql);
        ResultSet results = selectEmailStmt.executeQuery();

        String duplicateUserId = "";
        if (results.next()) {
            duplicateUserId = results.getString("user_id");
        }

        if (duplicateUserId == "") {
            return false;
        } else {
            return true;
        }
    }

    public static ClientInfo getClientInfo(Connection con, String userId) throws SQLException {
        String selectEmailSql = "SELECT * FROM user WHERE user_id = '" + userId + "';";
        PreparedStatement selectEmailStmt = con.prepareStatement(selectEmailSql);
        ResultSet results = selectEmailStmt.executeQuery();

        ClientInfo clientInfo = new ClientInfo();

        if (results.next()) {
            clientInfo.setName(results.getString("name"));
            clientInfo.setEmail(results.getString("email"));
            clientInfo.setZipcode(results.getString("zipcode"));
        }

        return clientInfo;
    }

    public static List<Event> getEvents(Connection con, String organizerId, String zipcode,
                                        int eventId, Boolean isDescending, int size) throws SQLException {
        StringBuffer selectEventSql = new StringBuffer();
        selectEventSql.append("SELECT * FROM event");
        //String selectEventSql = "SELECT * FROM event;";

        if (zipcode != null && eventId != 0) {
            selectEventSql.append(" WHERE zipcode = '" + zipcode + "' AND WHERE event_id = '" + eventId + "'");
        } else if (zipcode != null) {
            selectEventSql.append(" WHERE zipcode = '" + zipcode + "'");
        } else if (eventId != 0) {
            selectEventSql.append(" WHERE event_id = " + eventId);
        } else if (organizerId != null) {
            selectEventSql.append(" WHERE organizer_id = '" + organizerId + "'");
        }

        if (isDescending) {
            selectEventSql.append(" ORDER BY event_id DESC");
        }

        if(size > 0) {
            selectEventSql.append(" LIMIT " + size);
        }

        selectEventSql.append(";");

        PreparedStatement selectEventsStmt = con.prepareStatement(selectEventSql.toString());
        ResultSet results = selectEventsStmt.executeQuery();

        List<Event> listOfEvents = new ArrayList<>();
        while(results.next()) {
            Event event = new Event();
            event.setEventId(results.getInt("event_id"));
            event.setName(results.getString("name"));
            event.setStartDate(results.getTimestamp("start_date"));
            event.setEndDate(results.getTimestamp("end_date"));
            event.setDescription(results.getString("description"));
            event.setTicketPrice(results.getInt("ticket_price"));
            event.setTicketTotal(results.getInt("ticket_total"));
            event.setTicketSold(results.getInt("ticket_sold"));
            event.setTicketAvailable(results.getInt("ticket_available"));
            event.setOrganizer(results.getString("organizer_id"));
            event.setAddress(results.getString("address"));
            event.setCity(results.getString("city"));
            event.setState(results.getString("state"));
            event.setZipCode(results.getString("zip_code"));
            event.setFullAddress();
            listOfEvents.add(event);;
        }

        return listOfEvents;
    }

    public static List<Ticket> getTickets(Connection con, String userId, boolean isDescending, int size) throws SQLException {
        String selectTicketSql = "SELECT * FROM ticket WHERE user_id = '" + userId + "' ORDER BY ticket_id ASC LIMIT " + size +";";
        PreparedStatement selectTicketStmt = con.prepareStatement(selectTicketSql);
        ResultSet results = selectTicketStmt.executeQuery();

        List<Ticket> listOfTickets = new ArrayList<>();
        if (results.next()) {
            Ticket ticket = new Ticket();
            ticket.setTicketId(results.getInt("ticket_id"));
            ticket.setUserId(results.getString("user_id"));
            ticket.setEventId(results.getInt("event_id"));
            listOfTickets.add(ticket);
        }

        return listOfTickets;
    }

    public static List<Event> getTransactions(Connection con, String sessionId) throws SQLException {
        String selectTransactionsSql = "SELECT * FROM transaction WHERE buyer_id = " +
                "(SELECT user_id FROM user_session WHERE session_id = '" + sessionId + "') OR seller_id =" +
                "(SELECT user_id FROM user_session WHERE session_id = '" + sessionId + "');";

        PreparedStatement selectAllContactsStmt = con.prepareStatement(selectTransactionsSql);
        ResultSet results = selectAllContactsStmt.executeQuery();
        while(results.next()) {
            System.out.printf("Name: %s\n", results.getString("name"));
            System.out.printf("Extension: %s\n", results.getInt("extension"));
            System.out.printf("Email: %s\n", results.getString("email"));
            System.out.printf("Start Date: %s\n", results.getString("startdate"));
        }

        return null;
    }

    public static int getTicketCount(Connection con, String userId) throws SQLException {
        String selectTicketCountSql = "SELECT COUNT(*) AS ticket_num FROM ticket WHERE user_id = '" + userId + "';";
        PreparedStatement selectTicketStmt = con.prepareStatement(selectTicketCountSql);
        ResultSet results = selectTicketStmt.executeQuery();

        int ticketCount = 0;
        if (results.next()) {
            ticketCount = results.getInt("ticket_num");
        }

        return ticketCount;
    }

    public static List<Event> getUserEventsInfo(Connection con, String userId) throws SQLException {
        String getTicketSql =
                "SELECT event.name, user.name " +
                "FROM event JOIN user ON event.organizer_id = user.user_id " +
                "JOIN (SELECT event_id FROM ticket WHERE user_id = '"+ userId +"' " +
                "GROUP BY event_id) t ON t.event_id = event.event_id;";

        PreparedStatement selectTicketStmt = con.prepareStatement(getTicketSql);
        ResultSet results = selectTicketStmt.executeQuery();

        List<Event> eventList = new ArrayList<>();

        if (results.next()) {
            Event event = new Event();
            event.setName(results.getString("event.name"));
            event.setOrganizer(results.getString("user.name"));
            eventList.add(event);
        }

        return eventList;
    }

}
