package cs601.project4.database;

import cs601.project4.utilities.gson.ClientInfo;
import cs601.project4.utilities.gson.Event;

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

        if (duplicateUserId == null) {
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


    public static List<Event> getEvents(Connection con, int size, String zipcode) throws SQLException {

        String selectAllContactsSql = "SELECT * FROM event;";
        if (zipcode != null && size > 0) {
            selectAllContactsSql = "SELECT * FROM event WHERE zipcode = '" + zipcode + "' LIMIT " + size + ";";
        } else if (zipcode != null) {
            selectAllContactsSql = "SELECT * FROM event WHERE zipcode = '" + zipcode + "';";
        } else if(size > 0) {
            selectAllContactsSql = "SELECT * FROM event LIMIT " + size + ";";
        }

        PreparedStatement selectAllEventsStmt = con.prepareStatement(selectAllContactsSql);
        ResultSet results = selectAllEventsStmt.executeQuery();

        List<Event> listOfEvents = new ArrayList<>();
        while(results.next()) {
            Event event = new Event();
            event.setName(results.getString("name"));
            event.setStartDate(results.getTimestamp("start_date"));
            event.setDescription(results.getString("description"));
            event.setTicketPrice(results.getInt("ticket_price"));
            event.setTicketSold(results.getInt("ticket_sold"));
            event.setTicketAvailable(results.getInt("ticket_available"));
            event.setOrganizer(results.getString("organizer_id"));
            event.setAddress(results.getString("address"));
            event.setCity(results.getString("city"));
            event.setState(results.getString("state"));
            event.setState(results.getString("zip_code"));
            event.setFullAddress();
            listOfEvents.add(event);;
        }
        return listOfEvents;
    }

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
