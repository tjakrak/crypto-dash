package cs601.project4.database;

import cs601.project4.tableobject.ClientInfo;
import cs601.project4.tableobject.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class DataFetcherManagerTest {
    private static final Logger LOGGER = LogManager.getLogger(DataFetcherManagerTest.class);

    @BeforeAll
    public static void initialSetup() {
        LOGGER.info("startup the server");
        //inserting data to user table
        insertDataToUserTable();
    }

    public static void insertDataToUserTable() {
        LOGGER.info("inserting fake data");
        String insertMockUserSql = "INSERT INTO user (user_id, name, email, zipcode, created_at) VALUES (?, ?, ?, ?, ?);";

        try (Connection con = DBCPDataSource.getConnection()){
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            PreparedStatement insertMockUserStmt = con.prepareStatement(insertMockUserSql);
            insertMockUserStmt.setString(1, "XXXXXXXXXXXXXXXXXXXX");
            insertMockUserStmt.setString(2, "UnitTest");
            insertMockUserStmt.setString(3, "unittest@dons.usfca.edu");
            insertMockUserStmt.setString(4, "99999");
            insertMockUserStmt.setTimestamp(5, timestamp);
            insertMockUserStmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void destroy() {
        LOGGER.info("delete fake data");
        String deleteUser =
                "DELETE FROM user WHERE user_id = 'XXXXXXXXXXXXXXXXXXXX';";
        try (Connection con = DBCPDataSource.getConnection()){
            PreparedStatement DeleteUserStmt = con.prepareStatement(deleteUser);
            DeleteUserStmt.execute();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void isUserIdExistTrue() {
        try (Connection connection = DBCPDataSource.getConnection()) {
            Boolean isExist = DataFetcherManager.isUserIdExist(connection, "XXXXXXXXXXXXXXXXXXXX");
            Assertions.assertTrue(isExist);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void isUserIdExistFalse() {
        try (Connection connection = DBCPDataSource.getConnection()){
            Boolean isExist = DataFetcherManager.isUserIdExist(connection, "NNNNNNNNNNNNNNNNNNNN");
            Assertions.assertFalse(isExist);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getClientInfoTest() {
        String expected = "UnitTest";
        try (Connection connection = DBCPDataSource.getConnection()){
            ClientInfo clientInfo = DataFetcherManager.getClientInfo(connection, "XXXXXXXXXXXXXXXXXXXX");
            Assertions.assertEquals(expected, clientInfo.getName());
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getClientIdByEmailTest() {
        String expected = "XXXXXXXXXXXXXXXXXXXX";
        try (Connection connection = DBCPDataSource.getConnection()){
            ClientInfo clientInfo = DataFetcherManager.getClientIdByEmail(connection, "unittest@dons.usfca.edu");
            Assertions.assertEquals(expected, clientInfo.getUniqueId());
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getUserEventsInfoTest() {
        String expected = "Chinese New Year 2022";
        try (Connection connection = DBCPDataSource.getConnection()){
            List<Event> eventList = DataFetcherManager.getUserCurrentEventsInfo(connection, "U02KS0CHMMKT02DN684M");
            if (eventList != null) {
                String eventName = eventList.get(0).getName();
                Assertions.assertEquals(eventName, expected);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
