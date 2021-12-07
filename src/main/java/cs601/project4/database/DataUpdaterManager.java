package cs601.project4.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DataUpdaterManager {
    public static void updateUser(Connection con, String name, String email, String zipcode, String userId) throws SQLException {
        String updateUserSql = "";

        if (!zipcode.equals("")) {
            updateUserSql = "UPDATE user SET name = ?, email = ?, zipcode = ? WHERE user_id = '" + userId + "';";
        } else {
            updateUserSql = "UPDATE user SET name = ?, email = ? WHERE user_id = '" + userId + "';";
        }

        PreparedStatement updateUserStmt = con.prepareStatement(updateUserSql);

        updateUserStmt.setString(1, name);
        updateUserStmt.setString(2, email);
        updateUserStmt.setString(3, zipcode);
        updateUserStmt.executeUpdate();
    }
}
