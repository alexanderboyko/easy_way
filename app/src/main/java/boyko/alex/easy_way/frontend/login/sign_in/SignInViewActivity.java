package boyko.alex.easy_way.frontend.login.sign_in;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import boyko.alex.easy_way.R;

/**
 * Created by Sasha on 29.10.2017.
 */

public class SignInViewActivity extends AppCompatActivity {
    private final String LOG_TAG = getClass().getSimpleName();

    private Toolbar toolbar;
//    private LoginButton facebookLoginButton;
//    private CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_sign_in);

        init();
//        callbackManager = CallbackManager.Factory.create();
//
//        facebookLoginButton = findViewById(R.id.login_button);
//        facebookLoginButton.setReadPermissions("email");
//        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.i(LOG_TAG, "SUCCESS " + loginResult.toString());
//                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        Log.d(LOG_TAG, "" + response.getJSONObject().toString());
//
//                    }
//                });
//
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,name,first_name,last_name,gender,email,picture.type(large)");
//                graphRequest.setParameters(parameters);
//                graphRequest.executeAsync();
//            }
//
//            @Override
//            public void onCancel() {
//                Log.i(LOG_TAG, "CANCEL");
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                Log.i(LOG_TAG, exception.toString());
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_in_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.sign_in_menu_forgot_password:
                SignInPresenter.getInstance(SignInViewActivity.this).onForgotPasswordClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        initViews();
        initToolbar();
    }

    private void initViews(){
        toolbar = findViewById(R.id.login_toolbar);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }


}
