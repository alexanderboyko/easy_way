package boyko.alex.easy_way.frontend.item.item_details;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.backend.models.Booking;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.models.Review;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.frontend.custom_views.AvailabilityCalendar;
import boyko.alex.easy_way.frontend.explore.ItemsRecyclerAdapter;
import boyko.alex.easy_way.libraries.DateHelper;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sasha on 10.11.2017.
 */

public class ItemDetailsViewActivity extends AppCompatActivity {
    private LinearLayout loadingLayout;
    private Toolbar toolbar;
    private AppCompatImageView photo, likeIcon;
    private AppBarLayout appBarLayout;

    private TextView title, location, ownerName, description, notesLabel, notes, price, rating, reviewsReadOtherButton;
    private Button contactButton;
    private CircleImageView ownerPhoto;
    private LinearLayout noOwnerPhotoLayout, noItemPhotoLayout;
    private RecyclerView reviewsRecycler, similarItemsRecycler;
    private ItemsRecyclerAdapter similarItemsAdapter;

    private AvailabilityCalendar calendarView;

    private Item item;
    private ArrayList<Booking> bookings;
    private ArrayList<Review> reviews;
    private ArrayList<Item> similarItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_item_details);

        init();

        item = Parcels.unwrap(getIntent().getParcelableExtra("item"));
        if (item != null) ItemDetailsPresenter.getInstance(this).startLoading(item);

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

    private void init() {
        initViews();
        initToolbar();
        initAppBarLayout();
        initSimilarItemsRecycler();
        initCalendar();
    }

    private void initViews() {
        loadingLayout = findViewById(R.id.item_details_loading_layout);
        appBarLayout = findViewById(R.id.item_details_app_bar);
        toolbar = findViewById(R.id.item_details_toolbar);
        photo = findViewById(R.id.item_details_image);
        likeIcon = findViewById(R.id.item_details_like);
        price = findViewById(R.id.item_details_price);
        rating = findViewById(R.id.item_details_average_mark);
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
        similarItemsRecycler = findViewById(R.id.item_details_content).findViewById(R.id.item_details_see_also_recycler);
        noItemPhotoLayout = findViewById(R.id.item_details_no_item_photo_layout);
        calendarView = findViewById(R.id.item_details_content).findViewById(R.id.item_details_calendar);
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

    private void initSimilarItemsRecycler(){
        similarItemsAdapter = new ItemsRecyclerAdapter(ItemsRecyclerAdapter.MODE_LINEAR);
        similarItemsAdapter.setOnItemClickListener(new ItemsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                //todo
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        similarItemsRecycler.setLayoutManager(layoutManager);
        similarItemsRecycler.setAdapter(similarItemsAdapter);
    }

    private void initCalendar(){
        calendarView.setPage(AvailabilityCalendar.PAGE_CENTER);
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                int monthSelected = DateHelper.whichMonthSelectedCompareToCurrent(firstDayOfNewMonth.getTime());
                if(monthSelected == 0) calendarView.setPage(AvailabilityCalendar.PAGE_LEFT);
                else{
                    if(monthSelected == 1) calendarView.setPage(AvailabilityCalendar.PAGE_CENTER);
                    else calendarView.setPage(AvailabilityCalendar.PAGE_RIGHT);
                }
            }
        });
        calendarView.setLocale(TimeZone.getDefault(), Locale.ENGLISH);
        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.displayOtherMonthDays(false);
        calendarView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        //calendar.setMinDate(DateHelper.getDayLastMonth(Calendar.getInstance().getTimeInMillis()));
        //calendar.setMaxDate(DateHelper.getDayInAMonth(Calendar.getInstance().getTimeInMillis()));
    }

    void setItemPhoto(String photoUrl) {
        if (photoUrl == null) {
            noItemPhotoLayout.setVisibility(View.VISIBLE);
            photo.setVisibility(View.GONE);
        } else {
            noItemPhotoLayout.setVisibility(View.GONE);
            photo.setVisibility(View.VISIBLE);
            Glide.with(ApplicationController.getInstance())
                    .load(photoUrl)
                    .apply(RequestOptions.fitCenterTransform())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                    .apply(RequestOptions.noTransformation())
                    .into(photo);
        }
    }

    void setTitle(String title) {
        this.title.setText(title);
    }

    void setPrice(String price){
        this.price.setText(price);
    }

    void setLocation(String location) {
        this.location.setText(location);
    }

    void setOwner(User owner){
        if(owner != null){
            this.ownerName.setText(owner.getFullName());
            if(owner.photos != null && !owner.photos.isEmpty()){
                ownerPhoto.setVisibility(View.VISIBLE);
                noOwnerPhotoLayout.setVisibility(View.GONE);
                Glide.with(ApplicationController.getInstance())
                        .load(owner.photos.get(0))
                        .apply(RequestOptions.fitCenterTransform())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                        .apply(RequestOptions.noTransformation())
                        .into(ownerPhoto);
            }else{
                ownerPhoto.setVisibility(View.GONE);
                noOwnerPhotoLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    void setDescription(String description) {
        this.description.setText(description);
    }

    void setNotes(String notes) {
        if (notes == null) {
            this.notesLabel.setVisibility(View.GONE);
            this.notes.setVisibility(View.GONE);
        } else {
            this.notesLabel.setVisibility(View.VISIBLE);
            this.notes.setVisibility(View.VISIBLE);
            this.notes.setText(notes);
        }
    }

    void setRating(String rating){
        this.rating.setText(rating);
    }

    void setLoading(boolean isLoading) {
        if (isLoading) loadingLayout.setVisibility(View.VISIBLE);
        else loadingLayout.setVisibility(View.GONE);
    }

    void setBookings(ArrayList<Booking> bookings) {
        this.bookings = bookings;
    }

    void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    void setSimilarItems(ArrayList<Item> similarItems) {
        this.similarItems = similarItems;

        ArrayList<Object> items = new ArrayList<>();
        items.addAll(similarItems);

        similarItemsAdapter.setItems(items);
        similarItemsAdapter.notifyItemRangeInserted(0, items.size());
    }
}
