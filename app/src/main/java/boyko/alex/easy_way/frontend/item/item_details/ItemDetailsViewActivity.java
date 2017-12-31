package boyko.alex.easy_way.frontend.item.item_details;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.parceler.Parcels;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Booking;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.Review;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.frontend.custom_views.AvailabilityCalendar;
import boyko.alex.easy_way.frontend.explore.ItemsRecyclerAdapter;
import boyko.alex.easy_way.frontend.explore.ReviewsRecyclerAdapter;
import boyko.alex.easy_way.frontend.gallery.GalleryFragment;
import boyko.alex.easy_way.frontend.item.item_details.contact.ContactActivity;
import boyko.alex.easy_way.frontend.profile.details.ProfileViewActivity;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sasha on 10.11.2017.
 * <p>
 * Item details here
 */

public class ItemDetailsViewActivity extends AppCompatActivity {
    private Toast toast;

    private LinearLayout loadingLayout;
    private Toolbar toolbar;
    private AppCompatImageView photo;
    private AppBarLayout appBarLayout;

    private TextView title, location, ownerName, description, notesLabel, notes, price, rating, reviewsEmptyMessage, similarItemsEmptyMessage;
    private Button contactButton;
    private CircleImageView ownerPhoto;
    private LinearLayout noOwnerPhotoLayout, noItemPhotoLayout;
    private RecyclerView reviewsRecycler, similarItemsRecycler;
    private ItemsRecyclerAdapter similarItemsAdapter;
    private ReviewsRecyclerAdapter reviewsRecyclerAdapter;

    private AvailabilityCalendar calendarView;
    private AppCompatImageView calendarButtonSwipeMonthLeft, calendarButtonSwipeMonthRight;
    private TextView calendarMonthName;

    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheetBookings, shadow;
    private RecyclerView bottomSheetBookingsRecycler;
    private BookingsInfoRecyclerAdapter bottomSheetBookingsAdapter;
    private TextView bottomSheetEmptyMessage, bottomSheetDate;

    private Item item;
    private ArrayList<Booking> bookings;
    private User owner;

