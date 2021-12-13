package cs601.project4.database;

import java.sql.*;


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
        String insertEventSql = "INSERT INTO event (name, start_date, end_date, description, ticket_price, " +
                "ticket_total, ticket_sold, ticket_available, organizer_id, address, city, state, zip_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

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

    public static void insertToTicket(Connection con, String userId, int eventId) throws SQLException {
        String insertTicketSql = "INSERT INTO ticket (user_id, event_id) VALUES (?, ?);";
        PreparedStatement insertTicketStmt = con.prepareStatement(insertTicketSql);
        insertTicketStmt.setString(1, userId);
        insertTicketStmt.setInt(2, eventId);
        insertTicketStmt.executeUpdate();
    }

    public static void insertToTransaction(Connection con, int ticketId, String buyerId,
                                           String sellerId) throws SQLException {
        String insertTransactionSql = "INSERT INTO transaction (transaction_date, ticket_id, " +
                "buyer_id, seller_id) VALUES (?, ?, ?, ?);";

        PreparedStatement insertTransactionStmt = con.prepareStatement(insertTransactionSql);
        Timestamp transactionDate = new Timestamp(System.currentTimeMillis());

        insertTransactionStmt.setTimestamp(1, transactionDate);
        insertTransactionStmt.setInt(2, ticketId);
        insertTransactionStmt.setString(3, buyerId);
        insertTransactionStmt.setString(4, sellerId);

        insertTransactionStmt.executeUpdate();
    }

}
