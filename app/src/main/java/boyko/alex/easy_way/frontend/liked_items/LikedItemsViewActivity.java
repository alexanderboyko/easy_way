package boyko.alex.easy_way.frontend.liked_items;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.util.ArrayList;

import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.Like;
import boyko.alex.easy_way.frontend.explore.ItemsRecyclerAdapter;
import boyko.alex.easy_way.frontend.item.item_details.ItemDetailsViewActivity;
import boyko.alex.easy_way.frontend.item.item_edit.DataFragment;

/**
 * Created by Sasha on 19.12.2017.
 * <p>
 * List of liked items
 */

public class LikedItemsViewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ItemsRecyclerAdapter adapter;
    private TextView emptyMessage;

    private boolean isNextItemLoading = false, allDataLoaded = false;
    private int itemsLoaded = 0;
    private int itemsToLoad = 0;
    private ArrayList<Item> tempLoadedItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_liked_items);

        if (savedInstanceState == null) {
            init();
            startLoading();
        } else {
            isNextItemLoading = savedInstanceState.getBoolean("isNextItemLoading");
            allDataLoaded = savedInstanceState.getBoolean("allDataLoaded");
            itemsLoaded = savedInstanceState.getInt("itemsLoaded");
            itemsToLoad = savedInstanceState.getInt("itemsToLoad");

            ArrayList<Object> items = ((DataFragment) getSupportFragmentManager().findFragmentByTag("dataFragment")).getItems();

            initViews();
            initToolbar();
            initRecyclerView(items);

            if(allDataLoaded && items.size() == 0){
                showEmptyMessage();
            }

            if((items.isEmpty() || isNextItemLoading) && !allDataLoaded) {
                itemsToLoad = 0;
                itemsLoaded = 0;
                startLoading();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isNextItemLoading", isNextItemLoading);
        outState.putBoolean("allDataLoaded", allDataLoaded);
        outState.putInt("itemsLoaded", itemsLoaded);
        outState.putInt("itemsToLoad", itemsToLoad);

        if (getSupportFragmentManager().findFragmentByTag("dataFragment") != null) {
            DataFragment dataFragment = (DataFragment) getSupportFragmentManager().findFragmentByTag("dataFragment");
            dataFragment.setItems(adapter.getItems());
        } else {
            DataFragment fragment = new DataFragment();
            fragment.setItems(adapter.getItems());

            getSupportFragmentManager().beginTransaction().add(fragment, "dataFragment").commit();
        }
        super.onSaveInstanceState(outState);
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
        initRecyclerView(null);
    }

    private void initViews() {
        toolbar = findViewById(R.id.liked_toolbar);
        recyclerView = findViewById(R.id.liked_recycler);
        emptyMessage = findViewById(R.id.liked_empty);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.saved_items));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initRecyclerView(ArrayList<Object> items) {
        adapter = new ItemsRecyclerAdapter(ItemsRecyclerAdapter.MODE_GRID);
        if (items != null) {
            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
        adapter.setOnItemClickListener(new ItemsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                launchItemDetailsActivity((Item) adapter.getItems().get(position));
            }

            @Override
            public void onLikeClicked(int position) {
                LikedItemsViewActivity.this.onLikeClicked(position);
            }

            @Override
            public void onEditClicked(int position) {

            }

            @Override
            public void onDeleteClicked(int position) {

            }
        });

        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (adapter.getMode() == ItemsRecyclerAdapter.MODE_GRID)
                    layoutManager.invalidateSpanAssignments();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && !isNextItemLoading) {
                    int array[] = new int[2];
                    array = layoutManager.findFirstVisibleItemPositions(array);
                    if (array[0] + layoutManager.getChildCount() >= layoutManager.getItemCount()
                            || array[1] + layoutManager.getChildCount() >= layoutManager.getItemCount()) {
                        startLoadingNext();
                    }
                }
            }
        });

    }

    private void showEmptyMessage() {
        recyclerView.setVisibility(View.GONE);
        emptyMessage.setVisibility(View.VISIBLE);
    }

    void startLoading() {
        isNextItemLoading = true;
        ArrayList<Like> likes = DataMediator.getLikes();
        if (likes.size() >= 20) {
            itemsToLoad = 20;
            loadByLikes(new ArrayList<>(likes.subList(0, 20)), false);
        } else {
            itemsToLoad = likes.size();
            loadByLikes(likes, false);
        }
    }

    void startLoadingNext() {
        if (!isNextItemLoading) {
            ArrayList<Like> likes = DataMediator.getLikes();
            if (likes.size() > 20) {
                if (adapter.getItemCount() + 20 >= likes.size()) {
                    isNextItemLoading = true;
                    itemsToLoad = 20;
                    loadByLikes(new ArrayList<>(likes.subList(adapter.getItemCount(), adapter.getItemCount() + 20)), true);
                } else {
                    isNextItemLoading = true;
                    itemsToLoad = likes.size() - adapter.getItemCount();
                    loadByLikes(new ArrayList<>(likes.subList(adapter.getItemCount(), likes.size())), true);
                }
            }
        }
    }

    private void loadByLikes(ArrayList<Like> likes, final boolean isLoadingNext) {
        tempLoadedItems = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (Like like : likes) {
            if (like != null && like.itemId != null) {
                DocumentReference docRef = db.collection("items").document(like.itemId);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                Item item = ConvertHelper.convertToItem(task.getResult());
                                if (item != null) {
                                    item.isLiked = true;
                                    tempLoadedItems.add(item);
                                }
                            }
                        }

                        itemsLoaded++;
                        if (itemsLoaded == itemsToLoad) {
                            if (isLoadingNext) loadingNextFinished();
                            else loadingFinished();
                        }
                    }
                });
            } else {
                itemsLoaded++;
            }
        }
    }

    private void loadingFinished() {
        itemsLoaded = 0;
        itemsToLoad = 0;

        ArrayList<Object> items = new ArrayList<>();
        items.addAll(tempLoadedItems);

        if (items.isEmpty()) {
            showEmptyMessage();
            allDataLoaded = true;
        }
        if (items.size() >= 20) {
            items.add("loading");
        }
        adapter.addItems(items);
        adapter.notifyItemRangeInserted(adapter.getItemCount(), items.size());

        isNextItemLoading = false;
    }

    void loadingNextFinished() {
        itemsLoaded = 0;
        itemsToLoad = 0;

        ArrayList<Object> items = new ArrayList<>();
        items.addAll(tempLoadedItems);

        if (items.isEmpty()) {
            allDataLoaded = true;
        }
        if (items.size() >= 20) {
            items.add("loading");
        }
        adapter.addItems(items);
        adapter.notifyItemRangeInserted(adapter.getItemCount(), items.size());

        isNextItemLoading = false;
    }

    void onLikeClicked(int position) {
        Item item = (Item)adapter.getItems().get(position);

        if (DataMediator.getLike(item.id) == null) {
            final Like like1 = new Like();
            like1.itemId = item.id;
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
            Like like = DataMediator.getLike(item.id);
            if(like != null && like.id != null) {
                DataMediator.removeLike(like.id);
                FirebaseFirestore.getInstance().collection("likes").document(like.id).delete();
            }
        }

        if(item.isLiked){
            adapter.getItems().remove(position);
            adapter.notifyItemRemoved(position);

            if(adapter.getItemCount() == 0) showEmptyMessage();
        }
    }

    void launchItemDetailsActivity(Item item) {
        Intent intent = new Intent(this, ItemDetailsViewActivity.class);
        intent.putExtra("item", Parcels.wrap(item));
        startActivity(intent);
    }
}
