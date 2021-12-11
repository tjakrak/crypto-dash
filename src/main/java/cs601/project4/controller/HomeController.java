package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.utilities.LoginConstants;
import cs601.project4.tableobject.ClientInfo;
//import org.springframework.data.repository.query.Param;
import cs601.project4.tableobject.Event;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
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
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        try (Connection connection = DBCPDataSource.getConnection()){
            List<Event> listEvents = DataFetcherManager.getEvents(connection,
                    null, null, 0, false, 5);
            if (listEvents.size() > 5) {
                model.addAttribute("showMore", "true");
            }
//            clientInfo = DataFetcherManager.getClientInfo(connection, clientInfo.getUniqueId());
            model.addAttribute("listEvents", listEvents);
            model.addAttribute("name", clientInfo.getName());
        } catch(SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("totalPages", 5);
        model.addAttribute("currentPage", 1);
        return "home";
    }


    @PostMapping("/home")
    public String postHome(@RequestParam("search-bar") String search, Model model, HttpServletRequest req) {
        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        System.out.println(search);

        return "home";
    }
//    @GetMapping("/all-events/{pageNum}")
//    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
//                             @Param("sortField") String sortField,
//                             HttpServletRequest req) {
//        return "/";
//    }
}
