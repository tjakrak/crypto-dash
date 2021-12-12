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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

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

        return "redirect:/home/1/search-mode/0/q=";
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

        return "redirect:/home/1/search-mode/1/q=" + search;
    }

    @RequestMapping(value = "/home/{pageNum}/search-mode/{bool}/q={searchQuery}", method = RequestMethod.GET)
    public String getEventPage(
            @PathVariable(name = "pageNum") int pageNum,
            @PathVariable(name = "bool") int isSearchMode,
            @PathVariable String searchQuery,               // Equal to "" if there is no query
            Model model, HttpServletRequest req
    ) {
        System.out.println(searchQuery);
        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login";
        }

        int limit = pageNum * EVENT_PER_PAGE;
        int offset = (limit > EVENT_PER_PAGE) ? limit - EVENT_PER_PAGE : 0;

        List<Event> eventList = getListOfAllEvents(limit + 1, offset);

        if (isSearchMode == 1) {
            eventList = getListOfFilteredEvents(searchQuery, limit, offset);
        }

        int listSize = eventList.size();
        if (listSize > limit) {
            model.addAttribute("nextPage", true);
            eventList.remove(listSize - 1);
        }

        model.addAttribute("eventList", eventList);
        model.addAttribute("name", clientInfo.getName());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("isSearchMode", isSearchMode);
        model.addAttribute("searchQuery", searchQuery);

        return "home";
    }

    private List<Event> getListOfAllEvents(int limit, int offset) {
        List<Event> eventList = new ArrayList<>();
        try (Connection connection = DBCPDataSource.getConnection()){
            eventList = DataFetcherManager.getEvents(
                    connection, null, null, 0, false, limit, offset
            );
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return eventList;
    }

    private List<Event> getListOfFilteredEvents(String search, int limit, int offset) {
        List<Event> eventList = new ArrayList<>();
        try (Connection connection = DBCPDataSource.getConnection()){
            eventList = DataFetcherManager.getSearch(connection, search, 8, 0);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return eventList;
    }

}
