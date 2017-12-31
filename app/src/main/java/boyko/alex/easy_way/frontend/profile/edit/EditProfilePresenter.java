package boyko.alex.easy_way.frontend.profile.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.vansuita.pickimage.bean.PickResult;

import java.util.Calendar;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.libraries.DateHelper;

/**
 * Created by Sasha on 30.12.2017.
 * <p>
 * Edit profile logic here
 */

class EditProfilePresenter {
    private EditProfileViewActivity view;
    private static EditProfilePresenter presenter;

    private EditProfilePresenter(EditProfileViewActivity view) {
        this.view = view;
    }

    private boolean isPhotoLoading = false, isUserUpdating = false;

    static EditProfilePresenter getInstance(EditProfileViewActivity view) {
        if (presenter == null) {
            presenter = new EditProfilePresenter(view);
        } else {
            presenter.view = view;
        }
        return presenter;
    }

    void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            isUserUpdating = false;
            isPhotoLoading = false;
            User user = DataMediator.getUser();
            EditProfileModel.getInstance(this).setUserBeforeEdit(user);
            EditProfileModel.getInstance(this).setUserEdited(user);

            if (user.name != null) {
                view.setName(user.name);
            }
            if (user.surname != null) {
                view.setSurname(user.surname);
            }
            if (user.birthday != 0) {
                view.setBirthday(DateHelper.getSmartFormattedDate(user.birthday));
            }
            if (user.gender == 0) {
                view.setGender(ApplicationController.getInstance().getString(R.string.female));
            } else {
                view.setGender(ApplicationController.getInstance().getString(R.string.male));
            }

            if(user.about != null && !user.about.isEmpty()) view.setAbout(user.about);
            if(user.address != null && user.address.fullName != null) view.setAddress(user.address);

