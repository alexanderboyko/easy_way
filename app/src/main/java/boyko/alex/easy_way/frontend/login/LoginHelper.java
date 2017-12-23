package boyko.alex.easy_way.frontend.login;

import android.content.Context;
import android.content.SharedPreferences;

import boyko.alex.easy_way.ApplicationController;

/**
 * Created by Sasha on 12.12.2017.
 */

public class LoginHelper {
    public static String getCurrentUserEmail() {
        SharedPreferences sharedPref = ApplicationController.getInstance().getSharedPreferences("login", Context.MODE_PRIVATE);
        return sharedPref.getString("email", null);
    }

    public static void setCurrentUserEmail(String email){
        SharedPreferences sharedPref = ApplicationController.getInstance().getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", email);
        editor.apply();
    }

    public static void deleteCurrentUserEmail(){
        SharedPreferences sharedPref = ApplicationController.getInstance().getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", null);
        editor.apply();
    }
}