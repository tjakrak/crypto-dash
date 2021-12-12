package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.utilities.LoginConstants;
import cs601.project4.tableobject.ClientInfo;
//import org.springframework.data.repository.query.Param;
import cs601.project4.tableobject.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Controller
public class HomeController {

    private static final Logger LOGGER = LogManager.getLogger(HomeController.class);
    private static final int EVENT_PER_PAGE = 7;

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
                    null, null, 0, false, 8, 0);
            if (listEvents.size() > 7) {
                listEvents.remove(listEvents.size() - 1);
                model.addAttribute("nextPage", true);
            }

            model.addAttribute("currentPage", 1);
            model.addAttribute("listEvents", listEvents);
            model.addAttribute("name", clientInfo.getName());
            model.addAttribute("isSearchMode", 0);
        } catch(SQLException e) {
            e.printStackTrace();
        }

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

        try (Connection connection = DBCPDataSource.getConnection()){
            List<Event> listEvents = DataFetcherManager.getSearch(connection, search, 8, 0);

            int listSize = listEvents.size();
            if (listEvents.size() > 7) {
                listEvents.remove(listEvents.size() - 1);
                model.addAttribute("nextPage", true);
            }

            model.addAttribute("listEvents", listEvents);
            model.addAttribute("name", clientInfo.getName());
            model.addAttribute("currentPage", 1);
            model.addAttribute("isSearchMode", 1);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return "home";
    }

    @GetMapping("/home/{pageNum}/search-mode/{bool}")
    public String getEventPage(@PathVariable(name = "pageNum") int pageNum,
                               @PathVariable(name = "bool") int isSearchMode,
                               Model model, HttpServletRequest req) {
        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        int limit = pageNum * EVENT_PER_PAGE;
        int offset = limit - EVENT_PER_PAGE;
        try (Connection connection = DBCPDataSource.getConnection()){
            List<Event> listEvents = DataFetcherManager.getEvents(connection,
                    null, null, 0, false, limit + 1, offset);

            int listSize = listEvents.size();
            if (listSize > limit) {
                model.addAttribute("nextPage", true);
                listEvents.remove(listSize - 1);
            }

            model.addAttribute("listEvents", listEvents);
            model.addAttribute("name", clientInfo.getName());
            model.addAttribute("currentPage", pageNum);
            model.addAttribute("isSearchMode", isSearchMode);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return "home";
    }
}
