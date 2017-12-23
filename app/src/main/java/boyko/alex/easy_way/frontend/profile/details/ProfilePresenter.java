package boyko.alex.easy_way.frontend.profile.details;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.Review;
import boyko.alex.easy_way.backend.models.User;

/**
 * Created by Sasha on 17.12.2017.
 */

class ProfilePresenter {
    private static ProfilePresenter presenter;
    private ProfileViewActivity view;

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

        }
    }


    void loadingFinished() {
        view.setLoading(false);
    }

    void setUser(User user) {
        view.setUser(user);
    }

    void setItems(ArrayList<Item> items) {
        if(items != null && !items.isEmpty()) view.setItems(items);
        else view.setNoItemsMessageVisible();
    }

    void setReviews(ArrayList<Review> reviews) {
        if(reviews != null && !reviews.isEmpty()) view.setReviews(reviews);
        else view.setNoReviewsMessageVisible();
    }

    void setAddress(Address address){
        view.setAddress(address);
    }

    void showError(String error) {
        view.showError(error);
    }
}
