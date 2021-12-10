package cs601.project4.database;

import cs601.project4.tableobject.ClientInfo;
import cs601.project4.tableobject.Event;

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

    public static List<Event> getEvents(Connection con, int size, String zipcode, int eventId) throws SQLException {

        String selectAllContactsSql = "SELECT * FROM event;";
        if (zipcode != null && size > 0 && eventId != 0) {
            selectAllContactsSql = "SELECT * FROM event WHERE zipcode = '" + zipcode + "' WHERE event_id = '" +
                    eventId + "' LIMIT " + size + ";";
        } else if (zipcode != null && size > 0) {
            selectAllContactsSql = "SELECT * FROM event WHERE zipcode = '" + zipcode + "' LIMIT " + size + ";";
        } else if (zipcode != null) {
            selectAllContactsSql = "SELECT * FROM event WHERE zipcode = '" + zipcode + "';";
        } else if(size > 0) {
            selectAllContactsSql = "SELECT * FROM event LIMIT " + size + ";";
        } else if (eventId != 0) {
            selectAllContactsSql = "SELECT * FROM event WHERE event_id = " + eventId + ";";
        }

        PreparedStatement selectAllEventsStmt = con.prepareStatement(selectAllContactsSql);
        ResultSet results = selectAllEventsStmt.executeQuery();

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

//    public static ClientInfo getTickets(Connection con, String userId) throws SQLException {
//        String selectAllContactsSql = "SELECT * FROM transaction WHERE buyer_id = '" + userId + "';";
//        PreparedStatement selectEmailStmt = con.prepareStatement(selectEmailSql);
//        ResultSet results = selectEmailStmt.executeQuery();
//
//        ClientInfo clientInfo = new ClientInfo();
//
//        if (results.next()) {
//            clientInfo.setName(results.getString("name"));
//            clientInfo.setEmail(results.getString("email"));
//            clientInfo.setZipcode(results.getString("zipcode"));
//        }
//
//        return clientInfo;
//    }

    public static List<Event> getTransactions(Connection con, String sessionId) throws SQLException {
        String selectAllContactsSql = "SELECT * FROM transaction WHERE buyer_id = " +
                "(SELECT user_id FROM user_session WHERE session_id = '" + sessionId + "') OR seller_id =" +
                "(SELECT user_id FROM user_session WHERE session_id = '" + sessionId + "');";

        PreparedStatement selectAllContactsStmt = con.prepareStatement(selectAllContactsSql);
        ResultSet results = selectAllContactsStmt.executeQuery();
        while(results.next()) {
            System.out.printf("Name: %s\n", results.getString("name"));
            System.out.printf("Extension: %s\n", results.getInt("extension"));
            System.out.printf("Email: %s\n", results.getString("email"));
            System.out.printf("Start Date: %s\n", results.getString("startdate"));
        }
        return null;
    }


}
