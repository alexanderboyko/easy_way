package boyko.alex.easy_way.frontend.login.sign_up.step1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import boyko.alex.easy_way.frontend.login.sign_up.step2.SignUpStep2ViewActivity;
import boyko.alex.easy_way.R;

/**
 * Created by Sasha on 03.11.2017.
 */

public class SignUpStep1ViewActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButton;
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
    }

    private void initViews() {
        toolbar = findViewById(R.id.sign_up_toolbar);
        floatingActionButton = findViewById(R.id.sign_up_fab);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initFab() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpStep1Presenter.getInstance(SignUpStep1ViewActivity.this).onFabClicked();
            }
        });
    }

    void launchStep2Activity() {
        Intent intent = new Intent(this, SignUpStep2ViewActivity.class);
        //todo send inputted values
        startActivity(intent);
    }
}
