package boyko.alex.easy_way.frontend.profile.edit;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.libraries.DeepCopy;

/**
 * Created by Sasha on 30.12.2017.
 * <p>
 * Edit profile server uploads and data here
 */

class EditProfileModel {
    private EditProfilePresenter presenter;
    private static EditProfileModel model;

    private User userBeforeEdit;
    private User userEdited;

    private EditProfileModel(EditProfilePresenter profilePresenter) {
        presenter = profilePresenter;
    }

    static EditProfileModel getInstance(EditProfilePresenter profilePresenter) {
        if (model == null) {
            model = new EditProfileModel(profilePresenter);
        }

        return model;
    }

    void setUserBeforeEdit(User user) {
        userBeforeEdit = (User) DeepCopy.copy(user);
    }

    void setUserEdited(User user) {
        userEdited = user;
    }

    User getUserBeforeEdit() {
        return userBeforeEdit;
    }

    User getUserEdited() {
        return userEdited;
    }

    void uploadPhoto(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef
                .child("items/" + UUID.nameUUIDFromBytes(data));

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getDownloadUrl() != null) {
                        presenter.finishedUploadPhoto(task.getResult().getDownloadUrl().toString());
                    }
                } else {
                    presenter.finishedUploadPhoto(null);
                }
            }
        });
    }

    void deletePhoto(String url) {
        try {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference desertRef = storage.getReferenceFromUrl(url);

            desertRef.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateUser() {
        FirebaseFirestore.getInstance()
                .collection("user")
                .document(userBeforeEdit.id)
                .update("name", userEdited.name,
                        "surname", userEdited.surname,
                        "about", userEdited.about,
                        "gender", userEdited.gender,
                        "birthday", userEdited.birthday,
                        "address.id", userEdited.address.id,
                        "address.name", userEdited.address.name,
                        "address.fullName", userEdited.address.fullName,
                        "address.latitude", userEdited.address.latitude,
                        "address.longitude", userEdited.address.longitude,
                        "address.southwestLongitude", userEdited.address.southwestLongitude,
                        "address.southwestLatitude", userEdited.address.southwestLatitude,
                        "address.northeastLongitude", userEdited.address.northeastLongitude,
                        "address.northeastLatitude", userEdited.address.northeastLatitude,
                        "photo", userEdited.photo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (userBeforeEdit.photo != null && userEdited.photo != null && !userEdited.photo.equals(userBeforeEdit.photo)) {
                                deletePhoto(userBeforeEdit.photo);
                            }
                            DataMediator.setUser(userEdited);
                            presenter.updateUserFinished();
                        } else {
                            presenter.errorUpdateUser();
                        }
                    }
                });

    }
}
