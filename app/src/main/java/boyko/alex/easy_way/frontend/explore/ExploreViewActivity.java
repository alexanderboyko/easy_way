package boyko.alex.easy_way.frontend.explore;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.parceler.Parcels;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.frontend.bookings.BookingsViewActivity;
import boyko.alex.easy_way.frontend.dialogs.CategorySelectFragmentView;
import boyko.alex.easy_way.frontend.inbox.InboxViewActivity;
import boyko.alex.easy_way.frontend.item.item_details.ItemDetailsViewActivity;
import boyko.alex.easy_way.frontend.item.item_edit.AddItemViewActivity;
import boyko.alex.easy_way.frontend.liked_items.LikedItemsViewActivity;
import boyko.alex.easy_way.frontend.my_offers.MyOffersViewActivity;
import boyko.alex.easy_way.frontend.profile.details.ProfileViewActivity;
import boyko.alex.easy_way.frontend.settings.SettingsActivityView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sasha on 01.11.2017.
 * <p>
 * This is Main App Activity. Welcome.
 */

public class ExploreViewActivity extends AppCompatActivity {
    //private final String LOG_TAG = getClass().getSimpleName();

    private Toast toast;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ItemsRecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private BottomSheetBehavior bottomSheetFiltersBehavior;
    private View bottomSheetFilters, shadow;
    private AppCompatImageView openBottomSheetButton;

    private ProgressBar progressBar;
    private TextView noItemsMessage;

    //bottom sheet
    private TextView filtersInfo, filtersViewCardsLabel, filtersViewListLabel;
    private AppCompatImageView filtersLocationInfoIcon, filtersInfoIcon, filtersAddressClear, filtersPriceClear, filtersViewCardsIcon, filtersViewListIcon;
    private TextInputEditText filtersCategory, filtersAddress, filtersPriceFrom, filtersPriceTo, filtersOrder;
    private LinearLayout filtersViewCards, filtersViewList;

    //navigationview
    private TextView userFullName, userEmail, inboxUnreadMessages;
    private CircleImageView userPhoto;
    private LinearLayout userNoPhotoLayout;
    private RelativeLayout userLayout;

    private Thread thread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_explore);

        init();

        ExplorePresenter.getInstance(this).onCreate(savedInstanceState);
        //testDB();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ExplorePresenter.getInstance(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.explore_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.explore_menu_search:
