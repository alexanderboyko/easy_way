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
import java.util.Iterator;
import java.util.UUID;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Category;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.PriceType;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.libraries.DateHelper;
import boyko.alex.easy_way.libraries.DeepCopy;

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
    private static Item itemBeforeEdit;

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

    void setItemBeforeEdit(Item i) {
        itemBeforeEdit = null;
        itemBeforeEdit = (Item)DeepCopy.copy(i);
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

    /**
     * Compare current item and item before edit. If there were any changes - return true
     *
     * @return true if at least 1 change in data has been done
     */
    boolean isItemChanged() {
        if ((item.mainPhoto == null && itemBeforeEdit.mainPhoto != null)
                || (item.mainPhoto != null && itemBeforeEdit.mainPhoto == null)) return true;

        if (itemBeforeEdit.mainPhoto != null
                && !item.mainPhoto.equals(itemBeforeEdit.mainPhoto))
            return true;

        if ((itemBeforeEdit.photos != null && item.photos == null)
                || (itemBeforeEdit.photos == null && item.photos != null))
            return true;

        if (itemBeforeEdit.photos != null && !itemBeforeEdit.photos.isEmpty()) {
            if (item.photos == null || item.photos.isEmpty())
                return true;
            for (int i = 0; i < itemBeforeEdit.photos.size(); i++) {
                if (item.photos.size() <= i)
                    return true;
                if (!item.photos.get(i).equals(itemBeforeEdit.photos.get(i)))
                    return true;
            }
        }else{
            if(item.photos != null && !item.photos.isEmpty())
                return true;
        }

        return !item.title.equals(itemBeforeEdit.title)
                || !item.description.equals(itemBeforeEdit.description)
                || !item.notes.equals(itemBeforeEdit.notes)
                || item.price != itemBeforeEdit.price
                || !item.categoryId.equals(itemBeforeEdit.categoryId)
                || !item.priceTypeId.equals(itemBeforeEdit.priceTypeId)
                || !item.address.id.equals(itemBeforeEdit.address.id);
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

    void deleteAllPhotos() {
        if (item.mainPhoto != null) {
            deletePhoto(item.mainPhoto);
            if (item.photos != null) {
                for (String s : item.photos) deletePhoto(s);
            }
        }
    }

    void deleteAllPhotosOnCancelEdit() {
        if (item.mainPhoto != null && itemBeforeEdit.mainPhoto != null && item.mainPhoto.equals(itemBeforeEdit.mainPhoto)) {
            item.mainPhoto = null;
        }
        if (item.photos != null && !item.photos.isEmpty()) {
            Iterator<String> i = item.photos.iterator();
            while (i.hasNext()) {
                String s = i.next();
                if ((itemBeforeEdit.photos != null && itemBeforeEdit.photos.contains(s)) || (itemBeforeEdit.mainPhoto != null && itemBeforeEdit.mainPhoto.equals(s))) {
                    i.remove();
                }
            }
        }

        if (item.mainPhoto != null) {
            deletePhoto(item.mainPhoto);
        }

        if (item.photos != null && !item.photos.isEmpty()) {
            for (String s : item.photos) deletePhoto(s);
        }
    }

    private void deleteAllOldPhotosOnEditCompleted() {
        if (item.mainPhoto != null && itemBeforeEdit.mainPhoto != null && item.mainPhoto.equals(itemBeforeEdit.mainPhoto)) {
            itemBeforeEdit.mainPhoto = null;
        }
        if (itemBeforeEdit.photos != null && !itemBeforeEdit.photos.isEmpty()) {
            Iterator<String> i = itemBeforeEdit.photos.iterator();
            while (i.hasNext()) {
                String s = i.next();
                if ((item.photos != null && item.photos.contains(s)) || (item.mainPhoto != null && item.mainPhoto.equals(s))) {
                    i.remove();
                }
            }
        }

        if (itemBeforeEdit.mainPhoto != null) {
            deletePhoto(itemBeforeEdit.mainPhoto);
        }

        if (itemBeforeEdit.photos != null && !itemBeforeEdit.photos.isEmpty()) {
            for (String s : itemBeforeEdit.photos) deletePhoto(s);
        }
    }

    boolean isBeforeEditItemHasPhoto(String photo){
        if(itemBeforeEdit.mainPhoto != null && itemBeforeEdit.mainPhoto.equals(photo)){
            return true;
        }

        if(itemBeforeEdit.photos != null && !itemBeforeEdit.photos.isEmpty()){
            for(String s : itemBeforeEdit.photos){
                if(s.equals(photo)) return true;
            }
        }
        return false;
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
        item.createdAt = DateHelper.getCurrentTime();
        item.ratingCount = 0;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items")
                .add(item)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) presenter.onItemUploaded(item);
                        else presenter.onItemUploadedError();
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
        deleteAllOldPhotosOnEditCompleted();
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
                        if (task.isSuccessful()) presenter.onItemUploaded(item);
                        else presenter.onItemUploadedError();
                    }
                });
    }
}
