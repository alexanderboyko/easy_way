package boyko.alex.easy_way.backend;

/**
 * Created by PNazar on 26.05.2017.
 *
 * Configuration for LoginJSONParser class, mostly containing field definitions for digesting JSON.
 */

public class ConfigLoginParser {

    //Fields (reading)
    public static final String FIELD_SUCCESS_TAG = "success";
    public static final String FIELD_TOKEN_TAG = "AccessToken";
    public static final String FIELD_USER_ID = "Id";

    //Objects (nesting)
    public static final String OBJECT_DATA_TAG = "data";    //Contains the token and User object
    public static final String OBJECT_USER_TAG = "User";    //Contains Id of currently logged in user

    //Return bundle key values
    public static final String BUNDLE_KEY_SUCCESS = "ENTIS_LOGIN_SUCCESSFUL";
    public static final String BUNDLE_KEY_TOKEN = "ENTIS_AUTH_TOKEN";
    public static final String BUNDLE_KEY_USERID = "ENTIS_AUTH_USERID";
}
