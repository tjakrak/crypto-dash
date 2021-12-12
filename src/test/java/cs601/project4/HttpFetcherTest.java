package cs601.project4;

import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.tableobject.Event;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class HttpFetcherTest {

    @Test
    public void isUserIdExistTrue() {
        try (Connection connection = DBCPDataSource.getConnection()){
            Boolean isExist = DataFetcherManager.isUserIdExist(connection, "U02KS0CHMMKT02DN684M");
            Assertions.assertTrue(isExist);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void isUserIdExistFalse() {
        try (Connection connection = DBCPDataSource.getConnection()){
            Boolean isExist = DataFetcherManager.isUserIdExist(connection, "U02");
            Assertions.assertFalse(isExist);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getUserEventsInfoTestAssertEventName() {
        String expected = "Chinese New Year 2022";
        try (Connection connection = DBCPDataSource.getConnection()){
            List<Event> eventList = DataFetcherManager.getUserEventsInfo(connection, "U02KS0CHMMKT02DN684M");
            if (eventList != null) {
                String eventName = eventList.get(0).getName();
                Assertions.assertEquals(eventName, expected);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

}
