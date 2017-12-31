package boyko.alex.easy_way.frontend.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.frontend.login.welcome.WelcomeViewActivity;
import boyko.alex.easy_way.frontend.profile.edit.EditProfileViewActivity;

/**
 * Created by Sasha on 07.12.2017.
 * <p>
 * This is the settings activity.
 */

public class SettingsActivityView extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView editProfile, signOut;

    private boolean isProfileEdited = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_settings);

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
        if(requestCode == RequestCodes.REQUEST_CODE_EDIT){
            if(resultCode == RequestCodes.RESULT_CODE_PROFILE_EDITED){
                isProfileEdited = true;
            }
        }
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

    @Override
    public void onBackPressed() {
        if(isProfileEdited) setResult(RequestCodes.RESULT_CODE_PROFILE_EDITED);
        super.onBackPressed();
    }

    private void init() {
        initViews();
        initToolbar();
        initButtons();
    }

    private void initViews() {
        toolbar = findViewById(R.id.settings_toolbar);
        signOut = findViewById(R.id.settings_sign_out);
        editProfile = findViewById(R.id.settings_edit_profile);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.settings));
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initButtons() {
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                AuthUI.getInstance()
                        .signOut(SettingsActivityView.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                launchWelcomeActivity();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchEditProfileActivity();
            }
        });
    }

    private void launchWelcomeActivity() {
        Intent intent = new Intent(SettingsActivityView.this, WelcomeViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void launchEditProfileActivity() {
        Intent intent = new Intent(this, EditProfileViewActivity.class);
        startActivityForResult(intent, RequestCodes.REQUEST_CODE_EDIT);
    }
}
