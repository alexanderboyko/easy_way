package boyko.alex.easy_way.frontend.login.sign_up.step2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.parceler.Parcels;

import java.util.Calendar;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.libraries.DateHelper;

/**
 * Created by Sasha on 03.11.2017.
 *
 * This is second step of sign up logic class.
 */

class SignUpStep2Presenter {
    private SignUpStep2ViewActivity view;
    private static SignUpStep2Presenter presenter;

    private SignUpStep2Presenter(SignUpStep2ViewActivity view) {
        this.view = view;
    }

    static SignUpStep2Presenter getInstance(SignUpStep2ViewActivity view) {
        if (presenter == null) {
            presenter = new SignUpStep2Presenter(view);
        } else {
            presenter.view = view;
        }
        return presenter;
    }


    void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            User user = Parcels.unwrap(view.getIntent().getParcelableExtra("user"));
            if(user == null){
                view.setGender(ApplicationController.getInstance().getString(R.string.male), 1);
            }else{
                if(user.name != null){
                    view.setName(user.name);
                }
                if(user.surname != null){
                    view.setSurname(user.surname);
                }
                if(user.birthday != 0){
                    view.setBirthday(DateHelper.getSmartFormattedDate(user.birthday),user.birthday);
                }
                if(user.gender == 0){
                    view.setGender(ApplicationController.getInstance().getString(R.string.female), user.gender);
                }else{
                    view.setGender(ApplicationController.getInstance().getString(R.string.male), user.gender);
                }
            }
        } else {
            long birthdayTime = savedInstanceState.getLong("birthday");
            int gender = savedInstanceState.getInt("gender");

            view.setBirthday(DateHelper.getSmartFormattedDate(birthdayTime), birthdayTime);
            if(gender == 0){
                view.setGender(ApplicationController.getInstance().getString(R.string.female), gender);
            }else{
                view.setGender(ApplicationController.getInstance().getString(R.string.male), gender);
            }
        }
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.REQUEST_CODE_ADDRESS) {
            if (resultCode == Activity.RESULT_OK) {
                onAddressSelected(ConvertHelper.convertPlaceToAddress(PlaceAutocomplete.getPlace(view, data)));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                onAddressSelectError(PlaceAutocomplete.getStatus(view, data));
            }
        }
    }

    void onSaveInstanceState(Bundle outState){
        outState.putLong("birthday", view.getBirthdayTime());
        outState.putInt("gender", view.getGender());
    }

    void onAddressClicked(){
        view.launchSelectAddressActivity();
    }

    private void onAddressSelected(@NonNull Address address) {
        view.setAddress(address);
    }

    private void onAddressSelectError(Status status) {
        if (status != null && status.getStatusMessage() != null)
            view.showToastError(status.getStatusMessage());
        else view.showToastError(ApplicationController.getInstance().getString(R.string.error_message));
    }

    void onBirthdayClicked(long currentTime) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        view.launchBirthdayPicker(year, month, day);
    }

    void onBirthdaySelected(int year, int monthOfYear, int dayOfMonth){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.YEAR, year);

        view.setBirthday(DateHelper.getSmartFormattedDate(calendar.getTimeInMillis()), calendar.getTimeInMillis());
    }

    void onGenderClicked(View view) {
        PopupMenu popup = new PopupMenu(ApplicationController.getInstance(), view);

        popup.getMenu().add(0, Menu.NONE, Menu.NONE, ApplicationController.getInstance().getString(R.string.male));
        popup.getMenu().add(0, Menu.NONE, Menu.NONE, ApplicationController.getInstance().getString(R.string.female));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onGenderSelected(item.getTitle().toString());
                return false;
            }
        });
        popup.show();
    }

    private void onGenderSelected(String string) {
        if(string.equals("Male")) {
            view.setGender(string, 1);
        }else{
            view.setGender(string, 0);
        }
    }

    void onSignUpClicked() {
        if(isValid()){
            view.createUser();
        }
    }

    private boolean isValid(){
        boolean isValid = true;

        String name = view.getName();
        String surName = view.getSurname();
        String birthday = view.getBirthday();
        String address = view.getAddress();

        if(name.isEmpty()){
            isValid = false;
            view.showNameError(ApplicationController.getInstance().getString(R.string.error_empty));
        }

        if(surName.isEmpty()){
            isValid = false;
            view.showSurnameError(ApplicationController.getInstance().getString(R.string.error_empty));
        }

        if(birthday.isEmpty()){
            isValid = false;
            view.showBirthdayError(ApplicationController.getInstance().getString(R.string.error_empty));
        }

        if(address.isEmpty()){
            isValid = false;
            view.showAddressError(ApplicationController.getInstance().getString(R.string.error_empty));
        }

        return isValid;
    }
}
