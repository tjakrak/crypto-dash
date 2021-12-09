package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.database.DataInsertionManager;
import cs601.project4.server.LoginServerConstants;
import cs601.project4.utilities.gson.ClientInfo;
import cs601.project4.utilities.gson.Event;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class EventController {

    @GetMapping("/event/{id}")
    public String getEvent(@PathVariable(value = "id") int id,
                           Model model, HttpServletRequest req) {

        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginServerConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        try (Connection connection = DBCPDataSource.getConnection()){
            List<Event> listEvents = DataFetcherManager.getEvents(connection, 0, null, id);
            if (listEvents.size() == 1) {
                Event event = listEvents.get(0);
                model.addAttribute("event", event);
            }
            model.addAttribute("name", clientInfo.getName());
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return("event-details");
    }


    @GetMapping("/event/create")
    public String getCreateEvent(Model model, HttpServletRequest req) {

        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);

        if (clientInfoObj == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginServerConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login ";
        }

        String currTime = getCurrTimeInString();

        model.addAttribute("startDate", currTime);
        model.addAttribute("endDate", currTime);
        model.addAttribute("ticketPrice", 0);
        model.addAttribute("ticketTotal", 0);
        model.addAttribute("description", "");

        return "event-create";
    }


    /**
     * a POST method to parse data from the user input and store it in MySql database
     */
    @PostMapping("/event/create")
    public String postCreateEvent(
            @RequestParam("event-name") String eventName,
            @RequestParam("start-date") String startDate,
            @RequestParam("end-date") String endDate,
            @RequestParam("ticket-price") String ticketPriceStr,
            @RequestParam("ticket-total") String ticketTotalStr,
            @RequestParam("address") String address,
            @RequestParam("city") String city,
            @RequestParam("state") String state,
            @RequestParam("zipcode") String zipcode,
            @RequestParam("description") String description,
            HttpServletRequest req) {

        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfoObj == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginServerConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        try {
            Timestamp startTimeStamp = dbDateFormatter(startDate);
            Timestamp endTimeStamp = dbDateFormatter(endDate);

            String response = validateInput(eventName, startTimeStamp,
                    endTimeStamp, address, city, state, zipcode);
            String userId = clientInfo.getUniqueId();

            if (response != null) { // if the user did not put the correct information
                return "redirect:/event/create";
            }

            double ticketPrice = Double.parseDouble(ticketPriceStr);
            int ticketTotal = Integer.parseInt(ticketTotalStr);

            if (ticketTotal < 0) { // handle negative value
                ticketTotal = 0;
            }

            try (Connection connection = DBCPDataSource.getConnection()){
                DataInsertionManager.insertToEvent(
                        connection, eventName, startTimeStamp, endTimeStamp,
                        description, ticketPrice, ticketTotal, 0,
                        ticketTotal, userId, address, city, state, zipcode
                );

            } catch(SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e){
            System.out.println(e);
        }

        return "event-create";
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
        newTimeFormat.setCharAt(10, 'T');
//        System.out.println(newTimeFormat);

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

    private String validateInput(String eventName, Timestamp startTime, Timestamp endTime,
                                 String address, String city, String state, String zipcode) {

        if (eventName == null || address == null || city == null || state == null || zipcode == null) {
            return "One or more fields is empty";
        } if (startTime.compareTo(endTime) > 0) {
            return "End date has to be later than the start date";
        }

        return null;
    }
}
