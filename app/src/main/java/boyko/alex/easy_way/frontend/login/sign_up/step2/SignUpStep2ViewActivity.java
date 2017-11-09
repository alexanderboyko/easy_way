package boyko.alex.easy_way.frontend.login.sign_up.step2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import boyko.alex.rentit.R;

/**
 * Created by Sasha on 03.11.2017.
 */

public class SignUpStep2ViewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputEditText name, surname, birthday, sex, about;
    private Button signUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
        setContentView(R.layout.activity_sign_up_step2);

        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
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
        initInputFields();
        initToolbar();
    }

    private void initViews() {
        toolbar = findViewById(R.id.sign_up_toolbar);
        name = findViewById(R.id.sign_up_name);
        surname = findViewById(R.id.sign_up_surname);
        birthday = findViewById(R.id.sign_up_birthday);
        sex = findViewById(R.id.sign_up_sex);
        about = findViewById(R.id.sign_up_about);
        signUp = findViewById(R.id.sign_up_button);
    }

    private void initToolbar(){
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initInputFields() {
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpStep2Presenter.getInstance(SignUpStep2ViewActivity.this).onBirthdayChange();
            }
        });

        sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpStep2Presenter.getInstance(SignUpStep2ViewActivity.this).onSexChange();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpStep2Presenter.getInstance(SignUpStep2ViewActivity.this).onSignUpClicked();
            }
        });
    }
}
