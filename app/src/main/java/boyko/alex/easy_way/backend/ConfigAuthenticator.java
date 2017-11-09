package boyko.alex.easy_way.backend;


import static boyko.alex.easy_way.ApplicationController.APP_TEST_MODE;

/**
 * Created by PNazar on 24.05.2017.
 *
 * Class contains static, build-time configuration for the authenticator, making it easy to adjust the project should necessity arise. By design
 * it does not perform any active logic operations.
 */

public class ConfigAuthenticator {

    //Logging separate from test config to allow debugging in production environment
    public static final boolean AUTH_LOGGING_ENABLED = true;

    //Request code
    public static final int LOGIN_REQUEST_CODE = 69;

    public static final String AUTH_LOG_TAG = "Entis Authenticator";

    //Server links (complete)
    private static final String AUTH_SERVER_TEST = "https://api-entis.deployflex.com/system/v1/user/login";
    private static final String AUTH_SERVER_MAIN = "https://api-entis.io/system/v1/user/login";

    //Autoselector for login URL
    public static String getLoginUrl(){
        if(APP_TEST_MODE){
            return AUTH_SERVER_TEST;
        } else {
            return AUTH_SERVER_MAIN;
        }
    }
}
