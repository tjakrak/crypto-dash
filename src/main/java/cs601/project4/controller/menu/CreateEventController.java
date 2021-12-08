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
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class CreateEventController {

    @GetMapping("/event/create")
    public String getCreateEvent(Model model) {
        Timestamp currTime = new Timestamp(System.currentTimeMillis());
        String currTimeStr = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(currTime);

//        min="2017-06-01T08:30"

        model.addAttribute("eventName", "coding bootcamp");
        model.addAttribute("startDate", currTime);
        model.addAttribute("endDate", currTime);
        model.addAttribute("ticketPrice", 20);
        model.addAttribute("description", "coding bootcamp special");

        return "create-event";
    }

    private String dateFormatter(String date) {
        StringBuilder dateNewFormat = new StringBuilder(date);
        dateNewFormat.setCharAt(10, ' ');
        dateNewFormat.append(":00");

        return dateNewFormat.toString();
    }

    @PostMapping("/event/create")
    public String postCreateEvent(
            @RequestParam("event-name") String eventName,
            @RequestParam("start-date") String startDate,
            @RequestParam("end-date") String endDate,
//            @RequestParam("ticket-price") Float ticketPrice,
            @RequestParam("description") String description,
            HttpServletRequest req) {


        StringBuilder dateNewFormat = new StringBuilder(startDate);
        dateNewFormat.setCharAt(10, ' ');
        dateNewFormat.append(":00");

        startDate = dateNewFormat.toString();

        dateNewFormat = new StringBuilder(endDate);
        dateNewFormat.setCharAt(10, ' ');
        dateNewFormat.append(":00");

        endDate = dateNewFormat.toString();

        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        String userId = clientInfo.getUniqueId();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date parsedDate = dateFormat.parse(startDate);
            Timestamp startTimestamp = new java.sql.Timestamp(parsedDate.getTime());
            parsedDate = dateFormat.parse(endDate);
            Timestamp endTimeStamp = new java.sql.Timestamp(parsedDate.getTime());

            try (Connection connection = DBCPDataSource.getConnection()){
                DataInsertionManager.insertToEvent(connection, eventName, startTimestamp, endTimeStamp, description,
                        0, 0, 0, 0, userId, "", "", "",
                        "");
            } catch(SQLException e) {
                e.printStackTrace();
            }

        } catch(Exception e) { //this generic but you can control another types of exception
            // look the origin of excption
        }


//        Connection con, String eventName, Timestamp startDate, Timestamp endDate,
//                String description, double ticketPrice, int ticketTotal, int ticketSold,
//        int ticketAvailable, String organizer, String address, String city,
//                String state, String zipCode) throws SQLException



        return "create-event";
    }
}