    private int toolbarLikeIcon = R.drawable.ic_favorite_border_white_24px;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_item_details);

        init();

        ItemDetailsPresenter.getInstance(this).onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ItemDetailsPresenter.getInstance(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_details_menu, menu);
        menu.findItem(R.id.item_details_menu_like).setIcon(toolbarLikeIcon);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.item_details_menu_like:
                ItemDetailsPresenter.getInstance(ItemDetailsViewActivity.this).onLikeClicked(this.item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("item", Parcels.wrap(item));
        outState.putParcelable("similarItems", Parcels.wrap(similarItemsAdapter.getItems()));
        outState.putParcelable("bookings", Parcels.wrap(bookings));
        outState.putParcelable("owner", Parcels.wrap(owner));
        outState.putParcelable("reviews", Parcels.wrap(reviewsRecyclerAdapter.getReviews()));
        super.onSaveInstanceState(outState);
    }

    private void init() {
        initViews();
        initToolbar();
        initAppBarLayout();
        initSimilarItemsRecycler();
        initReviewsRecycler();
        initCalendar();
        initBottomSheet();
        initShadow();
    }

    private void initViews() {
        loadingLayout = findViewById(R.id.item_details_loading_layout);
        appBarLayout = findViewById(R.id.item_details_app_bar);
        toolbar = findViewById(R.id.item_details_toolbar);
        photo = findViewById(R.id.item_details_image);
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
        reviewsEmptyMessage = findViewById(R.id.item_details_content).findViewById(R.id.item_details_reviews_empty_message);
        similarItemsEmptyMessage = findViewById(R.id.item_details_content).findViewById(R.id.item_details_see_also_empty_message);
        similarItemsRecycler = findViewById(R.id.item_details_content).findViewById(R.id.item_details_see_also_recycler);
        noItemPhotoLayout = findViewById(R.id.item_details_no_item_photo_layout);
        calendarView = findViewById(R.id.item_details_content).findViewById(R.id.item_details_calendar);
        calendarButtonSwipeMonthLeft = findViewById(R.id.item_details_content).findViewById(R.id.item_details_calendar_swipe_left_button);
        calendarButtonSwipeMonthRight = findViewById(R.id.item_details_content).findViewById(R.id.item_details_calendar_swipe_right_button);
        calendarMonthName = findViewById(R.id.item_details_content).findViewById(R.id.item_details_calendar_month);
        bottomSheetBookings = findViewById(R.id.item_details_bottom_sheet_bookings);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetBookings);
        shadow = findViewById(R.id.item_details_shadow);
        bottomSheetBookingsRecycler = findViewById(R.id.item_details_bottom_sheet_bookings).findViewById(R.id.item_details_bottom_sheet_bookings_recycler);
        bottomSheetEmptyMessage = findViewById(R.id.item_details_bottom_sheet_bookings).findViewById(R.id.item_details_bottom_sheet_empty_message);
        bottomSheetDate = findViewById(R.id.item_details_bottom_sheet_bookings).findViewById(R.id.item_details_bottom_sheet_date);
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
        Drawable backgrounds[] = new Drawable[2];
        backgrounds[0] = getResources().getDrawable(R.drawable.drawable_toolbar_gradient, null);
        backgrounds[1] = getResources().getDrawable(R.drawable.drawable_toolbar_white, null);

        final TransitionDrawable transitionDrawable = new TransitionDrawable(backgrounds);
        toolbar.setBackground(transitionDrawable);
        transitionDrawable.startTransition(0);
        toolbar.setTag(0);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                    if (!toolbar.getTag().equals(0)) {
                        if (toolbar.getNavigationIcon() != null) {
                            toolbar.getNavigationIcon().setColorFilter
                                    (ContextCompat.getColor(ItemDetailsViewActivity.this, R.color.primary_dark), PorterDuff.Mode.SRC_ATOP);
                            transitionDrawable.reverseTransition(200);
                            toolbar.setTag(0);
                            if (item != null) setLikeIcon(item.isLiked);
                        }
                    }
                } else {
                    if (!toolbar.getTag().equals(1)) {
                        if (toolbar.getNavigationIcon() != null) {
                            toolbar.getNavigationIcon().setColorFilter
                                    (ContextCompat.getColor(ItemDetailsViewActivity.this, R.color.icons), PorterDuff.Mode.SRC_ATOP);
                            transitionDrawable.reverseTransition(200);
                            toolbar.setTag(1);
                            if (item != null) setLikeIcon(item.isLiked);
                        }
                    }
                }
            }
        });
    }

    private void initSimilarItemsRecycler() {
        similarItemsAdapter = new ItemsRecyclerAdapter(ItemsRecyclerAdapter.MODE_LINEAR_HORIZONTAL);
        similarItemsAdapter.setOnItemClickListener(new ItemsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                launchItemDetailsActivity((Item) similarItemsAdapter.getItems().get(position));
            }

            @Override
            public void onLikeClicked(int position) {
                ItemDetailsPresenter.getInstance(ItemDetailsViewActivity.this).onSimilarItemLikeClicked(similarItemsAdapter, position, item);
            }

            @Override
            public void onEditClicked(int position) {

            }

            @Override
            public void onDeleteClicked(int position) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        similarItemsRecycler.setLayoutManager(layoutManager);
        similarItemsRecycler.setAdapter(similarItemsAdapter);
    }

    private void initReviewsRecycler() {
        reviewsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        reviewsRecyclerAdapter = new ReviewsRecyclerAdapter(ReviewsRecyclerAdapter.MODE_ITEM_DETAILS);
        reviewsRecyclerAdapter.setListener(new ReviewsRecyclerAdapter.OnReviewClickListener() {
            @Override
            public void onUserClicked(int position) {
                launchProfileActivity(reviewsRecyclerAdapter.getReviews().get(position).userId);
            }
        });
        reviewsRecycler.setAdapter(reviewsRecyclerAdapter);
    }

    private void initCalendar() {
        calendarView.setPage(AvailabilityCalendar.PAGE_CENTER);
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                ItemDetailsPresenter.getInstance(ItemDetailsViewActivity.this).onCalendarDayClicked(
                        dateClicked.getTime(),
                        (calendarView.getEvents(dateClicked) == null || calendarView.getEvents(dateClicked).isEmpty()),
                        bookings);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                ItemDetailsPresenter.getInstance(ItemDetailsViewActivity.this).onMonthChange(calendarView, firstDayOfNewMonth.getTime());
            }
        });
        calendarView.setLocale(TimeZone.getDefault(), Locale.ENGLISH);
        calendarView.setUseThreeLetterAbbreviation(true);

        calendarButtonSwipeMonthLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemDetailsPresenter.getInstance(ItemDetailsViewActivity.this).onMonthChangeButtonLeftClicked(calendarView, calendarView.getFirstDayOfCurrentMonth().getTime());
            }
        });

        calendarButtonSwipeMonthRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemDetailsPresenter.getInstance(ItemDetailsViewActivity.this).onMonthChangeButtonRightClicked(calendarView, calendarView.getFirstDayOfCurrentMonth().getTime());
            }
        });
    }

    private void initBottomSheet() {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                ItemDetailsPresenter.getInstance(ItemDetailsViewActivity.this).onBottomSheetStateChanged(newState, shadow);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                ItemDetailsPresenter.getInstance(ItemDetailsViewActivity.this).onBottomSheetSlide(slideOffset, shadow);
            }
        });
        bottomSheetBookings.findViewById(R.id.item_details_bottom_sheet_close_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        bottomSheetBookingsAdapter = new BookingsInfoRecyclerAdapter(BookingsInfoRecyclerAdapter.MODE_INFO);
        bottomSheetBookingsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        bottomSheetBookingsRecycler.setAdapter(bottomSheetBookingsAdapter);

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemDetailsPresenter.getInstance(ItemDetailsViewActivity.this).onContactClicked();
            }
        });
    }

    private void initShadow() {
        shadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    void setItem(Item item) {
        this.item = item;
    }

    void setItemPhoto(final ArrayList<String> photos) {
        String photoUrl = null;
        if (photos != null && !photos.isEmpty()) photoUrl = photos.get(0);

        if (photoUrl == null) {
            noItemPhotoLayout.setVisibility(View.VISIBLE);
            photo.setVisibility(View.GONE);
        } else {
            noItemPhotoLayout.setVisibility(View.GONE);
            photo.setVisibility(View.VISIBLE);
            try {
                Glide.with(ApplicationController.getInstance())
                        .load(new URL(photoUrl))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                        .apply(RequestOptions.noTransformation())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                noItemPhotoLayout.setVisibility(View.VISIBLE);
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

            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchFullscreenGallery(photos, 0);
                }
            });
        }
    }

    void setTitle(String title) {
        this.title.setText(title);
    }

    void setPrice(String price) {
        this.price.setText(price);
    }

    void setLocation(String location) {
        this.location.setText(location);
    }

    void setOwner(final User owner) {
        this.owner = owner;
        if (owner != null) {
            this.ownerName.setText(owner.getFullName());
            if (owner.photo != null && !owner.photo.isEmpty()) {
                ownerPhoto.setVisibility(View.VISIBLE);
                noOwnerPhotoLayout.setVisibility(View.GONE);
                try {
                    Glide.with(ApplicationController.getInstance())
                            .load(new URL(owner.photo))
                            .apply(RequestOptions.skipMemoryCacheOf(true))
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                            .apply(RequestOptions.noTransformation())
                            .into(ownerPhoto);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                ownerPhoto.setVisibility(View.GONE);
                noOwnerPhotoLayout.setVisibility(View.VISIBLE);
            }
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ItemDetailsViewActivity.this.owner != null && ItemDetailsViewActivity.this.owner.id != null)
                    launchProfileActivity(ItemDetailsViewActivity.this.owner.id);
            }
        };
        noOwnerPhotoLayout.setOnClickListener(onClickListener);
        ownerPhoto.setOnClickListener(onClickListener);
        ownerName.setOnClickListener(onClickListener);
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

    void setRating(String rating) {
        this.rating.setText(rating);
    }

    void setLoading(boolean isLoading) {
        if (isLoading) loadingLayout.setVisibility(View.VISIBLE);
        else loadingLayout.setVisibility(View.GONE);
    }

    void setBookings(ArrayList<Booking> bookings) {
        this.bookings = bookings;
    }

    void setAddress(String address){
        this.location.setText(address);
    }

    void addBooking(Booking booking) {
        bookings.add(booking);

    }

    Item getItem() {
        return item;
    }

    ItemsRecyclerAdapter getSimilarItemsAdapter() {
        return similarItemsAdapter;
    }

    void setReviews(ArrayList<Review> reviews) {
        reviewsRecyclerAdapter.setReviews(reviews);
        reviewsRecyclerAdapter.notifyItemRangeInserted(0, reviews.size());
    }

    void setSimilarItems(ArrayList<Item> similarItems) {
        ArrayList<Object> items = new ArrayList<>();
        items.addAll(similarItems);

        similarItemsAdapter.setItems(items);
        similarItemsAdapter.notifyItemRangeInserted(0, items.size());
    }

    void setSimilarObjectItems(ArrayList<Object> similarItems) {
        similarItemsAdapter.setItems(similarItems);
        similarItemsAdapter.notifyItemRangeInserted(0, similarItems.size());
    }

    void setEvents(ArrayList<Event> events) {
        calendarView.removeAllEvents();
        calendarView.addEvents(events);
    }

    void addEvents(ArrayList<Event> events) {
        calendarView.addEvents(events);
    }

    void setMonthName(String name) {
        calendarMonthName.setText(name);
    }

    void setCalendarButtonSwipeMonthLeftVisibility(int visibility) {
        calendarButtonSwipeMonthLeft.setVisibility(visibility);
    }

    void setCalendarButtonSwipeMonthRightVisibility(int visibility) {
        calendarButtonSwipeMonthRight.setVisibility(visibility);
    }

    void setContactButtonVisibility(int visibility) {
        contactButton.setVisibility(visibility);
    }

    void openBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    void setBottomSheetDate(String date) {
        bottomSheetDate.setText(date);
    }

    void setBottomSheetBookings(ArrayList<Booking> bookings) {
        bottomSheetBookingsAdapter.setBookings(bookings);
        bottomSheetBookingsAdapter.notifyDataSetChanged();
    }

    void setBottomSheetEmptyMessageVisibility(int visibility) {
        bottomSheetEmptyMessage.setVisibility(visibility);
    }

    void setBottomSheetRecyclerVisibility(int visibility) {
        bottomSheetBookingsRecycler.setVisibility(visibility);
    }

    void setReviewsEmptyMessageVisibility(int visibility) {
        reviewsEmptyMessage.setVisibility(visibility);
    }

    void setReviewsListVisibility(int visibility) {
        reviewsRecycler.setVisibility(visibility);
    }

    void setSimilarItemsEmptyMessageVisibility(int visibility) {
        similarItemsEmptyMessage.setVisibility(visibility);
    }

    void setSimilarItemsRecyclerVisibility(int visibility) {
        similarItemsRecycler.setVisibility(visibility);
    }

    void setLikeIcon(boolean isLiked) {
        item.isLiked = isLiked;

        if (isLiked) toolbarLikeIcon = R.drawable.ic_favorite_red_24px;
        else {
            if (toolbar.getTag().equals(1))
                toolbarLikeIcon = R.drawable.ic_favorite_border_white_24px;
            else toolbarLikeIcon = R.drawable.ic_favorite_border_black_24px;
        }
        invalidateOptionsMenu();
    }

    void showError() {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT);
        toast.show();
    }

    void launchFirstContactActivity() {
        Intent intent = new Intent(ItemDetailsViewActivity.this, ContactActivity.class);
        intent.putExtra("bookings", Parcels.wrap(bookings));
        intent.putExtra("item", Parcels.wrap(item));
        intent.putExtra("itemOwner", Parcels.wrap(owner));
        startActivityForResult(intent, RequestCodes.REQUEST_CODE_EDIT);
    }

    void launchFullscreenGallery(ArrayList<String> images, int position) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("images", images);
        bundle.putInt("position", position);

        GalleryFragment newFragment = GalleryFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "fullscreenGallery");
    }

    void launchItemDetailsActivity(Item itemBase) {
        Intent intent = new Intent(this, ItemDetailsViewActivity.class);
        intent.putExtra("item", Parcels.wrap(itemBase));
        startActivity(intent);
    }

    void launchProfileActivity(String userId) {
        Intent intent = new Intent(this, ProfileViewActivity.class);
        intent.putExtra("userId", userId);
        startActivityForResult(intent, RequestCodes.REQUEST_CODE_DETAILS);
    }
}
