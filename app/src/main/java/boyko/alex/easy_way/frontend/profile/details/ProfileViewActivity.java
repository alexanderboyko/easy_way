package boyko.alex.easy_way.frontend.profile.details;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.parceler.Parcels;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.Review;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.frontend.explore.ItemsRecyclerAdapter;
import boyko.alex.easy_way.frontend.explore.ReviewsRecyclerAdapter;
import boyko.alex.easy_way.frontend.gallery.GalleryFragment;
import boyko.alex.easy_way.frontend.item.item_details.ItemDetailsViewActivity;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sasha on 17.12.2017.
 * <p>
 * Iser profile details activity.
 */

public class ProfileViewActivity extends AppCompatActivity {
    private Toast toast;

    private Toolbar toolbar;
    private CircleImageView photo;
    private LinearLayout noPhotoLayout;
    private TextView userName, address, about, noItemsMessage, noReviewsMessage, readMoreReviews;
    private RecyclerView itemsRecycler, reviewsRecycler;
    private ItemsRecyclerAdapter itemsAdapter;
    private ReviewsRecyclerAdapter reviewsAdapter;
    private View loadingBackground;
    private ProgressBar progressBar;

    private User user;
    private Address addressObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_profile);

        init();

        ProfilePresenter.getInstance(this).onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("items", Parcels.wrap(itemsAdapter.getItems()));
        outState.putParcelable("reviews", Parcels.wrap(reviewsAdapter.getReviews()));
        outState.putParcelable("user", Parcels.wrap(user));
        outState.putParcelable("address", Parcels.wrap(addressObject));
        super.onSaveInstanceState(outState);
    }

    private void init() {
        initViews();
        initToolbar();
        initRecyclerViews();
    }

    private void initViews() {
        toolbar = findViewById(R.id.profile_toolbar);
        photo = findViewById(R.id.profile_photo);
        noPhotoLayout = findViewById(R.id.profile_no_photo_layout);
        userName = findViewById(R.id.profile_user_name);
        address = findViewById(R.id.profile_address);
        about = findViewById(R.id.profile_about);
        noItemsMessage = findViewById(R.id.profile_items_empty_message);
        noReviewsMessage = findViewById(R.id.profile_reviews_empty_message);
        readMoreReviews = findViewById(R.id.profile_reviews_read_others);
        itemsRecycler = findViewById(R.id.profile_items_recycler);
        reviewsRecycler = findViewById(R.id.profile_reviews_recycler);
        loadingBackground = findViewById(R.id.profile_loading_background);
        progressBar = findViewById(R.id.profile_progress_bar);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initPhoto() {
        if (user.photo == null) {
            noPhotoLayout.setVisibility(View.VISIBLE);
            photo.setVisibility(View.GONE);
        } else {
            noPhotoLayout.setVisibility(View.GONE);
            photo.setVisibility(View.VISIBLE);
            try {
                Glide.with(ApplicationController.getInstance())
                        .load(new URL(user.photo))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                        .apply(RequestOptions.noTransformation())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                noPhotoLayout.setVisibility(View.VISIBLE);
                                photo.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(photo);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> photos = new ArrayList<>();
                photos.add(user.photo);
                launchFullscreenGallery(photos, 0);
            }
        });
    }

    private void initRecyclerViews() {
        itemsAdapter = new ItemsRecyclerAdapter(ItemsRecyclerAdapter.MODE_LINEAR_HORIZONTAL);
        itemsAdapter.setOnItemClickListener(new ItemsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                launchItemDetailsActivity((Item) itemsAdapter.getItems().get(position));
            }

            @Override
            public void onLikeClicked(int position) {
                ProfilePresenter.getInstance(ProfileViewActivity.this).onLikeClicked(((Item) itemsAdapter.getItems().get(position)).id);

                ((Item) itemsAdapter.getItems().get(position)).isLiked = !((Item) itemsAdapter.getItems().get(position)).isLiked;
                itemsAdapter.notifyItemChanged(position);
            }

            @Override
            public void onEditClicked(int position) {

            }

            @Override
            public void onDeleteClicked(int position) {

            }
        });
        itemsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        itemsRecycler.setAdapter(itemsAdapter);

        reviewsAdapter = new ReviewsRecyclerAdapter(ReviewsRecyclerAdapter.MODE_ITEM_DETAILS);
        reviewsAdapter.setListener(new ReviewsRecyclerAdapter.OnReviewClickListener() {
            @Override
            public void onUserClicked(int position) {
                launchProfileActivity(reviewsAdapter.getReviews().get(position).userId);
            }
        });
        reviewsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        reviewsRecycler.setNestedScrollingEnabled(false);
        reviewsRecycler.setAdapter(reviewsAdapter);
    }

    void setUser(User user) {
        this.user = user;
        initPhoto();

        userName.setText(user.getFullName());
        if (user.about != null) about.setText(user.about);
        else about.setText(getString(R.string.no_info));


    }

    void setItems(@NonNull ArrayList<Item> items1) {
        ArrayList<Object> items = new ArrayList<>();
        items.addAll(items1);

        itemsAdapter.setItems(items);
        itemsAdapter.notifyItemRangeInserted(0, items.size());

        noItemsMessage.setVisibility(View.GONE);
    }

    void setNoItemsMessageVisible() {
        noItemsMessage.setVisibility(View.VISIBLE);
        itemsRecycler.setVisibility(View.GONE);
    }

    void setReviews(@NonNull ArrayList<Review> reviews) {
        reviewsAdapter.setReviews(reviews);
        reviewsAdapter.notifyItemRangeInserted(0, reviews.size());
        noReviewsMessage.setVisibility(View.GONE);
    }

    void setNoReviewsMessageVisible() {
        noReviewsMessage.setVisibility(View.VISIBLE);
        reviewsRecycler.setVisibility(View.GONE);
        readMoreReviews.setVisibility(View.GONE);
    }

    void setAddress(Address address) {
        addressObject = address;
        if (address != null) this.address.setText(address.fullName);
        else this.address.setText(getString(R.string.no_info));
    }

    void setLoading(boolean isLoading) {
        if (isLoading) {
            loadingBackground.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            loadingBackground.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    void showError(String error) {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(this, error, Toast.LENGTH_LONG);
        toast.show();
    }

    void launchItemDetailsActivity(Item item) {
        Intent intent = new Intent(this, ItemDetailsViewActivity.class);
        intent.putExtra("item", Parcels.wrap(item));
        startActivity(intent);
    }

    void launchProfileActivity(String userId) {
        Intent intent = new Intent(this, ProfileViewActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    void launchFullscreenGallery(ArrayList<String> images, int position) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("images", images);
        bundle.putInt("position", position);

        GalleryFragment newFragment = GalleryFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "fullscreenGallery");
    }
}
