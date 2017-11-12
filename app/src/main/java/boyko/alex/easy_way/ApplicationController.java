package boyko.alex.easy_way;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import boyko.alex.easy_way.libraries.FontsOverride;

/**
 * Created by Sasha on 01.11.2017.
 */

public class ApplicationController extends Application {
    public static final boolean APP_LOGGING_ENABLED = true;     //Logging
    public static final boolean APP_TEST_MODE = true;           //Targetting test server or production

    private static ApplicationController appInstance;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        appInstance = this;
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/WorkSans-Regular.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/WorkSans-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/WorkSans-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/WorkSans-Regular.ttf");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static synchronized ApplicationController getInstance() {return appInstance;}

}
