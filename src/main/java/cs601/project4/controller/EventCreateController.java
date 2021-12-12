package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.database.DataInsertionManager;
import cs601.project4.tableobject.ClientInfo;
import cs601.project4.tableobject.Event;
import cs601.project4.utilities.LoginConstants;
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
import java.util.List;

@Controller
public class EventCreateController {

    /**
     * A GET method for create event page. Redirect the user to the create event form page.
     */
    @GetMapping("/event/create")
    public String getCreateEvent(Model model, HttpServletRequest req) {
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);

        if (clientInfoObj == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login ";
        }

        String currTime = getCurrTimeInString(); // get the curr time in String

        // add attributes information to set up initial value in the input box
        model.addAttribute("startDate", currTime);
        model.addAttribute("endDate", currTime);
        model.addAttribute("ticketPrice", 0);
        model.addAttribute("ticketTotal", 0);
        model.addAttribute("description", "");

        return "event-create";
    }

    /**
     * A POST method to parse data from the user input and store it in MySql database
     */
    @PostMapping("/event/create")
    public String postCreateEvent(
            @RequestParam("event-name") String eventName,        // Name of the event
            @RequestParam("start-date") String startDate,        // Validated in html startDate >= currDate
            @RequestParam("end-date") String endDate,            // Validated in html endDate >= currDate
            @RequestParam("ticket-price") String ticketPriceStr, // Ticket price in String
            @RequestParam("ticket-total") String ticketTotalStr, // Validated in html file to be always >= 0
            @RequestParam("address") String address,             // Address of the event
            @RequestParam("city") String city,                   // City of the event
            @RequestParam("state") String state,                 // State of the event
            @RequestParam("zipcode") String zipcode,             // Zip code of the event
            @RequestParam("description") String description,     // Description of the event
            Model model, HttpServletRequest req) {

        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfoObj == null) { // If the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        try {
            // Convert String input to the required data type to be inserted to the database
            Timestamp startTimeStamp = dbDateFormatter(startDate);
            Timestamp endTimeStamp = dbDateFormatter(endDate);
            double ticketPrice = Double.parseDouble(ticketPriceStr);
            int ticketTotal = Integer.parseInt(ticketTotalStr);

            String userId = clientInfo.getUniqueId();

            // Validate the user input
            String responseMsg = validateInput(eventName, startTimeStamp,
                    endTimeStamp, address, city, state, zipcode);

            if (responseMsg != null) { // If the user did not put the correct information
                model.addAttribute("responseMsg", responseMsg);
                return "event-create";
            }

            // Add created event to the database
            addEventToDatabase(eventName, startTimeStamp, endTimeStamp,
                    description, ticketPrice, ticketTotal, 0, userId,
                    address, city, state, zipcode);

            int eventId = getEventId(clientInfo.getUniqueId()); // Get the eventId of the newly created event

            if (eventId != 0) { // Generate and add tickets for the event to the database
                for (int i = 0; i < ticketTotal; i++) {
                    addTicketToDatabase(clientInfo.getUniqueId(), eventId);
                }
            }
        } catch (Exception e){
            System.out.println(e);
        }

        // Generate and forward success response msg to the user through html file
        String responseMsg = "Event created successfully";
        model.addAttribute("responseMsg", responseMsg);

        return "event-create-verified";
    }

    /**
     * A helper method to get current date in Timestamp and format the date to a String
     * with proper format to be compatible with HTML datetime-local format.
     * Convert: "yyyy-MM-dd hh:mm:ss" to "yyyy-MM-ddTHH:mm"
     *          "2021-12-12 10:30:00" to "2021-12-12T10:30"
     */
    private String getCurrTimeInString() {
        Timestamp currTime = new Timestamp(System.currentTimeMillis());
        String currTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(currTime);
        StringBuilder newTimeFormat = new StringBuilder(currTimeStr);
        newTimeFormat.setCharAt(10, 'T');

        return newTimeFormat.toString();
    }

    /**
     * A helper method to convert a date in String to a date in Timestamp
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

    /**
     * A helper method to validate user input when he/she creating an event
     *
     * @param eventName         the name of the event
     * @param startTime         the time and date when the event ends
     * @param endTime           the time and date when the event ends
     * @param address           the specific address for the event
     * @param city              the city where the event is held
     * @param state             the state where the event is held
     * @param zipcode           the zipcode where the event is held
     */
    private String validateInput(String eventName, Timestamp startTime, Timestamp endTime,
                                 String address, String city, String state, String zipcode) {
        if (eventName == "" || address == "" || city == "" || state == "" || zipcode == "") {
            return "One or more fields is empty";
        }

        if (startTime.compareTo(endTime) > 0) {
            return "End date has to be later than the start date";
        }

        return null;
    }

    /**
     * A helper method to add ticket to the database after an event is created.
     * This method get called as many times as the number of tickets will be provided
     * for the event.
     *
     * @param userId    user unique id
     * @param eventId   the id of the event
     */
    public void addTicketToDatabase(String userId, int eventId) {
        try (Connection connection = DBCPDataSource.getConnection()){
            DataInsertionManager.insertToTicket(connection, userId, eventId);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * A helper method to add event to the database once an event is created.
     *
     * @param eventName         the name of the event
     * @param startTime    the time and date when the event ends
     * @param endTime      the time and date when the event ends
     * @param description       description of the event
     * @param ticketPrice       price of each ticket
     * @param ticketTotal       total number of ticket
     * @param ticketSold        number of ticket sold
     * @param userId            a unique id of the user
     * @param address           the specific address for the event
     * @param city              the city where the event is held
     * @param state             the state where the event is held
     * @param zipcode           the zipcode where the event is held
     */
    private void addEventToDatabase(
            String eventName, Timestamp startTime, Timestamp endTime,
            String description, double ticketPrice, int ticketTotal, int ticketSold,
            String userId, String address, String city, String state, String zipcode
    ) {
        try (Connection connection = DBCPDataSource.getConnection()){
            DataInsertionManager.insertToEvent(
                    connection, eventName, startTime, endTime,
                    description, ticketPrice, ticketTotal, ticketSold,
                    ticketTotal, userId, address, city, state, zipcode
            );

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * A helper method to call to database to get the latest event_id once the user
     * created an event. The reason for this is we need to know the event_id to create
     * list of tickets for this particular event_id.
     *
     * @param userId a unique id of the user
     */
    private int getEventId(String userId) {
        try (Connection connection = DBCPDataSource.getConnection()){
            List<Event> listEvents = DataFetcherManager.getEvents(connection,
                    userId, null, 0, true, 1, 0);
            if (listEvents.size() == 1) {
                Event event = listEvents.get(0);
                return event.getEventId();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
