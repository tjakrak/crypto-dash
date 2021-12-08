package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.database.DataUpdaterManager;
import cs601.project4.server.LoginServerConstants;
import cs601.project4.utilities.gson.ClientInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.SQLException;

@Controller
public class SettingController {
    Gson gson = new Gson();

    @GetMapping("/user-settings")
    public String getUserSetting(Model model, HttpServletRequest req) {
        ClientInfo clientInfo = getClientInfo(req);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginServerConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login ";
        }

        String userId = clientInfo.getUniqueId();

        try (Connection connection = DBCPDataSource.getConnection()){
            clientInfo = DataFetcherManager.getClientInfo(connection, userId);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("currName", clientInfo.getName());
        model.addAttribute("currEmail", clientInfo.getEmail());
        model.addAttribute("currZipcode", clientInfo.getZipcode());
        model.addAttribute("settingBean", new SettingBean());
        return "setting";
    }

    @PostMapping("/user-settings")
    public String postUserSetting(@ModelAttribute("settingBean") SettingBean settingBean, HttpServletRequest req) {
        String name = settingBean.getName();
        String email = settingBean.getEmail();
        String zipcode = settingBean.getZipcode();

        String validationMsg = validateInput(name, email);

        if (validationMsg.equals("validated")) {
            ClientInfo clientInfo = getClientInfo(req);
            String userId = clientInfo.getUniqueId();
            try (Connection connection = DBCPDataSource.getConnection()){
                DataUpdaterManager.updateUser(connection, name, email, zipcode, userId);
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }

        //update sql database;
        return "redirect:/user-settings";
    }

    private String validateInput(String name, String email) {
        if (email.equals("") || name.equals("")) {
            return "Name or email cannot be empty";
        } else if (!email.contains("@") || !email.contains(".")) {
            return "Please put a valid email address";
        } else {
            return "validated";
        }
    }

    // move this to other method
    public ClientInfo getClientInfo(HttpServletRequest req) {
        return gson.fromJson((String) req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY), ClientInfo.class);
    }

}
