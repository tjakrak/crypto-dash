//package cs601.project4.servlets;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.eclipse.jetty.http.HttpStatus;
//import cs601.project4.utilities.ClientInfo;
//import cs601.project4.utilities.Config;
//import cs601.project4.utilities.HTTPFetcher;
//import cs601.project4.utilities.LoginUtilities;
//
//import java.io.IOException;
//import java.util.Map;
//
///**
// * Implements logic for the /login path where Slack will redirect requests after
// * the user has entered their auth info.
// */
//public class LoginServlet extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//
//        // retrieve the ID of this session
//        String sessionId = req.getSession(true).getId();
//
//        // determine whether the user is already authenticated
//        Object clientInfoObj = req.getSession().getAttribute(cs601.project4.server.LoginServerConstants.CLIENT_INFO_KEY);
//        if(clientInfoObj != null) {
//            // already authed, no need to log in
//            resp.getWriter().println(cs601.project4.server.LoginServerConstants.PAGE_HEADER);
//            resp.getWriter().println("<h1>You have already been authenticated</h1>");
//            resp.getWriter().println(cs601.project4.server.LoginServerConstants.PAGE_FOOTER);
//            return;
//        }
//
//        // retrieve the config info from the context
//        Config config = (Config) req.getServletContext().getAttribute(cs601.project4.server.LoginServerConstants.CONFIG_KEY);
//
//        // retrieve the code provided by Slack
//        String code = req.getParameter(cs601.project4.server.LoginServerConstants.CODE_KEY);
//
//        // generate the url to use to exchange the code for a token:
//        // After the user successfully grants your app permission to access their Slack profile,
//        // they'll be redirected back to your service along with the typical code that signifies
//        // a temporary access code. Exchange that code for a real access token using the
//        // /openid.connect.token method.
//        String url = LoginUtilities.generateSlackTokenURL(config.getClient_id(), config.getClient_secret(), code, config.getRedirect_url());
//
//        // Make the request to the token API
//        // https://slack.com/api/openid.connect.token?client_id=<client_id>&client_secret=<client_secret>&
//        // code=<code>&redirect_uri=<redirect_uri>
//        String responseString = HTTPFetcher.doGet(url, null);
//        Map<String, Object> response = LoginUtilities.jsonStrToMap(responseString);
//
//        ClientInfo clientInfo = LoginUtilities.verifyTokenResponse(response, sessionId);
//
//        if(clientInfo == null) {
//            resp.setStatus(HttpStatus.OK_200);
//            resp.getWriter().println(cs601.project4.server.LoginServerConstants.PAGE_HEADER);
//            resp.getWriter().println("<h1>Oops, login unsuccessful</h1>");
//            resp.getWriter().println(cs601.project4.server.LoginServerConstants.PAGE_FOOTER);
//        } else {
//            req.getSession().setAttribute(cs601.project4.server.LoginServerConstants.CLIENT_INFO_KEY, clientInfo);
//            resp.setStatus(HttpStatus.OK_200);
//            resp.getWriter().println(cs601.project4.server.LoginServerConstants.PAGE_HEADER);
//            resp.getWriter().println("<h1>Hello, " + clientInfo.getName() + "</h1>");
//            resp.getWriter().println("<p><a href=\"/logout\">Signout</a>");
//            resp.getWriter().println(cs601.project4.server.LoginServerConstants.PAGE_FOOTER);
//
//        }
//    }
//}
