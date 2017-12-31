package boyko.alex.easy_way.frontend.profile.edit;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.net.MalformedURLException;
import java.net.URL;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Address;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sasha on 10.11.2017.
 *
 * Profile edit activity
 */

public class EditProfileViewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private LinearLayout noPhotoLayout, uploadingLayout;
    private TextView uploadingMessage;
    private CircleImageView photo;
    private TextInputEditText nameEditText, surnameEditText, birthdayEditText, genderEditText, aboutEditText, addressEditText;
    private TextInputLayout nameInputLayout, surnameInputLayout, birthdayInputLayout, addressInputLayout;
    private Button saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_profile_edit);

        init();

        EditProfilePresenter.getInstance(this).onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EditProfilePresenter.getInstance(this).onActivityResult(requestCode, resultCode, data);
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


    @Override
    public void onBackPressed() {
        if(EditProfilePresenter.getInstance(this).onBackPressed()) super.onBackPressed();
    }

    private void init() {
        initViews();
        initInputFields();
        initToolbar();
    }

    private void initViews() {
        toolbar = findViewById(R.id.profile_edit_toolbar);
        uploadingLayout = findViewById(R.id.profile_edit_uploading_layout);
        uploadingMessage = findViewById(R.id.profile_edit_uploading_message);
        noPhotoLayout = findViewById(R.id.profile_edit_no_photo_layout);
        photo = findViewById(R.id.profile_edit_photo);
        nameEditText = findViewById(R.id.profile_edit_name);
        surnameEditText = findViewById(R.id.profile_edit_surname);
        birthdayEditText = findViewById(R.id.profile_edit_birthday);
        genderEditText = findViewById(R.id.profile_edit_gender);
        aboutEditText = findViewById(R.id.profile_edit_about);
        saveButton = findViewById(R.id.profile_edit_button);
        nameInputLayout = findViewById(R.id.profile_edit_name_layout);
        surnameInputLayout = findViewById(R.id.profile_edit_surname_layout);
        birthdayInputLayout = findViewById(R.id.profile_edit_birthday_layout);
        addressEditText = findViewById(R.id.profile_edit_address);
        addressInputLayout = findViewById(R.id.profile_edit_address_layout);
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
                EditProfilePresenter.getInstance(EditProfileViewActivity.this).onAddressClicked();
            }
        });

        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfilePresenter.getInstance(EditProfileViewActivity.this).onBirthdayClicked();
            }
        });

        genderEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfilePresenter.getInstance(EditProfileViewActivity.this).onGenderClicked(genderEditText);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfilePresenter.getInstance(EditProfileViewActivity.this).onSaveClicked();
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchPickPhotoDialog();
            }
        };
        noPhotoLayout.setOnClickListener(onClickListener);
        photo.setOnClickListener(onClickListener);
    }

    String getName() {
        return nameEditText.getText().toString().isEmpty() ? null : nameEditText.getText().toString();
    }

    String getSurname() {
        return surnameEditText.getText().toString().isEmpty() ? null : surnameEditText.getText().toString();
    }

    String getAbout() {
        return aboutEditText.getText().toString().isEmpty() ? null : aboutEditText.getText().toString();
    }

    void setName(@NonNull String name) {
        this.nameEditText.setText(name);
    }

    void setSurname(@NonNull String surname) {
        surnameEditText.setText(surname);
    }

    void setAbout(String text){
        aboutEditText.setText(text);
    }

    void setGender(String genderName) {
        genderEditText.setText(genderName);
    }

    void setBirthday(String birthdayFormatted) {
        birthdayEditText.setText(birthdayFormatted);
    }

    void setPhoto(String photo){
        if(photo == null){
            this.photo.setVisibility(View.GONE);
            noPhotoLayout.setVisibility(View.VISIBLE);
        }else {
            this.photo.setVisibility(View.VISIBLE);
            noPhotoLayout.setVisibility(View.GONE);
            try {
                Glide.with(ApplicationController.getInstance())
                        .load(new URL(photo))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                        .apply(RequestOptions.noTransformation())
                        .into(this.photo);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    void setAddress(Address address) {
        addressEditText.setText(address.fullName);
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

    void setUploadingVisibility(int visibility){
        uploadingLayout.setVisibility(visibility);
    }

    void setUploadingMessage(@NonNull String message){
        uploadingMessage.setText(message);
    }

    void launchPickPhotoDialog() {
        PickImageDialog.build(new PickSetup()).setOnPickResult(new IPickResult() {
            @Override
            public void onPickResult(PickResult pickResult) {
                EditProfilePresenter.getInstance(EditProfileViewActivity.this).onPickResult(pickResult);
            }
        }).show(this);
    }

    void launchBirthdayPicker(int year, int month, int day) {
        final DatePickerDialog picker = new DatePickerDialog(EditProfileViewActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        EditProfilePresenter.getInstance(EditProfileViewActivity.this).onBirthdaySelected(year, monthOfYear, dayOfMonth);
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
                            .build(EditProfileViewActivity.this);
            startActivityForResult(intent, RequestCodes.REQUEST_CODE_ADDRESS);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            showToastError(e.getMessage());
        }
    }

    void launchCloseConfirmationDialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(R.string.confirmation_closing_mode_edit);
        builder1.setCancelable(true);
        builder1.setTitle(R.string.confirm_exit);

        builder1.setPositiveButton(
                R.string.exit,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        setResult(RequestCodes.RESULT_CODE_CANCELED);
                        finish();
                    }
                });

        builder1.setNegativeButton(
                R.string.dismiss,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
