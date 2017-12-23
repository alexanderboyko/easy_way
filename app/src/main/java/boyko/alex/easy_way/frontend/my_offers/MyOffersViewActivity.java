package boyko.alex.easy_way.frontend.my_offers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.frontend.explore.ItemsRecyclerAdapter;
import boyko.alex.easy_way.frontend.item.item_details.ItemDetailsViewActivity;
import boyko.alex.easy_way.frontend.item.item_edit.AddItemViewActivity;

/**
 * Created by Sasha on 20.12.2017.
 * <p>
 * This activity shows user items he offered. He can edit or delete them from this activity (for now only edit and delete).
 */

public class MyOffersViewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ItemsRecyclerAdapter adapter;
    private LinearLayoutManager layoutManager;
    private TextView emptyMessage;
    private LinearLayout loadingLayout;

    private boolean isNextItemLoading = false, allDataLoaded = false;
    private DocumentSnapshot lastLoadedItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_my_offers);

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
        toolbar = findViewById(R.id.my_offers_toolbar);
        recyclerView = findViewById(R.id.my_offers_recycler);
        emptyMessage = findViewById(R.id.my_offers_empty);
        loadingLayout = findViewById(R.id.my_offers_loading_layout);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.my_offers));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initRecyclerView() {
        adapter = new ItemsRecyclerAdapter(ItemsRecyclerAdapter.MODE_MY_OFFERS);
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
                launchEditItemActivity((Item) adapter.getItems().get(position));
            }

            @Override
            public void onDeleteClicked(int position) {
                launchDeleteConfirmationDialog(position);
            }
        });

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && !isNextItemLoading) {
                    if (layoutManager.findFirstVisibleItemPosition() + layoutManager.getChildCount()
                            >= layoutManager.getItemCount()) {
                        isNextItemLoading = true;
                        startLoadingNext();
                    }
                }
            }
        });
    }

    private void showEmptyMessage() {
        emptyMessage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    void startLoading() {
        isNextItemLoading = true;
        loadingLayout.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("items").whereEqualTo("ownerId", DataMediator.getUser().id).limit(20);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() != 0) {
                        lastLoadedItem = task.getResult().getDocuments()
                                .get(task.getResult().size() - 1);


                        ArrayList<Object> items = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            items.add(ConvertHelper.convertToItem(document));
                        }
                        if (items.size() >= 20) items.add("loading");
                        else allDataLoaded = true;

                        adapter.setItems(items);
                        adapter.notifyItemRangeInserted(0, items.size());
                    } else {
                        showEmptyMessage();
                        allDataLoaded = true;
                    }
                }
                isNextItemLoading = false;
                loadingLayout.setVisibility(View.GONE);
            }
        });
    }

    void startLoadingNext() {
        if (!isNextItemLoading && !allDataLoaded) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Query query = db.collection("items").startAfter(lastLoadedItem).whereEqualTo("ownerId", DataMediator.getUser().id).limit(20);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() != 0) {
                            lastLoadedItem = task.getResult().getDocuments()
                                    .get(task.getResult().size() - 1);


                            ArrayList<Object> items = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                items.add(ConvertHelper.convertToItem(document));
                            }
                            if (items.size() >= 20) items.add("loading");
                            else allDataLoaded = true;

                            adapter.getItems().remove(adapter.getItemCount() - 1);
                            adapter.notifyItemRemoved(adapter.getItemCount());
                            adapter.addItems(items);
                            adapter.notifyItemRangeInserted(adapter.getItemCount(), items.size());
                        } else {
                            allDataLoaded = true;
                        }
                    }
                    isNextItemLoading = false;
                }
            });
        }
    }

    void deleteItem(final int itemPosition) {
        final Item item = (Item) adapter.getItems().get(itemPosition);
        if (item != null && item.id != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("items").document(item.id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loadingLayout.setVisibility(View.GONE);
                            adapter.getItems().remove(itemPosition);
                            adapter.notifyItemRemoved(itemPosition);

                            if(adapter.getItemCount() == 0) showEmptyMessage();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingLayout.setVisibility(View.GONE);
                            Toast.makeText(MyOffersViewActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            loadingLayout.setVisibility(View.GONE);
            Toast.makeText(MyOffersViewActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
        }
    }

    void deleteLikes(String itemId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("likes");
    }

    /**
     * Delete all documents in a collection. Uses an Executor to perform work on a background
     * thread. This does *not* automatically discover and delete subcollections.
     */
    private Task<Void> deleteCollection(final CollectionReference collection,
                                        final int batchSize,
                                        Executor executor,
                                        final String itemId) {

        // Perform the delete operation on the provided Executor, which allows us to use
        // simpler synchronous logic without blocking the main thread.
        return Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Get the first batch of documents in the collection
                Query query = collection.whereEqualTo("itemId", itemId).orderBy(FieldPath.documentId()).limit(batchSize);

                // Get a list of deleted documents
                List<DocumentSnapshot> deleted = deleteQueryBatch(query);

                // While the deleted documents in the last batch indicate that there
                // may still be more documents in the collection, page down to the
                // next batch and delete again
                while (deleted.size() >= batchSize) {
                    // Move the query cursor to start after the last doc in the batch
                    DocumentSnapshot last = deleted.get(deleted.size() - 1);
                    query = collection.orderBy(FieldPath.documentId())
                            .startAfter(last.getId())
                            .limit(batchSize);

                    deleted = deleteQueryBatch(query);
                }

                return null;
            }
        });

    }

    /**
     * Delete all results from a query in a single WriteBatch. Must be run on a worker thread
     * to avoid blocking/crashing the main thread.
     */
    @WorkerThread
    private List<DocumentSnapshot> deleteQueryBatch(final Query query) throws Exception {
        QuerySnapshot querySnapshot = Tasks.await(query.get());

        WriteBatch batch = query.getFirestore().batch();
        for (DocumentSnapshot snapshot : querySnapshot) {
            batch.delete(snapshot.getReference());
        }
        Tasks.await(batch.commit());

        return querySnapshot.getDocuments();
    }


    void launchItemDetailsActivity(Item item) {
        Intent intent = new Intent(this, ItemDetailsViewActivity.class);
        intent.putExtra("item", Parcels.wrap(item));
        startActivity(intent);
    }

    void launchEditItemActivity(Item item) {
        Intent intent = new Intent(this, AddItemViewActivity.class);
        intent.putExtra("item", Parcels.wrap(item));
        startActivity(intent);
    }

    void launchDeleteConfirmationDialog(final int itemPosition) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(R.string.confirmation_delete_item);
        builder1.setCancelable(true);
        builder1.setTitle(R.string.confirmation_delete_photo_title);

        builder1.setPositiveButton(
                R.string.delete,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        loadingLayout.setVisibility(View.VISIBLE);
                        dialog.cancel();
                        deleteItem(itemPosition);
                    }
                });

        builder1.setNegativeButton(
                R.string.dismiss,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
