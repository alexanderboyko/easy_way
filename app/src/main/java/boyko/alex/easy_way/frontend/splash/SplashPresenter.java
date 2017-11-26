package boyko.alex.easy_way.frontend.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.facebook.AccessToken;

import boyko.alex.easy_way.backend.DataMediator;

/**
 * Created by PNazar on 30.05.2017.
 * <p>
 * Presenter (MVP) for the ic_splash_logo screen. Instantiates local Model (interface to backend). Follows a singleton model, therefore is not regenerated
 * when a configuration change (ie device rotation) occurs. Contains all the logic happening at the startup of the app.
 */

class SplashPresenter {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private static SplashPresenter instanceOfSelf;
    private final SplashViewActivity splashView;
    private final SplashModel splashModel;

    private SplashPresenter(SplashViewActivity instantiatingView) {
        splashModel = SplashModel.getInstance(this);
        splashView = instantiatingView;
    }

    static SplashPresenter getInstance(SplashViewActivity instantiatingView) {
        if (instanceOfSelf == null) {
            instanceOfSelf = new SplashPresenter(instantiatingView);
        }
        return instanceOfSelf;
    }

    //App initialisation
    void initApp() {
        if (!splashModel.checkAccountLogin()) {
            splashView.launchWelcomeActivity();    //Due to how Android handles activity lifecycle, it has to go through the activity
        } else {
            SplashModel.getInstance(this).startLoadingData();
        }
    }

    void loadingFinished(){
        DataMediator.printData();
        splashView.launchExploreActivity();
    }

    //Callback for login information return, synchronous, reverts automatically to init
    void handleLoginResult(Bundle loginData) {
        splashModel.addAccountInformation(loginData);
        //Immediately return to init for further initialisation
        this.initApp();
    }
}
