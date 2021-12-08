package cs601.project4.controller.menu;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataInsertionManager;
import cs601.project4.server.LoginServerConstants;
import cs601.project4.utilities.gson.ClientInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class CreateEventController {

    @GetMapping("/event/create")
    public String getCreateEvent(Model model) {

        String currTime = getCurrTimeInString();
        System.out.println(currTime);
        model.addAttribute("eventName", "coding bootcamp");
        model.addAttribute("startDate", currTime);
        model.addAttribute("endDate", currTime);
        model.addAttribute("ticketPrice", 20);
        model.addAttribute("description", "coding bootcamp special");

        return "create-event";
    }

    /**
     * a POST method to parse data from the user input and store it in MySql database
     */
    @PostMapping("/event/create")
    public String postCreateEvent(
            @RequestParam("event-name") String eventName,
            @RequestParam("start-date") String startDate,
            @RequestParam("end-date") String endDate,
//            @RequestParam("ticket-price") Double ticketPrice,
            @RequestParam("description") String description,
            HttpServletRequest req) {

        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        String userId = clientInfo.getUniqueId();

        try {
            Timestamp startTimeStamp = dbDateFormatter(startDate);
            Timestamp endTimeStamp = dbDateFormatter(endDate);
//            try (Connection connection = DBCPDataSource.getConnection()){
//                DataInsertionManager.insertToEvent(connection, eventName, startTimeStamp, endTimeStamp, description,
//                        0, 0, 0, 0, userId, "", "", "",
//                        "");
//            } catch(SQLException e) {
//                e.printStackTrace();
//            }
        } catch (Exception e){
            System.out.println(e);
        }

//        Connection con, String eventName, Timestamp startDate, Timestamp endDate,
//                String description, double ticketPrice, int ticketTotal, int ticketSold,
//        int ticketAvailable, String organizer, String address, String city,
//                String state, String zipCode) throws SQLException
        return "create-event";
    }

    /**
     * a helper method to get current date in Timestamp and format the date to a String
     * with proper format to be compatible with HTML datetime-local format.
     * Convert: "yyyy-MM-dd hh:mm:ss" to "yyyy-MM-ddTHH:mm"
     *          "2021-12-12 10:30:00" to "2021-12-12T10:30"
     */
    private String getCurrTimeInString() {
        Timestamp currTime = new Timestamp(System.currentTimeMillis());
        String currTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(currTime);
        StringBuilder newTimeFormat = new StringBuilder(currTimeStr);
        System.out.println(newTimeFormat);
        newTimeFormat.setCharAt(10, 'T');
        System.out.println(newTimeFormat);

        return newTimeFormat.toString();
    }

    /**
     * a helper method to convert a date in String to a date in Timestamp
     * with proper format to be compatible with MySql database format.
     * Convert: "yyyy-MM-ddTHH:mm" to "yyyy-MM-dd hh:mm:ss"
     *          "2021-12-12T10:30" to "2021-12-12 10:30:00"
     *
     * @param date date string type
     */
    private Timestamp dbDateFormatter(String date) throws ParseException {
        StringBuilder dateNewFormat = new StringBuilder(date);
        dateNewFormat.setCharAt(10, ' ');
        dateNewFormat.append(":00");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parsedDate = dateFormat.parse(dateNewFormat.toString());
        Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

        return timestamp;
    }

    private String validateInput() {
        return null;
    }
}
