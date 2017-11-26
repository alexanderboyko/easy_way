package boyko.alex.easy_way.frontend.item.item_details;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.DummyGenerator;
import boyko.alex.easy_way.backend.models.Booking;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.Review;

/**
 * Created by Sasha on 12.11.2017.
 */

class ItemDetailsModel {
    private final String LOG_TAG = getClass().getSimpleName();

    private ItemDetailsPresenter presenter;
    private static ItemDetailsModel model;

    private boolean bookingsLoaded = false, reviewsLoaded = false, similarItemsLoaded = false, ownerLoaded = false;

    private ItemDetailsModel(ItemDetailsPresenter presenter) {
        this.presenter = presenter;
    }

    public static ItemDetailsModel getInstance(ItemDetailsPresenter presenter) {
        if (model == null) {
            model = new ItemDetailsModel(presenter);
        }
        return model;
    }

    void startLoading(Item item) {
        loadBookings(item);
        loadReviews(item);
        loadSimilarItems(item);
        loadOwner(item);
    }

    private void checkIfLoaded() {
        if (bookingsLoaded && reviewsLoaded && similarItemsLoaded && ownerLoaded) {
            presenter.loadingFinished();
        }
    }

    private void loadBookings(Item item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("booking").whereEqualTo("itemId", item.id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Booking> bookings = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.i(LOG_TAG, document.getData().toString());
                        bookings.add(ConvertHelper.convertToBooking(document));
                    }
                    presenter.setBookings(bookings);
                    bookingsLoaded = true;
                    checkIfLoaded();
                } else {
                    Log.w(LOG_TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void loadReviews(Item item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("review").whereEqualTo("itemId", item.id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Review> reviews = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.i(LOG_TAG, document.getData().toString());
                        reviews.add(ConvertHelper.convertToReview(document));
                    }
                    presenter.setReviews(reviews);
                    reviewsLoaded = true;
                    checkIfLoaded();
                } else {
                    Log.w(LOG_TAG, "Error getting documents.", task.getException());
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
                        if(document.getId().equals(item.id)) continue;
                        items.add(ConvertHelper.convertToItem(document));
                    }
                    presenter.setSimilarItems(items);
                    similarItemsLoaded = true;
                    checkIfLoaded();
                } else {
                    Log.w(LOG_TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void loadOwner(Item item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference owner = db.collection("user").document("PbILqY0GA6sjqIS727Tz");
        owner.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.i(LOG_TAG, task.getResult().getData().toString());
                    presenter.setOwner(ConvertHelper.convertToUser(task.getResult()));
                    ownerLoaded = true;
                    checkIfLoaded();
                }else{
                    Log.w(LOG_TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

}
