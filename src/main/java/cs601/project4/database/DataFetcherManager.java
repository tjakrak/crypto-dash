package cs601.project4.database;

import cs601.project4.utilities.gson.ClientInfo;
import cs601.project4.utilities.gson.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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


    public static List<Event> getEvents(Connection con) throws SQLException {
        String selectAllContactsSql = "SELECT * FROM event;";
        PreparedStatement selectAllEventsStmt = con.prepareStatement(selectAllContactsSql);
        ResultSet results = selectAllEventsStmt.executeQuery();

        List<Event> listOfEvents = new ArrayList<>();
        while(results.next()) {
            Event event = new Event();
            event.setEventName(results.getString("event_name"));
            listOfEvents.add(event);
//            System.out.printf("Name: %s\n", results.getString("name"));
//            System.out.printf("Extension: %s\n", results.getInt("extension"));
//            System.out.printf("Email: %s\n", results.getString("email"));
//            System.out.printf("Start Date: %s\n", results.getString("startdate"));
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
