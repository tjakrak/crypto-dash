package cs601.project4.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DataUpdaterManager {
    public static void updateUser(Connection con, String name, String email, String zipcode, String userId) throws SQLException {
        String updateUserSql = ";";

        if (!zipcode.equals("")) {
            updateUserSql = "UPDATE user SET name = ?, email = ?, zipcode = ? WHERE user_id = '" + userId + "';";
        } else {
            updateUserSql = "UPDATE user SET name = ?, email = ? WHERE user_id = '" + userId + "';";
        }

        PreparedStatement updateUserStmt = con.prepareStatement(updateUserSql);

        updateUserStmt.setString(1, name);
        updateUserStmt.setString(2, email);

        if (!zipcode.equals("")) {
            updateUserStmt.setString(3, zipcode);
        }

        updateUserStmt.executeUpdate();
    }

    public static void updateEvent(Connection con, int eventId, int ticketSold, int ticketAvailable) throws SQLException {
        String updateEventSql = ";";
//        if (!zipcode.equals("")) {

        updateEventSql = "UPDATE event SET ticket_sold = ?, ticket_available = ? WHERE event_id = " + eventId + ";";
//        } else {
//            updateUserSql = "UPDATE user SET name = ?, email = ? WHERE user_id = '" + userId + "';";
//        }

        PreparedStatement updateEventStmt = con.prepareStatement(updateEventSql);

        updateEventStmt.setInt(1, ticketSold);
        updateEventStmt.setInt(2, ticketAvailable);
        updateEventStmt.executeUpdate();
    }
}
