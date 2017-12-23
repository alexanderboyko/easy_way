package boyko.alex.easy_way.frontend.liked_items;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
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

    private boolean isNextItemLoading = false;
    private int itemsLoaded = 0;
    private int itemsToLoad = 0;
    private ArrayList<Item> tempLoadedItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_liked_items);

        init();
        startLoading();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    private void init() {
        initViews();
        initToolbar();
        initRecyclerView();
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

    private void initRecyclerView() {
        if(DataMediator.getLikes().size() == 0){
            showEmptyMessage();
        }else {
            adapter = new ItemsRecyclerAdapter(ItemsRecyclerAdapter.MODE_GRID);
            adapter.setOnItemClickListener(new ItemsRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(int position) {
                    launchItemDetailsActivity((Item) adapter.getItems().get(position));
                }

                @Override
                public void onLikeClicked(int position) {

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
    }

    private void showEmptyMessage(){
        emptyMessage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    void startLoading() {
        isNextItemLoading = true;
        ArrayList<Like> likes = DataMediator.getLikes();
        if (likes.size() >= 20) {
            itemsToLoad = 20;
            loadByLikes(new ArrayList<>(likes.subList(0, 20)));
        } else {
            itemsToLoad = likes.size();
            loadByLikes(likes);
        }
    }

    void startLoadingNext() {
        if (!isNextItemLoading) {
            ArrayList<Like> likes = DataMediator.getLikes();
            if (likes.size() > 20) {
                if (adapter.getItemCount() + 20 >= likes.size()) {
                    isNextItemLoading = true;
                    itemsToLoad = 20;
                    loadByLikes(new ArrayList<>(likes.subList(adapter.getItemCount(), adapter.getItemCount() + 20)));
                } else {
                    isNextItemLoading = true;
                    itemsToLoad = likes.size() - adapter.getItemCount();
                    loadByLikes(new ArrayList<>(likes.subList(adapter.getItemCount(), likes.size())));
                }
            }
        }
    }

    private void loadByLikes(ArrayList<Like> likes) {
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
                                item.isLiked = true;
                                tempLoadedItems.add(item);
                            }
                        }

                        itemsLoaded++;
                        if (itemsLoaded == itemsToLoad) {
                            loadingFinished();
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

        if (items.size() >= 20) {
            items.add("loading");
        }
        adapter.addItems(items);
        adapter.notifyItemRangeInserted(adapter.getItemCount(), items.size());

        isNextItemLoading = false;
    }

    void launchItemDetailsActivity(Item item) {
        Intent intent = new Intent(this, ItemDetailsViewActivity.class);
        intent.putExtra("item", Parcels.wrap(item));
        startActivity(intent);
    }
}
