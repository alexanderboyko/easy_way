package boyko.alex.easy_way.frontend.login.welcome;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import boyko.alex.easy_way.frontend.login.sign_in.SignInViewActivity;
import boyko.alex.easy_way.frontend.login.sign_up.step1.SignUpStep1ViewActivity;
import boyko.alex.easy_way.frontend.login.terms.TermsViewActivity;
import boyko.alex.rentit.R;

/**
 * Created by Sasha on 02.11.2017.
 */

public class WelcomeViewActivity extends AppCompatActivity {
    private final String LOG_TAG = getClass().getSimpleName();

    private CallbackManager callbackManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
        setContentView(R.layout.activity_welcome);

        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void init(){
        callbackManager = CallbackManager.Factory.create();

        initTermsView();
        initButtons();
    }

    private void initButtons(){
        Button signIn = findViewById(R.id.welcome_sign_in);
        Button signUp = findViewById(R.id.welcome_sign_up);
        Button facebookLogin = findViewById(R.id.welcome_facebook_button);
        Button googleLogin = findViewById(R.id.welcome_google_button);

        final LoginButton facebookLoginButton = findViewById(R.id.welcome_facebook_login_button);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSignInActivity();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSignUpStep1Activity();
            }
        });
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLoginButton.performClick();
                WelcomePresenter.getInstance(WelcomeViewActivity.this).onFacebookLoginClicked();
            }
        });
        facebookLoginButton.setReadPermissions("email","user_birthday","user_location","public_profile");
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(LOG_TAG, "SUCCESS " + loginResult.toString());
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d(LOG_TAG, "" + response.getJSONObject().toString());
                        Log.d(LOG_TAG, "" + object.toString());

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,gender,birthday,location,email,picture.type(large)");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.i(LOG_TAG, "CANCEL");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.i(LOG_TAG, exception.toString());
            }
        });
    }

    private void initTermsView(){
        SpannableString ss = new SpannableString(getString(R.string.by_tapping_one_of_below_login_options_you_accept));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                WelcomePresenter.getInstance(WelcomeViewActivity.this).onTermsClicked();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(Color.WHITE);
            }
        };
        ss.setSpan(clickableSpan, 50, 64, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView textView = findViewById(R.id.welcome_terms);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    void launchSignInActivity(){
        Intent intent = new Intent(this, SignInViewActivity.class);
        startActivity(intent);
    }

    void launchSignUpStep1Activity(){
        Intent intent = new Intent(this, SignUpStep1ViewActivity.class);
        startActivity(intent);
    }

    void launchTermsActivity(){
        Intent intent = new Intent(this, TermsViewActivity.class);
        startActivity(intent);
    }
}
