package boyko.alex.easy_way.frontend.profile.details;

import android.support.annotation.NonNull;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.Review;
import boyko.alex.easy_way.backend.models.User;

/**
 * Created by Sasha on 17.12.2017.
 */

class ProfileModel {
    private static ProfileModel model;
    private ProfilePresenter presenter;

    private boolean userLoaded = false, itemsLoaded = false, reviewsLoaded = false, addressLoaded = false;

    private ProfileModel(ProfilePresenter presenter) {
        this.presenter = presenter;
    }

    static ProfileModel getInstance(ProfilePresenter presenter) {
        if (model == null) {
            model = new ProfileModel(presenter);
        }
        return model;
    }

    void startLoading(String userId) {
        userLoaded = false;
        itemsLoaded = false;
        reviewsLoaded = false;
        addressLoaded = false;

        startLoadingUser(userId);
        startLoadingItems(userId);
        startLoadingReviews(userId);
    }

    private void startLoadingUser(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference owner = db.collection("user").document(userId);
        owner.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = ConvertHelper.convertToUser(task.getResult());
                    presenter.setUser(user);

                    if (user.address != null && user.address.id != null) {
                        loadAddressById(user.address.id);
                    } else addressLoaded = true;

                    userLoaded = true;
                    checkIfLoaded();
                } else {
                    if (task.getException() != null)
                        presenter.showError(task.getException().getMessage());
                }
            }
        });
    }

    private void startLoadingItems(@NonNull String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("items").whereEqualTo("ownerId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Item> items = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        items.add(ConvertHelper.convertToItem(document));
                    }
                    presenter.setItems(items);
                    itemsLoaded = true;
                    checkIfLoaded();
                } else {
                    if (task.getException() != null)
                        presenter.showError(task.getException().getMessage());
                }
            }
        });
    }

    private void startLoadingReviews(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("reviews").whereEqualTo("ownerId", userId);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Review> reviews = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        reviews.add(ConvertHelper.convertToReview(document));
                    }
                    presenter.setReviews(reviews);
                    reviewsLoaded = true;
                    checkIfLoaded();
                } else {
                    if (task.getException() != null)
                        presenter.showError(task.getException().getMessage());
                }
            }
        });
    }

    private void loadAddressById(String id) {
        if (id != null) {
            Places.getGeoDataClient(ApplicationController.getInstance(), null).getPlaceById(id).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if (task.isSuccessful()) {
                        PlaceBufferResponse places = task.getResult();
                        Place myPlace = places.get(0);

                        presenter.setAddress(ConvertHelper.convertPlaceToAddress(myPlace));
                        addressLoaded = true;
                        checkIfLoaded();

                        places.release();
                    } else {
                        if (task.getException() != null){
                            presenter.showError(task.getException().getMessage());
                            presenter.setAddress(null);
                            addressLoaded = true;
                            checkIfLoaded();
                        }

                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    presenter.showError(e.getMessage());
                    presenter.setAddress(null);
                    addressLoaded = true;
                    checkIfLoaded();
                }
            });
        }
    }

    private void checkIfLoaded() {
        if (userLoaded && itemsLoaded && reviewsLoaded && addressLoaded) {
            presenter.loadingFinished();
        }
    }
}
