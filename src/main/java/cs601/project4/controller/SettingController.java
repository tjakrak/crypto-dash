package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.database.DataUpdaterManager;
import cs601.project4.utilities.LoginConstants;
import cs601.project4.tableobject.ClientInfo;
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

    /**
     * A method to handle GET user setting page
     *
     * @return setting.html page
     */
    @GetMapping("/user/settings")
    public String getUserSetting(Model model, HttpServletRequest req) {
        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");
            return "redirect:/login ";
        }

        String userId = clientInfo.getUniqueId();

        clientInfo = getClientInfoDatabase(userId);

        model.addAttribute("currName", clientInfo.getName());
        model.addAttribute("currEmail", clientInfo.getEmail());
        model.addAttribute("currZipcode", clientInfo.getZipcode());
        model.addAttribute("clientInfo", new ClientInfo());
        return "setting";
    }

    /**
     * A method to handle POST user setting page
     *
     * @return setting.html
     */
    @PostMapping("/user/settings")
    public String postUserSetting(@ModelAttribute("settingBean") ClientInfo settingBean,
                                  Model model, HttpServletRequest req) {
        Gson gson = new Gson();
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

        if (clientInfo == null) { // if the user hasn't logged in to the app
            req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "1");

            return "redirect:/login ";
        }

        String name = settingBean.getName();
        String email = settingBean.getEmail();
        String zipcode = settingBean.getZipcode();
        String userId = clientInfo.getUniqueId();

        String responseMsg = validateInput(name, email);

        if (responseMsg.equals("validated")) {
            responseMsg = "Your changes has been saved!";
            updateUserInfoDatabase(name, email, zipcode, userId);
        }

        clientInfo = getClientInfoDatabase(userId);

        // update the userinfo in the session attribute
        req.getSession().removeAttribute(LoginConstants.CLIENT_INFO_KEY);
        req.getSession().setAttribute(LoginConstants.CLIENT_INFO_KEY,  new Gson().toJson(clientInfo));

        model.addAttribute("responseMsg", responseMsg);

        return "setting";
    }

    /**
     * Validate input when the user changing their profile data
     *
     * @param name  updated name of the user
     * @param email updated email of the user
     * @return      response message for the user
     */
    private String validateInput(String name, String email) {
        if (email.equals("") || name.equals("")) {
            return "Name or email cannot be empty";
        } else if (!email.contains("@") || !email.contains(".")) {
            return "Please put a valid email address";
        } else {
            return "validated";
        }
    }

    /**
     * Helper method to get clientInfo by their user id
     *
     * @param userId unique id of the user
     * @return       user info in a clientInfo object form
     */
    private ClientInfo getClientInfoDatabase(String userId) {
        ClientInfo clientInfo = new ClientInfo();

        try (Connection connection = DBCPDataSource.getConnection()){
            clientInfo = DataFetcherManager.getClientInfo(connection, userId);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return clientInfo;
    }


    /**
     * Helper method to update user info after the user updated their profile
     *
     * @param name      name of the user
     * @param email     unique email of the user
     * @param zipcode   zip code of the user
     * @param userId    unique id of the user
     */
    private void  updateUserInfoDatabase(String name, String email, String zipcode, String userId) {
        try (Connection connection = DBCPDataSource.getConnection()){
            DataUpdaterManager.updateUser(connection, name, email, zipcode, userId);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

}
