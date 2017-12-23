package boyko.alex.easy_way.frontend.item.item_edit;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Category;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.PriceType;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.libraries.DateHelper;

/**
 * Created by Sasha on 02.12.2017.
 * <p>
 * Server communication is here. Data handle too
 */

class AddItemModel {
    static final int MODE_CREATE = 1, MODE_EDIT = 2;

    private AddItemPresenter presenter;
    private static AddItemModel model;
    private Item item;

    private int mode = MODE_CREATE;

    private AddItemModel(AddItemPresenter presenter) {
        initItem();
        this.presenter = presenter;
    }

    static AddItemModel getInstance(AddItemPresenter presenter) {
        if (model == null) {
            model = new AddItemModel(presenter);
        }
        return model;
    }

    void setItem(Item item) {
        this.item = item;
    }

    void setMode(int mode) {
        this.mode = mode;
    }

    int getMode() {
        return mode;
    }

    void initItem() {
        item = new Item();
    }

    Item getItem() {
        return item;
    }

    ArrayList<String> getAllPhotos() {
        ArrayList<String> photos = new ArrayList<>();
        if (item.mainPhoto != null) photos.add(item.mainPhoto);
        if (item.photos != null && !item.photos.isEmpty()) photos.addAll(item.photos);
        return photos;
    }

    void addPhoto(String url) {
        if (item.mainPhoto == null) {
            item.mainPhoto = url;
        } else {
            if (item.photos == null) {
                item.photos = new ArrayList<>();
                item.photos.add(url);
            } else {
                item.photos.add(url);
            }
        }
    }

    void setAddress(Address address) {
        item.address = address;
    }

    void setPriceType(PriceType priceType) {
        item.priceTypeId = priceType.id;
    }

    void setTitle(String title) {
        item.title = title;
    }

    void setDescription(String description) {
        item.description = description;
    }

    void setNotes(String notes) {
        item.notes = notes;
    }

    void setPrice(double price) {
        item.price = price;
    }

    void setCategory(Category category) {
        item.categoryId = category.id;
    }

    void setOwner(User user) {
        item.ownerId = user.id;
    }

    void loadAddressById(String id) {
        if (id != null) {
            Places.getGeoDataClient(ApplicationController.getInstance(), null).getPlaceById(id).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if (task.isSuccessful()) {
                        PlaceBufferResponse places = task.getResult();
                        Place myPlace = places.get(0);

                        presenter.onAddressSelected(ConvertHelper.convertPlaceToAddress(myPlace));

                        places.release();
                    } else {
                        presenter.onAddressSelectError(null);
                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    presenter.onAddressSelectError(null);
                }
            });
        }
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

    void deleteAllPhotos(){
        if(item.mainPhoto != null){
            deletePhoto(item.mainPhoto);
            if(item.photos != null){
                for(String s : item.photos) deletePhoto(s);
            }
        }
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

    void uploadItem() {
        item.createdAt = DateHelper.getTodayTime();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items")
                .add(item)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) presenter.onItemUploaded();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        presenter.onItemUploadedError();
                    }
                });
    }

    void updateItem() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items")
                .document(item.id)
                .update("title", item.title,
                        "description", item.description,
                        "notes", item.notes,
                        "price", item.price,
                        "categoryId", item.categoryId,
                        "priceTypeId", item.priceTypeId,
                        "address.id", item.address.id,
                        "address.fullName", item.address.fullName,
                        "address.latitude", item.address.latitude,
                        "address.longitude", item.address.longitude,
                        "address.name", item.address.name,
                        "address.northeastLatitude", item.address.northeastLatitude,
                        "address.northeastLongitude", item.address.northeastLongitude,
                        "address.southwestLatitude", item.address.southwestLatitude,
                        "address.southwestLongitude", item.address.southwestLongitude,
                        "photos", item.photos,
                        "mainPhoto", item.mainPhoto)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) presenter.onItemUploaded();
                        else presenter.onItemUploadedError();
                    }
                });
    }
}
