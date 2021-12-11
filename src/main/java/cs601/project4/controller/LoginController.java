package cs601.project4.controller;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DataFetcherManager;
import cs601.project4.database.DataInsertionManager;
import cs601.project4.utilities.LoginConstants;
import cs601.project4.server.NoStayHomeAppServer;
import cs601.project4.utilities.HTTPFetcher;
import cs601.project4.utilities.LoginUtilities;
import cs601.project4.tableobject.ClientInfo;
import cs601.project4.config.SlackConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Controller
public class LoginController {

    /**
     * a method to handle GET login request
     *
     * @param model     a holder for model attributes and is primarily designed for adding attributes to the model
     * @param req       servletRequest contains: session id, attribute, and other information from slack response
     */
    @GetMapping("/login")
    public String getLogin(Model model, HttpServletRequest req) throws FileNotFoundException {
        // retrieve the ID of this session
        String sessionId = req.getSession(true).getId();
        // determine whether the user is already authenticated
        Object clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
        // config_key will be uploaded to the session when the user visited login page
        Object clientConfigKeyObj = req.getSession().getAttribute(LoginConstants.SLACK_API_CONFIG_KEY);
        // is_fail value is '1' if the user try to access other pages without logging in, otherwise '0'
        String clientFailToLogin = (String) req.getSession().getAttribute(LoginConstants.IS_FAIL_TO_LOGIN);

        if (clientInfoObj != null) {  // ---------------------------------------------- the user already authenticated

            return "redirect:/home ";

        } else if (clientConfigKeyObj != null && clientFailToLogin.equals("0")) { // -- reset user session
            Boolean verified = slackLoginVerifier(req, sessionId);
            if(!verified) {           // ---------------------------------------------- fail to authenticate user
                req.getSession().invalidate();

                return "redirect:/login";

            } else {                  // ---------------------------------------------- user login successfully
                clientInfoObj = req.getSession().getAttribute(LoginConstants.CLIENT_INFO_KEY);
                boolean isAdded = addNewUserToDb(sessionId, clientInfoObj);

                if (!isAdded) {
                    Gson gson = new Gson();
                    ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);

                    String userId = clientInfo.getUniqueId();
                    clientInfo = getClientInfoDatabase(userId);

                    req.getSession().removeAttribute(LoginConstants.CLIENT_INFO_KEY);
                    req.getSession().setAttribute(LoginConstants.CLIENT_INFO_KEY,  new Gson().toJson(clientInfo));
                }

                return "redirect:/home";

            }
        } else { // the user just visited login page for the first time
            slackLoginPreparer(model, req, sessionId);

            return "login";
        }
    }

    /**
     * A helper wrapper method to help verify the login through slack
     *
     * @param sessionId     current session id
     * @param clientInfoObj obj that store various client information
     */
    private boolean addNewUserToDb(String sessionId, Object clientInfoObj) {
        try (Connection connection = DBCPDataSource.getConnection()){
            Gson gson = new Gson();
            ClientInfo clientInfo = gson.fromJson((String) clientInfoObj, ClientInfo.class);
            String userId = clientInfo.getUniqueId();
            boolean isUserIdExist = DataFetcherManager.isUserIdExist(connection, userId);

            if (!isUserIdExist) {
                String name = clientInfo.getName();
                String email = clientInfo.getEmail();
                String zipcode = "";
                DataInsertionManager.insertToUser(connection, userId, name, email, zipcode);
                DataInsertionManager.insertToUserToSession(connection, userId, sessionId);

                return true;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * A helper wrapper method to help verify the login through slack
     *
     * @param req       necessary to get session id, attribute, all information from slack response
     * @param sessionId current session id
     */
    private Boolean slackLoginVerifier(HttpServletRequest req, String sessionId) {
        // retrieve the config info from the session attribute and convert it to Config object
        Gson gson = new Gson();
        Object slackConfigObj = req.getSession().getAttribute(LoginConstants.SLACK_API_CONFIG_KEY);
        SlackConfig slackConfig = gson.fromJson((String) slackConfigObj, SlackConfig.class);

        // getting the "code" parameter from the Slack API response
        String code = req.getParameter(LoginConstants.CODE_KEY);
        // generate string url for slack authentication
        String url = LoginUtilities.generateSlackTokenURL(
                slackConfig.getClient_id(),
                slackConfig.getClient_secret(),
                code,
                slackConfig.getRedirect_url());

        // send get response to slack with the previously generated url
        String responseString = HTTPFetcher.doGet(url, null);

        // get the slack json response and put it to hashmap as key and value
        Map<String, Object> response = LoginUtilities.jsonStrToMap(responseString);

        // verifying the response from Slack API
        ClientInfo clientInfo = LoginUtilities.verifyTokenResponse(response, sessionId);

        if(clientInfo == null) {    // --- if the user is not verified
            return false;
        } else {                    // --- if the user is verified
            req.getSession().setAttribute(LoginConstants.CLIENT_INFO_KEY, new Gson().toJson(clientInfo));
            return true;
        }
    }

    /**
     * A helper method to generate the url to initiate an authentication request through slack
     *
     * @param model     a holder for model attributes and is primarily designed for adding attributes to the model
     * @param req       servletRequest contains: session id, attribute, and other information from slack response
     * @param sessionId current session id
     */
    private void slackLoginPreparer(Model model, HttpServletRequest req, String sessionId) {
        // to be passed to slackAPI for oauth
        String state = sessionId;
        String nonce = LoginUtilities.generateNonce(state);

        // adding client_key, secret_key and redirect uri information to the session attribute
        // it can be shared across different controller
        // store it as json formatted string (GSON -> json)
        SlackConfig config = NoStayHomeAppServer.readSlackAuthConfig();
        req.getSession().setAttribute(LoginConstants.SLACK_API_CONFIG_KEY, new Gson().toJson(config));

        // Generate url to send a request to SlackApi
        String url = LoginUtilities.generateSlackAuthorizeURL(config.getClient_id(),
                state,
                nonce,
                config.getRedirect_url());

        // adding the url to model so it can be read by thymeleaf html
        model.addAttribute("url", url);

        // if the user try to visit other page without logging in isFail = 1, otherwise isFail = Null
        model.addAttribute("isFail", req.getSession().getAttribute(LoginConstants.IS_FAIL_TO_LOGIN));
        // reset to false
        req.getSession().setAttribute(LoginConstants.IS_FAIL_TO_LOGIN, "0");
    }

    private ClientInfo getClientInfoDatabase(String userId) {
        ClientInfo clientInfo = new ClientInfo();

        try (Connection connection = DBCPDataSource.getConnection()){
            clientInfo = DataFetcherManager.getClientInfo(connection, userId, null);
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return clientInfo;
    }

}
