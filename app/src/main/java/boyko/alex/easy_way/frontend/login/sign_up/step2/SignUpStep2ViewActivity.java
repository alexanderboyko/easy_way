package boyko.alex.easy_way.frontend.login.sign_up.step2;

import android.app.DatePickerDialog;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.frontend.login.LoginHelper;
import boyko.alex.easy_way.frontend.splash.SplashViewActivity;
import boyko.alex.easy_way.libraries.DateHelper;

/**
 * Created by Sasha on 03.11.2017.
 * <p>
 * This is second step of sign in activity
 */

public class SignUpStep2ViewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputEditText nameEditText, surnameEditText, birthdayEditText, genderEditText, aboutEditText, addressEditText;
    private TextInputLayout nameInputLayout, surnameInputLayout, birthdayInputLayout, addressInputLayout;
    private Button signUp;

    private long birthdayTime = DateHelper.getTodayTime();
    private int gender = 1;
    private Address address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_sign_up_step2);

        init();

        SignUpStep2Presenter.getInstance(this).onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SignUpStep2Presenter.getInstance(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        AuthUI.getInstance().signOut(SignUpStep2ViewActivity.this);
        super.onBackPressed();
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
    protected void onSaveInstanceState(Bundle outState) {
        SignUpStep2Presenter.getInstance(this).onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private void init() {
        initViews();
        initInputFields();
        initToolbar();
    }

    private void initViews() {
        toolbar = findViewById(R.id.sign_up_toolbar);
        nameEditText = findViewById(R.id.sign_up_name);
        surnameEditText = findViewById(R.id.sign_up_surname);
        birthdayEditText = findViewById(R.id.sign_up_birthday);
        genderEditText = findViewById(R.id.sign_up_gender);
        aboutEditText = findViewById(R.id.sign_up_about);
        signUp = findViewById(R.id.sign_up_button);
        nameInputLayout = findViewById(R.id.sign_up_name_layout);
        surnameInputLayout = findViewById(R.id.sign_up_surname_layout);
        birthdayInputLayout = findViewById(R.id.sign_up_birthday_layout);
        addressEditText = findViewById(R.id.sign_up_address);
        addressInputLayout = findViewById(R.id.sign_up_address_layout);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initInputFields() {
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nameInputLayout.setError(null);
            }
        });
        surnameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                surnameInputLayout.setError(null);
            }
        });
        birthdayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                birthdayInputLayout.setError(null);
            }
        });
        addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                addressInputLayout.setError(null);
            }
        });

        addressEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpStep2Presenter.getInstance(SignUpStep2ViewActivity.this).onAddressClicked();
            }
        });

        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpStep2Presenter.getInstance(SignUpStep2ViewActivity.this).onBirthdayClicked(birthdayTime);
            }
        });

        genderEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpStep2Presenter.getInstance(SignUpStep2ViewActivity.this).onGenderClicked(genderEditText);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpStep2Presenter.getInstance(SignUpStep2ViewActivity.this).onSignUpClicked();
            }
        });
    }

    void setName(@NonNull String name) {
        this.nameEditText.setText(name);
    }

    void setSurname(@NonNull String surname) {
        surnameEditText.setText(surname);
    }

    void setGender(String genderName, int gender) {
        this.gender = gender;
        genderEditText.setText(genderName);
    }

    void setBirthday(String birthdayFormatted, long birthdayTime) {
        this.birthdayTime = birthdayTime;
        birthdayEditText.setText(birthdayFormatted);
    }

    void setAddress(Address address) {
        this.address = address;
        addressEditText.setText(address.fullName);
    }

    String getAddress() {
        return addressEditText.getText().toString();
    }

    String getName() {
        return nameEditText.getText().toString();
    }

    String getSurname() {
        return surnameEditText.getText().toString();
    }

    long getBirthdayTime() {
        return birthdayTime;
    }

    String getBirthday() {
        return birthdayEditText.getText().toString();
    }

    int getGender() {
        return gender;
    }

    void showNameError(String error) {
        nameInputLayout.setError(error);
    }

    void showSurnameError(String error) {
        surnameInputLayout.setError(error);
    }

    void showBirthdayError(String error) {
        birthdayInputLayout.setError(error);
    }

    void showAddressError(String error) {
        addressInputLayout.setError(error);
    }

    void showToastError(String string) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }

    void launchBirthdayPicker(int year, int month, int day) {
        final DatePickerDialog picker = new DatePickerDialog(SignUpStep2ViewActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        SignUpStep2Presenter.getInstance(SignUpStep2ViewActivity.this).onBirthdaySelected(year, monthOfYear, dayOfMonth);
                    }
                }, year, month, day);
        picker.show();
    }

    void launchSelectAddressActivity() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .setCountry("PL")
                    .build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(SignUpStep2ViewActivity.this);
            startActivityForResult(intent, RequestCodes.REQUEST_CODE_ADDRESS);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            showToastError(e.getMessage());
        }
    }

    void createUser() {
        User user = new User();
        user.name = nameEditText.getText().toString();
        user.surname = surnameEditText.getText().toString();
        user.gender = gender;
        user.birthday = birthdayTime;
        user.address = address;
        user.email = getIntent().getStringExtra("email");
        if(user.email == null){
            if(LoginHelper.getCurrentUserEmail() != null) user.email = LoginHelper.getCurrentUserEmail();
        }

        if (!aboutEditText.getText().toString().isEmpty())
            user.about = aboutEditText.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .add(user)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            finishSignUpAndStart();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("SIGNUP", e.getMessage());
                    }
                });
    }

    private void finishSignUpAndStart() {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            String email = getIntent().getStringExtra("email");
            String password = getIntent().getStringExtra("password");

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("SIGNUP", "createUserWithEmail:success" + firebaseAuth.getCurrentUser().getEmail());
                                launchSplashActivity();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("SIGNUP", "createUserWithEmail:failure", task.getException());
                                showToastError(getString(R.string.error_message));
                            }
                        }
                    });
        }else{
            launchSplashActivity();
        }
    }

    void launchSplashActivity(){
        Intent intent = new Intent(this, SplashViewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