//                ExplorePresenter.getInstance(ExploreViewActivity.this).onSearchClicked();
//                return true;
            case R.id.explore_menu_refresh:
                ExplorePresenter.getInstance(ExploreViewActivity.this).startLoading();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ExplorePresenter.getInstance(this).onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (ExplorePresenter.getInstance(this).onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            thread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void init() {
        initViews();
        initToolbar();
        initNavigationView();
        initDrawerToggle();
        initAdapter();
        initLinearLayoutManager();
        initRecyclerView();
        initFiltersBottomSheet();
        initShadow();
        initSwipeRefreshLayout();
        initUser();
        initAutoUpdateThread();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.explore_drawer);
        toolbar = findViewById(R.id.explore_toolbar);
        recyclerView = findViewById(R.id.explore_recycler_view);
        bottomSheetFilters = findViewById(R.id.explore_bottom_sheet_filters);
        bottomSheetFiltersBehavior = BottomSheetBehavior.from(bottomSheetFilters);
        shadow = findViewById(R.id.explore_shadow);
        openBottomSheetButton = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_open_icon);
        swipeRefreshLayout = findViewById(R.id.explore_swipe_to_refresh);
        progressBar = findViewById(R.id.explore_progress_bar);
        noItemsMessage = findViewById(R.id.explore_no_items);

        filtersInfo = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_categories_info);
        filtersLocationInfoIcon = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_location_info);
        filtersInfoIcon = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_filters_info);
        filtersAddressClear = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_clear_address);
        filtersPriceClear = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_clear_price);
        filtersCategory = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_category);
        filtersAddress = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_city);
        filtersPriceFrom = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_price_from);
        filtersPriceTo = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_price_to);
        filtersOrder = bottomSheetFilters.findViewById(R.id.explore_bottom_order);
        filtersViewCards = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_view_cards);
        filtersViewList = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_view_list);
        filtersViewCardsIcon = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_view_cards_icon);
        filtersViewListIcon = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_view_list_icon);
        filtersViewCardsLabel = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_view_cards_label);
        filtersViewListLabel = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_view_list_label);

        userFullName = findViewById(R.id.explore_navigation_view).findViewById(R.id.nav_header_username);
        userEmail = findViewById(R.id.explore_navigation_view).findViewById(R.id.nav_header_email);
        userLayout = findViewById(R.id.explore_navigation_view).findViewById(R.id.nav_header_layout);
        userNoPhotoLayout = findViewById(R.id.explore_navigation_view).findViewById(R.id.nav_header_no_photo_layout);
        userPhoto = findViewById(R.id.explore_navigation_view).findViewById(R.id.nav_header_user_photo);
        inboxUnreadMessages = findViewById(R.id.explore_navigation_view).findViewById(R.id.nav_inbox_count);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void initFiltersBottomSheet() {
        bottomSheetFiltersBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onBottomSheetStateChanged(newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onBottomSheetSlide(slideOffset);
            }
        });

        View.OnClickListener onOpenBottomSheetListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onBottomSheetInfoPanelClicked();
            }
        };

        openBottomSheetButton.setOnClickListener(onOpenBottomSheetListener);
        //always showing layout of bottom sheet (info bottom sheet layout)
        bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_info_layout).setOnClickListener(onOpenBottomSheetListener);

        filtersCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onFiltersCategoryClicked();
            }
        });

        filtersAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSelectAddressActivity();
            }
        });
        filtersAddressClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onFiltersClearAddressClicked();
            }
        });

        filtersViewCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onViewChangedToCards(adapter.getMode());
            }
        });
        filtersViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onViewChangedToList(adapter.getMode());
            }
        });

        filtersPriceClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onFiltersClearPrice();
            }
        });

        filtersPriceFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filtersPriceFrom.setError(null);
                filtersPriceTo.setError(null);
                ExplorePresenter.getInstance(ExploreViewActivity.this).onPriceChanged(
                        filtersPriceFrom.getText().toString(),
                        filtersPriceTo.getText().toString());
            }
        });
        filtersPriceTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filtersPriceFrom.setError(null);
                filtersPriceTo.setError(null);
                ExplorePresenter.getInstance(ExploreViewActivity.this).onPriceChanged(
                        filtersPriceFrom.getText().toString(),
                        filtersPriceTo.getText().toString());
            }
        });
        filtersOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onFiltersOrderClicked(filtersOrder);
            }
        });
    }

    private void initAdapter() {
        adapter = new ItemsRecyclerAdapter(ItemsRecyclerAdapter.MODE_GRID);
        adapter.setItems(new ArrayList<>());
        adapter.setOnItemClickListener(new ItemsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onItemClicked((Item) adapter.getItems().get(position));
            }

            @Override
            public void onLikeClicked(int position) {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onLikeClicked(((Item) adapter.getItems().get(position)).id);

                ((Item) adapter.getItems().get(position)).isLiked = !((Item) adapter.getItems().get(position)).isLiked;
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onEditClicked(int position) {

            }

            @Override
            public void onDeleteClicked(int position) {

            }
        });
    }

    private void initLinearLayoutManager() {
        if (adapter.getMode() == ItemsRecyclerAdapter.MODE_GRID) {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            ((StaggeredGridLayoutManager) layoutManager).setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        } else {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
    }

    private void initRecyclerView() {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (adapter.getMode() == ItemsRecyclerAdapter.MODE_GRID)
                    ((StaggeredGridLayoutManager) layoutManager).invalidateSpanAssignments();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (adapter.getMode() == ItemsRecyclerAdapter.MODE_GRID)
                    ExplorePresenter.getInstance(ExploreViewActivity.this).onListScrolled(((StaggeredGridLayoutManager) layoutManager), dy);
                else
                    ExplorePresenter.getInstance(ExploreViewActivity.this).onListScrolled(((LinearLayoutManager) layoutManager), dy);
            }
        });
    }

    private void initNavigationView() {
        drawerLayout.setScrimColor(ContextCompat.getColor(this, R.color.white_70_opacity));
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onDrawerStateChanged(newState);
            }
        });

        findViewById(R.id.explore_navigation_view).findViewById(R.id.nav_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAddItemActivity();
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        findViewById(R.id.explore_navigation_view).findViewById(R.id.nav_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchProfileActivity();
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        findViewById(R.id.explore_navigation_view).findViewById(R.id.nav_inbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchInboxActivity();
                drawerLayout.closeDrawer(Gravity.START);
                inboxUnreadMessages.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.explore_navigation_view).findViewById(R.id.nav_my_offers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMyOffersActivity();
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        findViewById(R.id.explore_navigation_view).findViewById(R.id.nav_bookings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBookingsActivity();
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        findViewById(R.id.explore_navigation_view).findViewById(R.id.nav_saved).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSavedItemsActivity();
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        findViewById(R.id.explore_navigation_view).findViewById(R.id.nav_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSettingsActivity();
                drawerLayout.closeDrawer(Gravity.START);
            }
        });


    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onRefresh();
            }
        });
    }

    private void initShadow() {
        shadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onShadowClicked();
            }
        });
    }

    void initUser() {
        User user = DataMediator.getUser();

        userFullName.setText(user.getFullName());
        userEmail.setText(user.email);
        if (user.photo != null) {
            userPhoto.setVisibility(View.VISIBLE);
            userNoPhotoLayout.setVisibility(View.GONE);
            try {
                Glide.with(ApplicationController.getInstance())
                        .load(new URL(user.photo))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                        .apply(RequestOptions.noTransformation())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                userPhoto.setVisibility(View.GONE);
                                userNoPhotoLayout.setVisibility(View.VISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(userPhoto);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            userPhoto.setVisibility(View.GONE);
            userNoPhotoLayout.setVisibility(View.VISIBLE);
        }

        View.OnClickListener onProfileClicked = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchProfileActivity();
                drawerLayout.closeDrawer(Gravity.START);
            }
        };
        userLayout.setOnClickListener(onProfileClicked);
        userNoPhotoLayout.setOnClickListener(onProfileClicked);
        userPhoto.setOnClickListener(onProfileClicked);
    }

    private void initAutoUpdateThread() {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(5000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ExplorePresenter.getInstance(ExploreViewActivity.this).checkNewMessages();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    /**
     * Init drawer toggle to animate toolbar hamburger
     */
    private void initDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    void setItems(ArrayList<Object> items) {
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    void addItems(ArrayList<Object> items) {
        adapter.getItems().remove(adapter.getItemCount() - 1);
        adapter.notifyItemRemoved(adapter.getItemCount());
        adapter.addItems(items);
        adapter.notifyItemRangeInserted(adapter.getItemCount() - items.size(), items.size());
    }

    ArrayList<Object> getItems() {
        return adapter.getItems();
    }

    ItemsRecyclerAdapter getAdapter() {
        return adapter;
    }

    int getMode() {
        return adapter.getMode();
    }

    View shadow() {
        return shadow;
    }

    void setUnreadMessagesCount(String count) {
        if (count == null) inboxUnreadMessages.setVisibility(View.GONE);
        else {
            inboxUnreadMessages.setVisibility(View.VISIBLE);
            inboxUnreadMessages.setText(count);
        }
    }

    BottomSheetBehavior bottomSheetBehavior() {
        return bottomSheetFiltersBehavior;
    }

    AppCompatImageView openBottomSheetButton() {
        return openBottomSheetButton;
    }

    DrawerLayout drawerLayout() {
        return drawerLayout;
    }


    /**
     * FILTERS CHANGE METHODS
     */

    void setFilterCategory(String category) {
        filtersCategory.setText(category);
        filtersInfo.setText(category);
    }

    void setFilterAddress(String address) {
        filtersAddress.setText(address);
        filtersAddressClear.setVisibility(View.VISIBLE);
        DrawableCompat.setTint(filtersLocationInfoIcon.getDrawable(), ContextCompat.getColor(this, R.color.color_accent));
    }

    void setFiltersPriceIcon(boolean active) {
        if (active) {
            filtersPriceClear.setVisibility(View.VISIBLE);
            DrawableCompat.setTint(filtersInfoIcon.getDrawable(), ContextCompat.getColor(ExploreViewActivity.this, R.color.color_accent));
        } else {
            filtersPriceClear.setVisibility(View.INVISIBLE);
            DrawableCompat.setTint(filtersInfoIcon.getDrawable(), ContextCompat.getColor(ExploreViewActivity.this, R.color.primary_light));
        }
    }

    void setFiltersViewCards() {
        adapter.setMode(ItemsRecyclerAdapter.MODE_GRID);
        initLinearLayoutManager();
        recyclerView.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();

        DrawableCompat.setTint(filtersViewCardsIcon.getDrawable(), ContextCompat.getColor(ExploreViewActivity.this, R.color.color_accent));
        filtersViewCardsLabel.setTextColor(ContextCompat.getColor(this, R.color.color_accent));

        DrawableCompat.setTint(filtersViewListIcon.getDrawable(), ContextCompat.getColor(ExploreViewActivity.this, R.color.primary_text));
        filtersViewListLabel.setTextColor(ContextCompat.getColor(this, R.color.primary_text));
    }

    void setFiltersViewList() {
        adapter.setMode(ItemsRecyclerAdapter.MODE_LINEAR_VERTICAL);
        initLinearLayoutManager();
        recyclerView.setLayoutManager(layoutManager);
        adapter.notifyDataSetChanged();

        DrawableCompat.setTint(filtersViewCardsIcon.getDrawable(), ContextCompat.getColor(ExploreViewActivity.this, R.color.primary_text));
        filtersViewCardsLabel.setTextColor(ContextCompat.getColor(this, R.color.primary_text));

        DrawableCompat.setTint(filtersViewListIcon.getDrawable(), ContextCompat.getColor(ExploreViewActivity.this, R.color.color_accent));
        filtersViewListLabel.setTextColor(ContextCompat.getColor(this, R.color.color_accent));
    }

    void setFiltersPriceToError(String error) {
        filtersPriceTo.setError(error);
    }

    void clearFiltersAddress() {
        filtersAddress.setText("");
        filtersAddressClear.setVisibility(View.INVISIBLE);
        DrawableCompat.setTint(filtersLocationInfoIcon.getDrawable(), ContextCompat.getColor(this, R.color.primary_light));
    }

    void clearFiltersPrices() {
        filtersPriceFrom.setText("");
        filtersPriceTo.setText("");
        filtersPriceClear.setVisibility(View.INVISIBLE);
        DrawableCompat.setTint(filtersInfoIcon.getDrawable(), ContextCompat.getColor(this, R.color.primary_light));
    }

    void setFilterOrder(String order) {
        filtersOrder.setText(order);
    }

    /**
     * END OF FILTERS CHANGE METHODS
     */

    void showError(String error) {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(this, error, Toast.LENGTH_LONG);
        toast.show();
    }

    void setLoading(boolean isLoading) {
        if (isLoading) {
            shadow.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            if (bottomSheetFiltersBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                shadow.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    void setNoItemsMessageVisibility(int visibility) {
        noItemsMessage.setVisibility(visibility);
    }

    /**
     * LAUNCHING ACTIVITIES AND DIALOGS
     */

    void launchSelectAddressActivity() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .setCountry("PL")
                    .build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(ExploreViewActivity.this);
            startActivityForResult(intent, RequestCodes.REQUEST_CODE_ADDRESS);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void launchCategorySelectDialog() {
        Intent intent = new Intent(ExploreViewActivity.this, CategorySelectFragmentView.class);
        intent.putExtra("allCategories", true);
        startActivityForResult(intent, RequestCodes.REQUEST_CODE_SELECT);
    }

    void launchItemDetailsActivity(Item itemBase) {
        Intent intent = new Intent(this, ItemDetailsViewActivity.class);
        intent.putExtra("item", Parcels.wrap(itemBase));
        startActivityForResult(intent, RequestCodes.REQUEST_CODE_DETAILS);
    }

    void launchAddItemActivity() {
        Intent intent = new Intent(this, AddItemViewActivity.class);
        startActivityForResult(intent, RequestCodes.REQUEST_CODE_EDIT);
    }

    void launchSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivityView.class);
        startActivityForResult(intent, RequestCodes.REQUEST_CODE_SETTINGS);
    }

    void launchProfileActivity() {
        Intent intent = new Intent(this, ProfileViewActivity.class);
        intent.putExtra("userId", DataMediator.getUser().id);
        startActivityForResult(intent, RequestCodes.REQUEST_CODE_DETAILS);
    }

    void launchInboxActivity() {
        Intent intent = new Intent(this, InboxViewActivity.class);
        startActivity(intent);
    }

    void launchMyOffersActivity() {
        Intent intent = new Intent(this, MyOffersViewActivity.class);
        startActivityForResult(intent, RequestCodes.REQUEST_CODE_EDIT);
    }

    void launchBookingsActivity() {
        Intent intent = new Intent(this, BookingsViewActivity.class);
        startActivity(intent);
    }

    void launchSavedItemsActivity() {
        Intent intent = new Intent(this, LikedItemsViewActivity.class);
        startActivityForResult(intent, RequestCodes.REQUEST_CODE_DETAILS);
    }
}
