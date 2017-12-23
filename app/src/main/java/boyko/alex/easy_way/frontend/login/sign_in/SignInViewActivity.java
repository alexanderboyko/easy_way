package boyko.alex.easy_way.frontend.login.sign_in;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import boyko.alex.easy_way.R;
import boyko.alex.easy_way.frontend.splash.SplashViewActivity;

/**
 * Created by Sasha on 29.10.2017.
 */

public class SignInViewActivity extends AppCompatActivity {
    private final String LOG_TAG = getClass().getSimpleName();

    private TextInputLayout emailInputLayout, passwordInputLayout;
    private TextInputEditText email, password;
    private Toolbar toolbar;
    private Button signInButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_sign_in);

        init();

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                onForgotPassword();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        initViews();
        initToolbar();
        initInputs();
    }

    private void initViews(){
        toolbar = findViewById(R.id.sign_in_toolbar);
        emailInputLayout = findViewById(R.id.sign_in_email_layout);
        passwordInputLayout = findViewById(R.id.sign_in_password_layout);
        email = findViewById(R.id.sign_in_email);
        password = findViewById(R.id.sign_in_password);
        signInButton = findViewById(R.id.sign_in_button);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initInputs(){
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailInputLayout.setError(null);
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwordInputLayout.setError(null);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()) signIn();
            }
        });
    }

    private boolean isValid(){
        boolean valid = true;

        if(email.getText().toString().isEmpty()){
            valid = false;
            emailInputLayout.setError(getString(R.string.error_empty));
        }
        if(password.getText().toString().isEmpty()){
            valid = false;
            passwordInputLayout.setError(getString(R.string.error_empty));
        }

        return valid;
    }

    private void onForgotPassword(){
        //todo
    }

    private void signIn(){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            launchSplashActivity();
                        } else {
                            Toast.makeText(SignInViewActivity.this, getString(R.string.wrong_email_or_password), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void launchSplashActivity(){
        Intent intent = new Intent(this, SplashViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
