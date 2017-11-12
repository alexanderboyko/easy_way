package boyko.alex.easy_way.frontend.item.item_details;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.parceler.Parcels;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.backend.models.ItemBase;
import boyko.alex.easy_way.backend.models.ItemExpanded;
import boyko.alex.easy_way.backend.models.UserBase;
import boyko.alex.rentit.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sasha on 10.11.2017.
 */

public class ItemDetailsViewActivity extends AppCompatActivity {
    private LinearLayout loadingLayout;
    private Toolbar toolbar;
    private AppCompatImageView photo, likeIcon;
    private AppBarLayout appBarLayout;

    private TextView title, location, ownerName, description, notesLabel, notes, price, mark, reviewsReadOtherButton;
    private Button contactButton;
    private CircleImageView ownerPhoto;
    private LinearLayout noOwnerPhotoLayout, noItemPhotoLayout;
    private RecyclerView reviewsRecycler, seeAlsoRecycler;

    private ItemBase itemBase;
    private ItemExpanded itemExpanded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_item_details);

        init();

        itemBase = Parcels.unwrap(getIntent().getParcelableExtra("item"));
        if (itemBase != null)
            ItemDetailsPresenter.getInstance(this).startLoadingItemExpanded(itemBase);

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
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

    void setItemExpanded(ItemExpanded itemExpanded) {
        this.itemExpanded = itemExpanded;
    }

    private void init() {
        initViews();
        initToolbar();
        initAppBarLayout();
    }

    private void initViews() {
        loadingLayout = findViewById(R.id.item_details_loading_layout);
        appBarLayout = findViewById(R.id.item_details_app_bar);
        toolbar = findViewById(R.id.item_details_toolbar);
        photo = findViewById(R.id.item_details_image);
        likeIcon = findViewById(R.id.item_details_like);
        price = findViewById(R.id.item_details_price);
        mark = findViewById(R.id.item_details_average_mark);
        contactButton = findViewById(R.id.item_details_contact_button);
        title = findViewById(R.id.item_details_content).findViewById(R.id.item_details_title);
        location = findViewById(R.id.item_details_content).findViewById(R.id.item_details_location);
        ownerPhoto = findViewById(R.id.item_details_content).findViewById(R.id.item_details_user_photo);
        noOwnerPhotoLayout = findViewById(R.id.item_details_content).findViewById(R.id.item_details_no_user_photo_layout);
        ownerName = findViewById(R.id.item_details_content).findViewById(R.id.item_details_user_name);
        description = findViewById(R.id.item_details_content).findViewById(R.id.item_details_description);
        notesLabel = findViewById(R.id.item_details_content).findViewById(R.id.item_details_notes_label);
        notes = findViewById(R.id.item_details_content).findViewById(R.id.item_details_notes);
        reviewsRecycler = findViewById(R.id.item_details_content).findViewById(R.id.item_details_reviews_recycler);
        reviewsReadOtherButton = findViewById(R.id.item_details_content).findViewById(R.id.item_details_reviews_read_others);
        seeAlsoRecycler = findViewById(R.id.item_details_content).findViewById(R.id.item_details_see_also_recycler);
        noItemPhotoLayout = findViewById(R.id.item_details_content).findViewById(R.id.item_details_no_item_photo_layout);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initAppBarLayout() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    if (toolbar.getNavigationIcon() != null)
                        toolbar.getNavigationIcon().setColorFilter
                                (ContextCompat.getColor(ItemDetailsViewActivity.this, R.color.primary_dark), PorterDuff.Mode.SRC_ATOP);
                } else {
                    if (toolbar.getNavigationIcon() != null)
                        toolbar.getNavigationIcon().setColorFilter
                                (ContextCompat.getColor(ItemDetailsViewActivity.this, R.color.icons), PorterDuff.Mode.SRC_ATOP);
                }
            }
        });
    }

    private void setItemPhoto(String photoUrl) {
        if (photoUrl == null) {
            noItemPhotoLayout.setVisibility(View.VISIBLE);
            photo.setVisibility(View.GONE);
        } else {
            noItemPhotoLayout.setVisibility(View.GONE);
            photo.setVisibility(View.VISIBLE);
            Glide.with(ApplicationController.getInstance())
                    .load(photoUrl)
                    .fitCenter()
                    .crossFade()
                    .skipMemoryCache(true)
                    .dontTransform()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(photo);
        }
    }

    void setTitle(String title){
        this.title.setText(title);
    }

    void setLocation(String location){
        this.location.setText(location);
    }

    void setOwner(UserBase owner){
        if(owner != null){
            this.ownerName.setText(owner.getFullName());
            if(owner.photo != null){
                ownerPhoto.setVisibility(View.VISIBLE);
                noOwnerPhotoLayout.setVisibility(View.GONE);
                Glide.with(ApplicationController.getInstance())
                        .load(owner.photo)
                        .fitCenter()
                        .crossFade()
                        .skipMemoryCache(true)
                        .dontTransform()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(ownerPhoto);
            }else{
                ownerPhoto.setVisibility(View.GONE);
                noOwnerPhotoLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    void setDescription(String description){
        this.description.setText(description);
    }

    void setNotes(String notes){
        if(notes == null){
            this.notesLabel.setVisibility(View.GONE);
            this.notes.setVisibility(View.GONE);
        }else{
            this.notesLabel.setVisibility(View.VISIBLE);
            this.notes.setVisibility(View.VISIBLE);
            this.notes.setText(notes);
        }
    }
}
