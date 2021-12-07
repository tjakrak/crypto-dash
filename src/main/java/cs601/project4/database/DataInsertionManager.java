package cs601.project4.database;

import cs601.project4.utilities.gson.Event;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.util.Calendar;
import java.util.List;


public class DataInsertionManager {

    public static void insertToUser(Connection con, String userId, String name, String email, String zipcode) throws SQLException {
        String insertUserSql = "INSERT INTO user (user_id, name, email, zipcode, created_at) VALUES (?, ?, ?, ?, ?);";
        PreparedStatement insertUserStmt = con.prepareStatement(insertUserSql);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // check if names contains other than alpha numeric
        insertUserStmt.setString(1, userId);
        insertUserStmt.setString(2, name);
        insertUserStmt.setString(3, email);
        insertUserStmt.setString(4, zipcode);
        insertUserStmt.setTimestamp(5, timestamp);
        insertUserStmt.executeUpdate();
    }

    public static void insertToEvent(Connection con, String eventName, Timestamp startDate, Timestamp endDate,
                                     String description, double ticketPrice, int ticketTotal, int ticketSold,
                                     int ticketAvailable, String organizer, String address, String city,
                                     String state, String zipCode) throws SQLException {
        String insertEventSql = "INSERT INTO event (event_name, event_start_date, event_end_date," +
                "event_description, ticket_price, ticket_total, ticket_sold, ticket_available, organizer," +
                "address, city, state, zip_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        PreparedStatement insertEventStmt = con.prepareStatement(insertEventSql);
        // check if names contains other than alpha numeric
        insertEventStmt.setString(1, eventName);
        insertEventStmt.setTimestamp(2, startDate);
        insertEventStmt.setTimestamp(3, endDate);
        insertEventStmt.setString(4, description);
        insertEventStmt.setDouble(5, ticketPrice);
        insertEventStmt.setInt(6, ticketTotal);
        insertEventStmt.setInt(7, ticketSold);
        insertEventStmt.setInt(8, ticketAvailable);
        insertEventStmt.setString(9, organizer);
        insertEventStmt.setString(10, address);
        insertEventStmt.setString(11, city);
        insertEventStmt.setString(12, state);
        insertEventStmt.setString(13, zipCode);

        insertEventStmt.executeUpdate();
    }

    public static void insertToUserToSession(Connection con, String userId, String sessionId) throws SQLException {
        String insertUserToSessionSql = "INSERT INTO user_session (user_id, session_id) VALUES (?, ?);";
        PreparedStatement insertUserToSessionStmt = con.prepareStatement(insertUserToSessionSql);
        insertUserToSessionStmt.setString(1, userId);
        insertUserToSessionStmt.setString(2, sessionId);
        insertUserToSessionStmt.executeUpdate();
    }

    public static void insertToTicket(Connection con, int eventId) throws SQLException {
        String insertTicketSql = "INSERT INTO ticket (event_id) VALUES (?, ?);";
        PreparedStatement insertTicketStmt = con.prepareStatement(insertTicketSql);
        insertTicketStmt.setInt(2, eventId);
        insertTicketStmt.executeUpdate();
    }

    public static void insertToTransaction(Connection con, Timestamp transactionDate, double ticketPrice,
                                           int ticketId, int buyerId, int sellerId) throws SQLException {
        String insertTransactionSql = "INSERT INTO transaction (transaction_date, ticket_price, ticket_id, buyer_id," +
                "seller_id) VALUES (?, ?, ?, ?, ?);";
        PreparedStatement insertTransactionStmt = con.prepareStatement(insertTransactionSql);
        insertTransactionStmt.setTimestamp(1, transactionDate);
        insertTransactionStmt.setDouble(2, ticketPrice);
        insertTransactionStmt.setInt(3, ticketId);
        insertTransactionStmt.setInt(4, buyerId);
        insertTransactionStmt.setInt(5, sellerId);

        insertTransactionStmt.executeUpdate();
    }


    public static void main(String[] args){
        try (Connection connection = DBCPDataSource.getConnection()){
//            insertToUser(connection, "Jose", 9984, "jsmith", "2026-09-01");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }


//    @Value("${spring.datasource.url}")
//    private String url;
//
//    @Value("${spring.datasource.username")
//    private String username;
//
//    @Value("${spring.datasource.password")
//    private String password;

}
