package boyko.alex.easy_way.frontend.explore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;

import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.frontend.item.item_details.ItemDetailsViewActivity;
import boyko.alex.easy_way.frontend.profile.EditProfileViewActivity;
import boyko.alex.easy_way.frontend.search.SearchViewActivity;
import boyko.alex.easy_way.R;

/**
 * Created by Sasha on 01.11.2017.
 * <p>
 * This is Main App Activity. Welcome.
 */

public class ExploreViewActivity extends AppCompatActivity {
    private final String LOG_TAG = getClass().getSimpleName();

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private RecyclerView recyclerView;
    private ItemsRecyclerAdapter adapter;
    private StaggeredGridLayoutManager layoutManager;

    private BottomSheetBehavior bottomSheetFiltersBehavior;
    private View bottomSheetFilters, shadow;
    private AppCompatImageView openBottomSheetButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_explore);

        init();

        ExplorePresenter.getInstance(this).startLoading();
        testDB();
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
            case R.id.explore_search:
                ExplorePresenter.getInstance(ExploreViewActivity.this).onSearchClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (ExplorePresenter.getInstance(this).onBackPressed()) {
            super.onBackPressed();
        }
    }

    private void init() {
        initViews();
        initToolbar();
        initNavigationView();
        initDrawerToggle();
        initAdapter();
        initRecyclerView();
        initFiltersBottomSheet();
        initShadow();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.explore_drawer);
        toolbar = findViewById(R.id.explore_toolbar);
        recyclerView = findViewById(R.id.explore_recycler_view);
        //searchEditText = findViewById(R.id.explore_search_edit_text);
        bottomSheetFilters = findViewById(R.id.explore_bottom_sheet_filters);
        bottomSheetFiltersBehavior = BottomSheetBehavior.from(bottomSheetFilters);
        shadow = findViewById(R.id.explore_shadow);
        openBottomSheetButton = bottomSheetFilters.findViewById(R.id.explore_bottom_sheet_open_icon);
        //cleatSearchButton = findViewById(R.id.explore_clear_search);
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
    }

    private void initAdapter() {
        adapter = new ItemsRecyclerAdapter(ItemsRecyclerAdapter.MODE_GRID);
        adapter.setOnItemClickListener(new ItemsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onItemClicked((Item) adapter.getItems().get(position));
            }
        });
    }

    private void initRecyclerView() {
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                layoutManager.invalidateSpanAssignments();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                ExplorePresenter.getInstance(ExploreViewActivity.this).onListScrolled(layoutManager, dy);
            }
        });
//        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
//        if (animator instanceof SimpleItemAnimator) {
//            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
//        }
    }

//    private void initSearchEditText() {
//        searchEditText.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        searchEditText.setCursorVisible(true);
//                        bottomSheetSearchBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                        break;
//                }
//                return false;
//            }
//        });
//        cleatSearchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                searchEditText.setText("");
//            }
//        });
//    }

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

        findViewById(R.id.explore_navigation_view).findViewById(R.id.nav_header_edit_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExplorePresenter.getInstance(ExploreViewActivity.this).onProfileEditClicked();
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

    /**
     * Init drawer toggle to animate toolbar hamburger
     */
    private void initDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    void setItems(ArrayList<Object> items) {
        adapter.setItems(items);
        adapter.notifyItemRangeChanged(0, items.size());
    }

    void addItems(ArrayList<Object> items) {
        adapter.getItems().remove(adapter.getItemCount() - 1);
        adapter.notifyItemRemoved(adapter.getItemCount());
        adapter.addItems(items);
        adapter.notifyItemRangeInserted(adapter.getItemCount() - items.size(), items.size());
    }

//    void hideKeyboard() {
//        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        //Find the currently focused view, so we can grab the correct window token from it.
//        View view = this.getCurrentFocus();
//        //If no view currently has focus, create a new one, just so we can grab a window token from it
//        if (view == null) {
//            view = new View(this);
//        }
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }

    View shadow() {
        return shadow;
    }

    BottomSheetBehavior bottomSheetBehavior() {
        return bottomSheetFiltersBehavior;
    }

//    View bottomSheet(){
//        return bottomSheetFilters;
//    }

    AppCompatImageView openBottomSheetButton() {
        return openBottomSheetButton;
    }

    DrawerLayout drawerLayout() {
        return drawerLayout;
    }

    void launchSearchActivity() {
        Intent intent = new Intent(this, SearchViewActivity.class);
        startActivity(intent);
    }

    void launchItemDetailsActivity(Item itemBase) {
        Intent intent = new Intent(this, ItemDetailsViewActivity.class);
        intent.putExtra("item", Parcels.wrap(itemBase));
        startActivity(intent);
    }

    void launchEditProfileActivity() {
        Intent intent = new Intent(this, EditProfileViewActivity.class);
        startActivity(intent);
    }

    void testDB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("items")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (DocumentSnapshot document : task.getResult()) {
////                                document.getDocumentReference("category").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
////                                    @Override
////                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
////                                        Log.d(LOG_TAG, task.getResult().getData() +"");
////                                    }
////                                });
//                                Log.d(LOG_TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.w(LOG_TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });

        CollectionReference collectionReference = db.collection("items");
        Query query = collectionReference
                .whereEqualTo("address.name", "Lublin")
                .whereLessThan("address.x", 60)
                .whereGreaterThan("address.x", 30);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d(LOG_TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.w(LOG_TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }
}
