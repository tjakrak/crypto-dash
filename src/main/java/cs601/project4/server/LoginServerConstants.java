package cs601.project4.server;

/**
 * A helper class to maintain constants used for the LoginServer example.
 */
public class LoginServerConstants {

    public static final String PAGE_HEADER = "<!DOCTYPE html>\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "  <title>Log in with Slack</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "\n";

    public static final String PAGE_FOOTER = "\n" +
            "</body>\n" +
            "</html>";

    public static final String HOST = "slack.com";
    public static final String AUTH_PATH = "openid/connect/authorize";
    public static final String TOKEN_PATH = "api/openid.connect.token";
    public static final String RESPONSE_TYPE_KEY = "response_type";
    public static final String RESPONSE_TYPE_VALUE= "code";
    public static final String CODE_KEY= "code";
    public static final String SCOPE_KEY = "scope";
    public static final String SCOPE_VALUE = "openid%20profile%20email";
    public static final String CLIENT_ID_KEY = "client_id";
    public static final String CLIENT_SECRET_KEY = "client_secret";
    public static final String STATE_KEY = "state";
    public static final String NONCE_KEY = "nonce";
    public static final String REDIRECT_URI_KEY = "redirect_uri";
    public static final String OK_KEY = "ok";


    public static final String CONFIG_KEY = "config_key";
    public static final String CLIENT_INFO_KEY = "client_info_key";
    public static final String BUTTON_URL = "https://platform.slack-edge.com/img/sign_in_with_slack@2x.png";

    public static final String IS_AUTHED_KEY = "is_authed";
    public static final String NAME_KEY = "name";
}
