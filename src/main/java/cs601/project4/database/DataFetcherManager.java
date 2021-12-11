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

    public static ClientInfo getClientInfo(Connection con, String userId, String email) throws SQLException {
        StringBuffer selectClientInfoSql = new StringBuffer();
        selectClientInfoSql.append("SELECT * FROM user");

        if (userId != null && email != null) {
            selectClientInfoSql.append(" WHERE user_id = '" + userId + "' AND email = '" + email + "'");
        } else if (userId != null) {
            selectClientInfoSql.append(" WHERE user_id = '" + userId + "'");
        } else if (email != null) {
            selectClientInfoSql.append(" WHERE email = '" + email + "'");
        }

        selectClientInfoSql.append(";");
        PreparedStatement selectClientInfoStmt = con.prepareStatement(selectClientInfoSql.toString());
        ResultSet results = selectClientInfoStmt.executeQuery();

        ClientInfo clientInfo = new ClientInfo();

        if (results.next()) {
            clientInfo.setUniqueId(userId);
            clientInfo.setName(results.getString("name"));
            clientInfo.setEmail(results.getString("email"));
            clientInfo.setZipcode(results.getString("zipcode"));
        }

        return clientInfo;
    }

    public static List<Event> getEvents(Connection con, String organizerId, String zipcode,
                                        int eventId, Boolean isDescending, int limit, int offset) throws SQLException {
        StringBuffer selectEventSql = new StringBuffer();
        selectEventSql.append("SELECT * FROM event JOIN user ON event.organizer_id = user.user_id");
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

        if (limit > 0) {
            selectEventSql.append(" LIMIT " + limit);
        }

        if (offset > 0) {
            selectEventSql.append(" OFFSET " + offset);
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
            event.setOrganizerId(results.getString("organizer_id"));
            event.setOrganizerName(results.getString("user.name"));
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
        while (results.next()) {
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

    public static List<Event> getUserEventsInfo(Connection con, String userId) throws SQLException {
        String getTicketSql =
                "SELECT event.event_id, event.name, user.name, t.ticket_count " +
                "FROM event JOIN user ON event.organizer_id = user.user_id " +
                "JOIN (SELECT event_id, COUNT(event_id) AS ticket_count " +
                "FROM ticket WHERE user_id = '"+ userId +"' " +
                "GROUP BY event_id) t ON t.event_id = event.event_id;";

        PreparedStatement selectTicketStmt = con.prepareStatement(getTicketSql);
        ResultSet results = selectTicketStmt.executeQuery();

        List<Event> eventList = new ArrayList<>();

        while (results.next()) {
            Event event = new Event();
            event.setEventId(results.getInt("event.event_id"));
            event.setName(results.getString("event.name"));
            event.setOrganizerName(results.getString("user.name"));
            event.setTicketCount(results.getInt("t.ticket_count"));
            eventList.add(event);
        }

        return eventList;
    }

    public static int getTicketCount(Connection con, String userId, int eventId) throws SQLException {
        String getTicketCountSql = "SELECT COUNT(*) AS ticket_count FROM ticket WHERE user_id = '"
                + userId + "' GROUP BY event_id = " + eventId + ";";

        PreparedStatement selectTicketCountStmt = con.prepareStatement(getTicketCountSql);
        ResultSet results = selectTicketCountStmt.executeQuery();

        int ticketCount = 0;
        if (results.next()) {
            ticketCount = results.getInt("ticket_count");
        }

        return ticketCount;
    }

    public static List<Event> getSearch(Connection con, String searchQuery, int limit, int offset) throws SQLException {

        searchQuery = (searchQuery == null || searchQuery.equals("")) ? searchQuery : searchQuery + ".*";
        searchQuery = searchQuery.toLowerCase();

        StringBuffer getSearchSql = new StringBuffer();
        getSearchSql.append(
                "SELECT * FROM event JOIN user ON event.organizer_id = user.user_id " +
                "WHERE LOWER(user.name) REGEXP '.*" + searchQuery +"' " +
                "OR LOWER(event.name) REGEXP '.*" + searchQuery + "' " +
                "OR LOWER(event.description) REGEXP '.*" + searchQuery + "' " +
                "OR LOWER(event.address) REGEXP '.*" + searchQuery + "' " +
                "OR LOWER(event.city) REGEXP '.*" + searchQuery + "' " +
                "OR LOWER(event.state) REGEXP '.*" + searchQuery + "' " +
                "OR LOWER(event.zip_code) REGEXP '.*" + searchQuery + "'"
        );

        if (limit > 0) {
            getSearchSql.append(" LIMIT " + limit);
        }

        if (offset > 0) {
            System.out.println("here");
            getSearchSql.append(" OFFSET " + offset);
        }

        getSearchSql.append(";");

        PreparedStatement selectSearchStmt = con.prepareStatement(getSearchSql.toString());
        ResultSet results = selectSearchStmt.executeQuery();

        List<Event> eventList = new ArrayList<>();

        while (results.next()) {
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
            event.setOrganizerId(results.getString("organizer_id"));
            event.setOrganizerName(results.getString("user.name"));
            event.setAddress(results.getString("address"));
            event.setCity(results.getString("city"));
            event.setState(results.getString("state"));
            event.setZipCode(results.getString("zip_code"));
            event.setFullAddress();
            eventList.add(event);;
        }

        return eventList;
    }

}