            view.setPhoto(user.photo);

        } else {
            long birthdayTime = EditProfileModel.getInstance(this).getUserEdited().birthday;
            int gender = EditProfileModel.getInstance(this).getUserEdited().gender;

            view.setBirthday(DateHelper.getSmartFormattedDate(birthdayTime));
            view.setPhoto(EditProfileModel.getInstance(this).getUserEdited().photo);
            if (gender == 0) {
                view.setGender(ApplicationController.getInstance().getString(R.string.female));
            } else {
                view.setGender(ApplicationController.getInstance().getString(R.string.male));
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

    boolean onBackPressed() {
        if (isPhotoLoading || isUserUpdating) return false;
        if (ifUserDataChanged()) {
            view.launchCloseConfirmationDialog();
            return false;
        }
        return true;
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
        if (string.equals("Male")) {
            EditProfileModel.getInstance(this).getUserEdited().gender = 1;
            view.setGender(string);
        } else {
            EditProfileModel.getInstance(this).getUserEdited().gender = 0;
            view.setGender(string);
        }
    }

    void onAddressClicked() {
        view.launchSelectAddressActivity();
    }

    private void onAddressSelected(@NonNull Address address) {
        EditProfileModel.getInstance(this).getUserEdited().address = address;
        view.setAddress(address);
    }

    private void onAddressSelectError(Status status) {
        if (status != null && status.getStatusMessage() != null)
            view.showToastError(status.getStatusMessage());
        else
            view.showToastError(ApplicationController.getInstance().getString(R.string.error_message));
    }

    void onBirthdayClicked() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(EditProfileModel.getInstance(this).getUserEdited().birthday);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        view.launchBirthdayPicker(year, month, day);
    }

    void onBirthdaySelected(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.YEAR, year);

        EditProfileModel.getInstance(this).getUserEdited().birthday = calendar.getTimeInMillis();

        view.setBirthday(DateHelper.getSmartFormattedDate(calendar.getTimeInMillis()));
    }

    void onPickResult(PickResult pickResult) {
        if (pickResult.getError() == null) {
            if (pickResult.getBitmap() != null) {
                startUploadPhoto(pickResult.getBitmap());

                User beforeEdit = EditProfileModel.getInstance(this).getUserBeforeEdit();
                User edited = EditProfileModel.getInstance(this).getUserEdited();
                if (beforeEdit.photo != null && edited.photo != null && !edited.photo.equals(beforeEdit.photo)) {
                    //delete previously uploaded photo, but not from userBeforeEdit object
                    EditProfileModel.getInstance(this).deletePhoto(edited.photo);
                    EditProfileModel.getInstance(this).getUserEdited().photo = null;
                }
            } else
                view.showToastError(ApplicationController.getInstance().getString(R.string.error_message));
        } else {
            view.showToastError(ApplicationController.getInstance().getString(R.string.error_message));
        }
    }

    private void startUploadPhoto(@NonNull Bitmap bitmap) {
        isPhotoLoading = true;
        EditProfileModel.getInstance(this).uploadPhoto(bitmap);
        view.setUploadingVisibility(View.VISIBLE);
        view.setUploadingMessage(ApplicationController.getInstance().getString(R.string.uploading_photo));
    }

    void finishedUploadPhoto(String url) {
        view.setUploadingVisibility(View.GONE);
        view.setPhoto(url);
        EditProfileModel.getInstance(this).getUserEdited().photo = url;
        if (url == null) {
            view.showToastError(ApplicationController.getInstance().getString(R.string.error_message));
        }
        isPhotoLoading = false;
    }

    private void setDataToEditedUser() {
        EditProfileModel.getInstance(this).getUserEdited().name = view.getName();
        EditProfileModel.getInstance(this).getUserEdited().surname = view.getSurname();
        EditProfileModel.getInstance(this).getUserEdited().about = view.getAbout();
    }

    private boolean ifUserDataChanged() {
        setDataToEditedUser();
        User userBeforeEdit = EditProfileModel.getInstance(this).getUserBeforeEdit();
        User userEdited = EditProfileModel.getInstance(this).getUserEdited();

        if (userBeforeEdit.name != null && userEdited.name != null && !userBeforeEdit.name.equals(userEdited.name)) {
            return true;
        }

        if (userBeforeEdit.surname != null && userEdited.surname != null && !userBeforeEdit.surname.equals(userEdited.surname)) {
            return true;
        }

        if (!userBeforeEdit.address.id.equals(userEdited.address.id)) {
            return true;
        }

        if (userBeforeEdit.birthday != userEdited.birthday) {
            return true;
        }

        if (userBeforeEdit.gender != userEdited.gender) {
            return true;
        }

        if ((userBeforeEdit.about != null && userEdited.about == null)
                || (userBeforeEdit.about == null && userEdited.about != null)
                || (userBeforeEdit.about != null && !userBeforeEdit.about.equals(userEdited.about))) {
            return true;
        }

        if ((userBeforeEdit.photo != null && userEdited.photo == null)
                || (userBeforeEdit.photo == null && userEdited.photo != null)
                || (userBeforeEdit.photo != null && !userBeforeEdit.photo.equals(userEdited.photo))) {
            return true;
        }

        return false;
    }

    private boolean isValid() {
        boolean isValid = true;

        String name = view.getName();
        String surName = view.getSurname();

        if (name != null && name.isEmpty()) {
            isValid = false;
            view.showNameError(ApplicationController.getInstance().getString(R.string.error_empty));
        }

        if (surName != null && surName.isEmpty()) {
            isValid = false;
            view.showSurnameError(ApplicationController.getInstance().getString(R.string.error_empty));
        }

        return isValid;
    }

    void onSaveClicked() {
        if (isValid()) {
            isUserUpdating = true;
            setDataToEditedUser();
            view.setUploadingMessage(ApplicationController.getInstance().getString(R.string.updating_user));
            view.setUploadingVisibility(View.VISIBLE);
            EditProfileModel.getInstance(this).updateUser();
        }
    }

    void updateUserFinished(){
        view.setResult(RequestCodes.RESULT_CODE_PROFILE_EDITED);
        view.finish();
    }

    void errorUpdateUser(){
        isUserUpdating = false;
        view.setUploadingVisibility(View.GONE);
        view.showToastError(ApplicationController.getInstance().getString(R.string.error_message));
    }

}
