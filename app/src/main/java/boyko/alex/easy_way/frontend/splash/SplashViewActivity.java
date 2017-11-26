package boyko.alex.easy_way.frontend.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import boyko.alex.easy_way.frontend.explore.ExploreViewActivity;
import boyko.alex.easy_way.frontend.login.welcome.WelcomeViewActivity;
import boyko.alex.easy_way.R;

/**
 * Created by Sasha on 01.11.2017.
 */

public class SplashViewActivity extends AppCompatActivity {
    private SplashPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_splash);

        //Instantiate the presenter
        presenter = SplashPresenter.getInstance(this);
        presenter.initApp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    void launchWelcomeActivity() {
        Intent intent = new Intent(this, WelcomeViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    void launchExploreActivity() {
        Intent intent = new Intent(this, ExploreViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
