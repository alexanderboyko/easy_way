package boyko.alex.easy_way.frontend.login.sign_up.step1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import boyko.alex.easy_way.R;
import boyko.alex.easy_way.frontend.login.sign_up.step2.SignUpStep2ViewActivity;

/**
 * Created by Sasha on 03.11.2017.
 */

public class SignUpStep1ViewActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButton;
    private TextInputEditText email, password, passwordRepeat;
    private TextInputLayout emailLayout, passwordLayout, passwordRepeatLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_sign_up_step1);

        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        initViews();
        initFab();
        initToolbar();
        initInputs();
    }

    private void initViews() {
        toolbar = findViewById(R.id.sign_up_toolbar);
        floatingActionButton = findViewById(R.id.sign_up_fab);
        email = findViewById(R.id.sign_up_email);
        password = findViewById(R.id.sign_up_password);
        passwordRepeat = findViewById(R.id.sign_up_repeat_password);
        passwordLayout = findViewById(R.id.sign_up_password_layout);
        passwordRepeatLayout = findViewById(R.id.sign_up_password_repeat_layout);
        emailLayout = findViewById(R.id.sign_up_email_layout);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initInputs() {
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailLayout.setError(null);
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
                passwordLayout.setError(null);
            }
        });
        passwordRepeat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwordRepeatLayout.setError(null);
            }
        });
    }

    private void initFab() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpStep1Presenter.getInstance(SignUpStep1ViewActivity.this).onFabClicked(
                        email.getText().toString(),
                        password.getText().toString(),
                        passwordRepeat.getText().toString());
            }
        });
    }

    void showEmailError(String error) {
        emailLayout.setError(error);
    }

    void showPasswordError(String error) {
        passwordLayout.setError(error);
    }

    void showPasswordRepeatError(String error) {
        passwordRepeatLayout.setError(error);
    }

    void launchStep2Activity() {
        Intent intent = new Intent(this, SignUpStep2ViewActivity.class);
        intent.putExtra("email", email.getText().toString());
        intent.putExtra("password", password.getText().toString());
        startActivity(intent);
    }

}
