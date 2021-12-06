package cs601.project4.server;

/**
 * A helper class to maintain constants used for the LoginServer example.
 */
public class LoginServerConstants {

    // constants necessary to generate url to send to slack to request for oauth
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


    // constants to act as a key to be stored in our server session attribute
    public static final String SLACK_API_CONFIG_KEY = "config_key";
    public static final String CLIENT_INFO_KEY = "client_info_key";
    public static final String IS_FAIL_TO_LOGIN = "fail_to_login";


    // slack sign in image button
    public static final String BUTTON_URL = "https://platform.slack-edge.com/img/sign_in_with_slack@2x.png";


    // list of constants that matches with slack response json body
    public static final String NAME_KEY = "name";
    public static final String EMAIL = "email";
    public static final String USER_ID = "https://slack.com/user_id";
    public static final String TEAM_ID = "https://slack.com/team_id";
}
