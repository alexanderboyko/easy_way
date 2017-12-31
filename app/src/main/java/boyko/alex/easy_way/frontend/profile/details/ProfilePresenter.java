package boyko.alex.easy_way.frontend.profile.details;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.util.ArrayList;

import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.Like;
import boyko.alex.easy_way.backend.models.Review;
import boyko.alex.easy_way.backend.models.User;

/**
 * Created by Sasha on 17.12.2017.
 */

class ProfilePresenter {
    private static ProfilePresenter presenter;
    private ProfileViewActivity view;

    private boolean dataLoaded = false;

    private ProfilePresenter(ProfileViewActivity view) {
        this.view = view;
    }

    static ProfilePresenter getInstance(ProfileViewActivity view) {
        if (presenter == null) {
            presenter = new ProfilePresenter(view);
        } else {
            presenter.view = view;
        }
        return presenter;
    }

    void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            view.setLoading(true);
            String userId = view.getIntent().getStringExtra("userId");
            if (userId != null) ProfileModel.getInstance(this).startLoading(userId);
        } else {
            if(dataLoaded){
                setUser((User)Parcels.unwrap(savedInstanceState.getParcelable("user")));
                setItems((ArrayList)Parcels.unwrap(savedInstanceState.getParcelable("items")));
                setReviews((ArrayList)Parcels.unwrap(savedInstanceState.getParcelable("reviews")));
                setAddress((Address)Parcels.unwrap(savedInstanceState.getParcelable("address")));
                loadingFinished();
            }else{
                dataLoaded = false;
                view.setLoading(true);
                String userId = view.getIntent().getStringExtra("userId");
                if (userId != null) ProfileModel.getInstance(this).startLoading(userId);
            }
        }
    }

    void loadingFinished() {
        dataLoaded = true;
        view.setLoading(false);
    }

    void setUser(User user) {
        view.setUser(user);
    }

    void setItems(ArrayList<Item> items) {
        if (items != null && !items.isEmpty()) {
            for (Object object : items) {
                if (object instanceof Item) {
                    if (DataMediator.getLike(((Item) object).id) != null) {
                        ((Item) object).isLiked = true;
                    }
                }
            }
            view.setItems(items);
        }
        else view.setNoItemsMessageVisible();
    }

    void setReviews(ArrayList<Review> reviews) {
        if (reviews != null && !reviews.isEmpty()) view.setReviews(reviews);
        else view.setNoReviewsMessageVisible();
    }

    void setAddress(Address address) {
        view.setAddress(address);
    }

    void showError(String error) {
        view.showError(error);
    }

    void onLikeClicked(String itemId) {
        if (DataMediator.getLike(itemId) == null) {
            final Like like1 = new Like();
            like1.itemId = itemId;
            like1.userId = DataMediator.getUser().id;
            FirebaseFirestore.getInstance().collection("likes").add(like1).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        like1.id = task.getResult().getId();
                        DataMediator.getLikes().add(like1);
                    }
                }
            });
        }else{
            Like like = DataMediator.getLike(itemId);
            if(like != null && like.id != null) {
                FirebaseFirestore.getInstance().collection("likes").document(like.id).delete();
                DataMediator.removeLike(like.id);
            }
        }
    }
}
