package boyko.alex.easy_way.frontend.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.facebook.AccessToken;

import boyko.alex.easy_way.backend.SharedPreferencesStorage;

import static boyko.alex.easy_way.backend.ConfigLoginParser.BUNDLE_KEY_TOKEN;
import static boyko.alex.easy_way.backend.ConfigLoginParser.BUNDLE_KEY_USERID;

/**
 * Created by PNazar on 30.05.2017.
 * <p>
 * Localised model (MVP) for ic_splash_logo screen. Interfaces the presenter with app backend. Follows a singleton pattern, so is not regenerated on config
 * changes. Does not cache data locally as there is no need to do that in here.
 */

class SplashModel {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private static SplashModel instanceOfSelf;
    private SplashPresenter splashPresenter;

    private SplashModel(SplashPresenter instantiatingPresenter) {
        splashPresenter = instantiatingPresenter;
    }

    static SplashModel getInstance(SplashPresenter instantiatingPresenter) {
        if (instanceOfSelf == null) {
            instanceOfSelf = new SplashModel(instantiatingPresenter);
        }
        return instanceOfSelf;
    }

    //Check if an account is logged in, returns boolean
    boolean checkAccountLogin() {

        return AccessToken.getCurrentAccessToken() != null;

//        Bundle credentialsBundle = SharedPreferencesStorage.getInstance().readUserCredentials();
//        return (credentialsBundle.getString(BUNDLE_KEY_USERID) != null) && (credentialsBundle.getString(BUNDLE_KEY_TOKEN) != null);
    }

    //TODO: Add login validity check with server to facilitate token/account invalidation

    void addAccountInformation(Bundle loginData) {
        String userId = loginData.getString(BUNDLE_KEY_USERID);
        String userToken = loginData.getString(BUNDLE_KEY_TOKEN);
        SharedPreferencesStorage.getInstance().writeUserCredentials(userId, userToken);
        //TODO: Consider overwriting variables with junk for increased security?
    }
}
