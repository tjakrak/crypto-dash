package cs601.project4.utilities;

import cs601.project4.utilities.gson.Event;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.util.Calendar;
import java.util.List;


public class JdbcManager {

    public static void insertToUser(Connection con, String name, String email, String zipcode, Timestamp startdate) throws SQLException {
        String insertUserSql = "INSERT INTO user (name, email, zipcode, created_at) VALUES (?, ?, ?, ?);";
        PreparedStatement insertUserStmt = con.prepareStatement(insertUserSql);
        // check if names contains other than alpha numeric
        insertUserStmt.setString(1, name);
        insertUserStmt.setString(2, email);
        insertUserStmt.setString(3, zipcode);
        insertUserStmt.setTimestamp(4, startdate);
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

    public static void insertToUserToSession(Connection con, int userId, String sessionId) throws SQLException {
        String insertUserToSessionSql = "INSERT INTO user_session (user_id, session_id) VALUES (?, ?);";
        PreparedStatement insertUserToSessionStmt = con.prepareStatement(insertUserToSessionSql);
        insertUserToSessionStmt.setInt(1, userId);
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

    public static List<Event> executeSelectAndPrint(Connection con) throws SQLException {
        String selectAllContactsSql = "SELECT * FROM event";
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





    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username")
    private String username;

    @Value("${spring.datasource.password")
    private String password;

    public static void main(String[] args){

        /*JdbcConfig config = Utilities.readConfig();
        if(config == null) {
            System.exit(1);
        }*/

        String a = "user037";
        // Make sure that mysql-connector-java is added as a dependency.
        // Force Maven to Download Sources and Documentation
        try (Connection con = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/" + a, a, a)) {

//            executeSelectAndPrint(con);
//            System.out.println("*****************");

            Calendar cal = Calendar.getInstance();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(cal.getTimeInMillis());

            insertToUser(con,"NoStayHome", "rgtjakrakartadinata@dons.usfca.edu", "94536", timestamp);

//            executeSelectAndPrint(con);
//            System.out.println("*****************");
//
//            executeInsert(con,"Bertha", 9876, "bzuniga", "2009-09-01");
//
//            executeSelectAndPrint(con);
//            System.out.println("*****************");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
