package cs601.project4.controller.setting;

import com.google.gson.Gson;
import cs601.project4.server.LoginServerConstants;
import cs601.project4.utilities.gson.ClientInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SettingController {
    Gson gson = new Gson();

    @GetMapping("/user-settings")
    public String getUserSetting(Model model, HttpServletRequest req) {
        ClientInfo clientInfo = gson.fromJson((String) req.getSession().getAttribute(LoginServerConstants.CLIENT_INFO_KEY), ClientInfo.class);
        model.addAttribute("currName", clientInfo.getName());
        model.addAttribute("currEmail", clientInfo.getEmail());
        model.addAttribute("settingBean", new SettingBean());
        return "setting";
    }

    @PostMapping("/user-settings")
    public String postUserSetting(@ModelAttribute("settingBean") SettingBean settingBean) {
        System.out.println("name: " + settingBean.getName());
        //update sql database;
        return "redirect:/";
    }

}
