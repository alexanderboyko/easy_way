package boyko.alex.easy_way.backend;

import android.content.Context;
import android.os.Bundle;

import boyko.alex.easy_way.libraries.ObscuredSharedPreferences;
import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;

/**
 * Created by PNazar on 06.06.2017.
 *
 * Class utilises ObscuredSharedPreferences to securely store keypairs (mainly token and userid but also possibly other data) in system Shared Preferences.
 * Values are encrypted using a per-device key, so they still can be compromised if the device contains a spying malware program. They are, however,
 * resistant to raw file access on rooted devices. Follows the singleton pattern
 */

public class SharedPreferencesStorage {
    private final String LOG_TAG = this.getClass().getSimpleName();

    //Instance
    private static SharedPreferencesStorage instanceOfSelf;

    //Sharedpreferences instance
    private ObscuredSharedPreferences appSharedPreferences;

    //Private constructor
    private SharedPreferencesStorage(){
        //Assemble information
        Context currentAppContext = ApplicationController.getInstance().getApplicationContext();
        String currentAppName =  ApplicationController.getInstance().getApplicationContext().getResources().getString(R.string.app_name);
        //Get handle on preferences
        appSharedPreferences = ObscuredSharedPreferences.getPrefs(currentAppContext,currentAppName,Context.MODE_PRIVATE);
    }

    //Instance access
    public static SharedPreferencesStorage getInstance(){
        if(instanceOfSelf==null) {
            instanceOfSelf = new SharedPreferencesStorage();
        }
        return instanceOfSelf;
    }

    //Write token and userid to secure sharedprefs
    public void writeUserCredentials(String userId, String userToken){
        appSharedPreferences.edit().putString(SharedPreferencesConfig.SHAREDPREFS_KEY_USERID,userId).apply();
        appSharedPreferences.edit().putString(SharedPreferencesConfig.SHAREDPREFS_KEY_USERTOKEN,userToken).apply();
    }

    /*Reads user credentials form shared preferences and returns a bundle containing them (uses keys obtained from ConfigLoginParser.java in authenticator
    package for consistency. Takes no arguments as we can only have one user logged in at a time. Returns null as values if nobody is logged in*/
    public Bundle readUserCredentials(){
        Bundle returnBundle = new Bundle();
        returnBundle.putString(ConfigLoginParser.BUNDLE_KEY_USERID,appSharedPreferences.getString(SharedPreferencesConfig.SHAREDPREFS_KEY_USERID,null));
        returnBundle.putString(ConfigLoginParser.BUNDLE_KEY_TOKEN,appSharedPreferences.getString(SharedPreferencesConfig.SHAREDPREFS_KEY_USERTOKEN,null));
        return returnBundle;
    }

    //Allows for user credential removal, takes no arguments
    public void deleteUserCredentials() {
        appSharedPreferences.edit().remove(SharedPreferencesConfig.SHAREDPREFS_KEY_USERID).apply();
        appSharedPreferences.edit().remove(SharedPreferencesConfig.SHAREDPREFS_KEY_USERTOKEN).apply();
    }
}
