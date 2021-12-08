package cs601.project4.controller.home;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.server.LoginServerConstants;
import cs601.project4.utilities.gson.ClientInfo;
//import org.springframework.data.repository.query.Param;
import cs601.project4.utilities.gson.Event;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    /**
     * a method to handle GET home request
     *
     * @param model     a holder for model attributes and is primarily designed for adding attributes to the model
     * @param req       servletRequest contains: session id, attribute, and other information from slack response
     */
    @GetMapping("/home")
    public String getHome(Model model, HttpServletRequest req) {
        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfoObj == null) {
            return "redirect:/login ";
        }

        try (Connection connection = DBCPDataSource.getConnection()){
            List<Event> eventList = DataFetcherManager.getEvents(connection); // get all events from db
            model.addAttribute("listEvents", eventList);
            model.addAttribute("name", clientInfo.getName());
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return "home";
    }


//    @GetMapping("/all-events/{pageNum}")
//    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
//                             @Param("sortField") String sortField,
//                             HttpServletRequest req) {
//        return "/";
//    }
}
