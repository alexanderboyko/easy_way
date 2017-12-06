package boyko.alex.easy_way.frontend.item.item_edit;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Category;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.PriceType;
import boyko.alex.easy_way.backend.models.User;

/**
 * Created by Sasha on 02.12.2017.
 */

class AddItemModel {
    private AddItemPresenter presenter;
    private static AddItemModel model;
    private Item item;

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

    void initItem() {
        item = new Item();
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

//    void setItemType(ItemType itemType) {
//        item.itemTypeId = itemType.id;
//    }

    void setOwner(User user) {
        item.ownerId = user.id;
    }

    void setPhotos(ArrayList<String> photos) {
        item.photos = photos;
    }

    private void addPhoto(String photo, boolean isMain) {
        if (item.photos == null) item.photos = new ArrayList<>();
        if (isMain) item.mainPhoto = photo;
        else item.photos.add(photo);
    }

    void uploadPhoto(Bitmap bitmap, final boolean isMain) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef
                .child("items/" + DataMediator.getUser().getFullName() + UUID.nameUUIDFromBytes(data));


        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot != null && taskSnapshot.getDownloadUrl() != null) {
                    addPhoto(taskSnapshot.getDownloadUrl().toString(), isMain);
                    presenter.photoUploaded();
                }
            }
        });
    }

    void uploadItem(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items").add(item).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()) presenter.onItemUploaded();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
