package cs601.project4.controller.home;

import com.google.gson.Gson;
import cs601.project4.server.LoginServerConstants;
import cs601.project4.utilities.gson.ClientInfo;
//import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String getHome(Model model, HttpServletRequest req) {
        System.out.println("HOME "+ req.getSession().getId());
        Gson gson = new Gson();
        ClientInfo clientInfo = gson.fromJson((String) req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY), ClientInfo.class);
        //model.addAttribute("email"), clientInfo.get);
        model.addAttribute("name", clientInfo.getName());
        return "home";
    }

//    @GetMapping("/all-events/{pageNum}")
//    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
//                             @Param("sortField") String sortField,
//                             HttpServletRequest req) {
//        return "/";
//    }
}
