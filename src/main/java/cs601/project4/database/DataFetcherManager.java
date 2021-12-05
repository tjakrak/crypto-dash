package cs601.project4.database;

import cs601.project4.utilities.gson.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DataFetcherManager {

    public static String getUserEmail(Connection con, String sessionId) throws SQLException {
        String selectEmailSql = "SELECT u_s.email FROM user_session u_s JOIN SPRING_SESSION S_S ON " +
                "u_s.session_id = S_S.SESSION_ID WHERE S_S.SESSION_ID = '" + sessionId + "';";
        PreparedStatement selectEmailStmt = con.prepareStatement(selectEmailSql);
        ResultSet results = selectEmailStmt.executeQuery();

        String email = "";
        if (results.next()) {
            email = results.getString("email");
            return email;
        }
        return email;
    }


    public static List<Event> getEvents(Connection con) throws SQLException {
        String selectAllContactsSql = "SELECT * FROM event;";
        PreparedStatement selectAllEventsStmt = con.prepareStatement(selectAllContactsSql);
        ResultSet results = selectAllEventsStmt.executeQuery();
        while(results.next()) {
            System.out.printf("Name: %s\n", results.getString("name"));
            System.out.printf("Extension: %s\n", results.getInt("extension"));
            System.out.printf("Email: %s\n", results.getString("email"));
            System.out.printf("Start Date: %s\n", results.getString("startdate"));
        }
        return null;
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
