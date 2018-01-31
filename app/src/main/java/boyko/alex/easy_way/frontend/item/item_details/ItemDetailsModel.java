package boyko.alex.easy_way.frontend.item.item_details;

import android.support.annotation.NonNull;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.models.Booking;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.Like;
import boyko.alex.easy_way.backend.models.Review;

/**
 * Created by Sasha on 12.11.2017.
 */

class ItemDetailsModel {
    private final String LOG_TAG = getClass().getSimpleName();

    private ItemDetailsPresenter presenter;
    private static ItemDetailsModel model;

    private boolean bookingsLoaded = false, reviewsLoaded = false, similarItemsLoaded = false, ownerLoaded = false, addressLoaded = false;

    private ItemDetailsModel(ItemDetailsPresenter presenter) {
        this.presenter = presenter;
    }

    public static ItemDetailsModel getInstance(ItemDetailsPresenter presenter) {
        if (model == null) {
            model = new ItemDetailsModel(presenter);
        }
        return model;
    }

    void startLoading(String itemId) {
        FirebaseFirestore.getInstance().collection("items").document(itemId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Item item = ConvertHelper.convertToItem(task.getResult());
                            presenter.setItem(item);
                            if(item != null) {
                                loadBookings(item);
                                loadReviews(item);
                                loadSimilarItems(item);
                                loadOwner(item);
                                loadAddressById(item.address.id);
                            }
                        }
                    }
                });
    }

    void startLoading(Item item) {
        if(item != null) {
            loadBookings(item);
            loadReviews(item);
            loadSimilarItems(item);
            loadOwner(item);
            loadAddressById(item.address.id);
        }
    }

    private void checkIfLoaded() {
        if (bookingsLoaded && reviewsLoaded && similarItemsLoaded && ownerLoaded && addressLoaded) {
            presenter.loadingFinished();
        }
    }

    private void loadBookings(Item item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("bookings")
                .whereEqualTo("itemId", item.id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Booking> bookings = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Booking booking = document.toObject(Booking.class);
                        booking.id = document.getId();
                        bookings.add(booking);
                    }
                    presenter.setBookings(bookings);
                    bookingsLoaded = true;
                    checkIfLoaded();
                }
            }
        });
    }

    private void loadReviews(Item item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("reviews").whereEqualTo("itemId", item.id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Review> reviews = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Review review = document.toObject(Review.class);
                        review.id = document.getId();
                        reviews.add(review);
                    }
                    presenter.setReviews(reviews);
                    reviewsLoaded = true;
                    checkIfLoaded();
                }
            }
        });
    }

    private void loadSimilarItems(final Item item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        double lowerPrice = item.price - (item.price * 0.3f);
        if (lowerPrice < 0) lowerPrice = 0;

        double higherPrice = item.price + (item.price * 0.3f);

        Query query = db.collection("items")
                .whereGreaterThan("price", lowerPrice)
                .whereLessThan("price", higherPrice)
                .whereEqualTo("categoryId", item.categoryId)
                .limit(10);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Item> items = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.getId().equals(item.id)) continue;
                        items.add(ConvertHelper.convertToItem(document));
                    }
                    presenter.setSimilarItems(items);
                    similarItemsLoaded = true;
                    checkIfLoaded();
                }
            }
        });
    }

    private void loadOwner(Item item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference owner = db.collection("user").document(item.ownerId);
        owner.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    presenter.setOwner(ConvertHelper.convertToUser(task.getResult()));
                    ownerLoaded = true;
                    checkIfLoaded();
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
                        addressLoaded = true;
                        presenter.setAddress(ConvertHelper.convertPlaceToAddress(myPlace));
                        checkIfLoaded();

                        places.release();
                    } else {
                        if (task.getException() != null){
                            checkIfLoaded();
                        }

                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    addressLoaded = true;
                    checkIfLoaded();
                }
            });
        }
    }

    void addLike(final Item item) {
        final Like like = new Like();
        like.id = null;
        like.itemId = item.id;
        like.userId = DataMediator.getUser().id;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("likes")
                .add(like)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        like.id = documentReference.getId();
                        DataMediator.getLikes().add(like);
                        presenter.likeAdded();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        presenter.likeGetError();
                    }
                });
    }

    void removeLike(final Like like) {
        DataMediator.removeLike(like.id);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("likes").document(like.id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        presenter.likeRemoved();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        presenter.likeGetError();
                    }
                });
    }
}
