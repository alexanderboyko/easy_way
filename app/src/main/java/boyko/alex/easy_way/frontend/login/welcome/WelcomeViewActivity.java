package boyko.alex.easy_way.frontend.login.welcome;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.frontend.login.LoginHelper;
import boyko.alex.easy_way.frontend.login.sign_in.SignInViewActivity;
import boyko.alex.easy_way.frontend.login.sign_up.step1.SignUpStep1ViewActivity;
import boyko.alex.easy_way.frontend.login.sign_up.step2.SignUpStep2ViewActivity;
import boyko.alex.easy_way.frontend.login.terms.TermsViewActivity;
import boyko.alex.easy_way.frontend.splash.SplashViewActivity;

/**
 * Created by Sasha on 02.11.2017.
 * <p>
 * This is activity, where user can choose login method.
 */

public class WelcomeViewActivity extends AppCompatActivity {
    private final String LOG_TAG = getClass().getSimpleName();

    private View loggingBackground;
    private ProgressBar progressBar;

    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth.AuthStateListener authStateListener;
    private int signInWith = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_welcome);

        callbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
        firebaseAuth = FirebaseAuth.getInstance();

        init();

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.REQUEST_CODE_SIGN_IN) {
            try {
                handleGoogleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class));
            } catch (ApiException e) {
                showSignInError(getString(R.string.error_google_sign_in_failed));
                showLoggingProgress(false);
            }
        }
    }

    private void init() {
        // callbackManager = CallbackManager.Factory.create();

        loggingBackground = findViewById(R.id.welcome_logging_background);
        progressBar = findViewById(R.id.welcome_logging_progress_bar);

        initTermsView();
        initButtons();
    }

    private void initButtons() {
        Button signIn = findViewById(R.id.welcome_sign_in);
        Button signUp = findViewById(R.id.welcome_sign_up);
        Button facebookLogin = findViewById(R.id.welcome_facebook_button);
        Button googleLogin = findViewById(R.id.welcome_google_button);

        final LoginButton facebookSignInButton = findViewById(R.id.welcome_facebook_sign_in_button);
        final SignInButton googleSignInButton = findViewById(R.id.welcome_google_sign_in_button);

        Drawable leftDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_facebook);
        facebookLogin.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);

        Drawable leftDrawable1 = AppCompatResources.getDrawable(this, R.drawable.ic_google);
        googleLogin.setCompoundDrawablesWithIntrinsicBounds(leftDrawable1, null, null, null);


        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignInButton.performClick();
                launchGoogleSignInActivity();
                showLoggingProgress(true);
            }
        });

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
                facebookSignInButton.performClick();
                showLoggingProgress(true);
            }
        });
        facebookSignInButton.setReadPermissions("email", "public_profile");
        facebookSignInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                handleFacebookSignInResult(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                showLoggingProgress(false);
            }

            @Override
            public void onError(FacebookException error) {
                if (error.getMessage() != null) showSignInError(error.getMessage());
                error.printStackTrace();
                showLoggingProgress(false);
            }
        });
    }

    private void initTermsView() {
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

    private void handleGoogleSignInResult(final GoogleSignInAccount googleSignInAccount) {
        final User user = ConvertHelper.convertGoogleResponseToUser(googleSignInAccount);
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //if signed in
                            if (firebaseAuth.getCurrentUser() != null) {
                                //if firebase user email not null
                                if (firebaseAuth.getCurrentUser().getEmail() != null) {
                                    LoginHelper.setCurrentUserEmail(firebaseAuth.getCurrentUser().getEmail());
                                    checkIfFirstTimeSignIn(user);
                                } else {
                                    //if google client email not null
                                    if (googleSignInAccount.getEmail() != null) {
                                        LoginHelper.setCurrentUserEmail(googleSignInAccount.getEmail());
                                        checkIfFirstTimeSignIn(user);
                                    } else {
                                        //no email - show error
                                        showSignInError(getString(R.string.error_google_sign_in_failed));
                                        signOut();
                                        showLoggingProgress(false);
                                    }
                                }
                            } else {
                                showSignInError(getString(R.string.error_google_sign_in_failed));
                                showLoggingProgress(false);
                            }
                        } else {
                            showSignInError(getString(R.string.error_google_sign_in_failed));
                            showLoggingProgress(false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showSignInError(getString(R.string.error_google_sign_in_failed));
                        showLoggingProgress(false);
                    }
                });
    }

    private void handleFacebookSignInResult(final AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, final GraphResponse response) {
                                    if (firebaseAuth.getCurrentUser() != null) {
                                        try {
                                            User user = ConvertHelper.convertFacebookJSONToUser(object);

                                            if (object != null && object.getString("email") != null) {
                                                LoginHelper.setCurrentUserEmail(object.getString("email"));
                                                checkIfFirstTimeSignIn(user);
                                            } else {
                                                if (firebaseAuth.getCurrentUser().getEmail() != null) {
                                                    LoginHelper.setCurrentUserEmail(firebaseAuth.getCurrentUser().getEmail());
                                                    checkIfFirstTimeSignIn(user);
                                                } else {
                                                    showSignInError(getString(R.string.eror_login_facebook_no_email));
                                                    signOut();
                                                    showLoggingProgress(false);
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            showLoggingProgress(false);
                                        }
                                    } else {
                                        signOut();
                                        showLoggingProgress(false);
                                    }

                                }
                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,first_name,last_name,gender,birthday,location,email,picture.type(large)");
                            graphRequest.setParameters(parameters);
                            graphRequest.executeAsync();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                showSignInError(e.getReason());
                            } catch (FirebaseAuthInvalidCredentialsException | FirebaseAuthUserCollisionException e) {
                                showSignInError(e.getMessage());
                            } catch (Exception e) {
                                //Log.e(LOG_TAG, e.getMessage());
                            }
                            // If sign in fails, display a message to the user.
                            if (task.getException() != null && task.getException().getMessage() != null)
                                showSignInError(task.getException().getMessage());
                            showLoggingProgress(false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showSignInError(getString(R.string.eror_login_facebook_no_email));
                        showLoggingProgress(false);
                    }
                });
    }

    private void checkIfFirstTimeSignIn(final User user) {
        String email = LoginHelper.getCurrentUserEmail();

        if (firebaseAuth.getCurrentUser() != null && email != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getDocuments().isEmpty()) {
                                    launchSignUpStep2Activity(user);
                                } else {
                                    launchSplashActivity();
                                }
                                showLoggingProgress(false);
                            } else {
                                if (task.getException() != null && task.getException().getMessage() != null)
                                    showSignInError(task.getException().getMessage());
                                showLoggingProgress(false);
                            }
                        }
                    });
        } else {
            showSignInError(getString(R.string.error_message));
            signOut();
            showLoggingProgress(false);
        }
    }

    void showLoggingProgress(boolean isLogging) {
        if (isLogging) {
            loggingBackground.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            loggingBackground.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    void showSignInError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    void signOut(){
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        AuthUI.getInstance().signOut(WelcomeViewActivity.this);
    }

    void launchSignInActivity() {
        Intent intent = new Intent(this, SignInViewActivity.class);
        startActivity(intent);
    }

    void launchSignUpStep1Activity() {
        Intent intent = new Intent(this, SignUpStep1ViewActivity.class);
        startActivity(intent);
    }

    void launchSignUpStep2Activity(User user) {
        Intent intent = new Intent(this, SignUpStep2ViewActivity.class);
        intent.putExtra("user", Parcels.wrap(user));
        startActivity(intent);
    }

    void launchTermsActivity() {
        Intent intent = new Intent(this, TermsViewActivity.class);
        startActivity(intent);
    }

    void launchSplashActivity() {
        Intent intent = new Intent(this, SplashViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void launchGoogleSignInActivity() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RequestCodes.REQUEST_CODE_SIGN_IN);
    }
}
