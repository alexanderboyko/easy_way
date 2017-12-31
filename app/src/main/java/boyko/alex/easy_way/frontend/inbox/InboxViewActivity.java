package boyko.alex.easy_way.frontend.inbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;

import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Dialog;
import boyko.alex.easy_way.frontend.inbox.dialog.DialogViewActivity;

/**
 * Created by Sasha on 24.12.2017.
 * <p>
 * This is inbox activity. Dialogs here. That's it.
 */

public class InboxViewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView emptyMessage;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private InboxRecyclerAdapter adapter;

    private Thread autoUpdateThread;

    private boolean firstLoaded = false, secondLoaded = false;
    private QuerySnapshot querySnapshot1, querySnapshot2;

    private int positionSelected = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_inbox);

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.REQUEST_CODE_EDIT) {
            if (resultCode == RequestCodes.RESULT_CODE_OK) {
                if (positionSelected != -1) {
                    Dialog dialog = adapter.getDialogs().get(positionSelected);
                    if (dialog.user1Id.equals(DataMediator.getUser().id)) {
                        adapter.getDialogs().get(positionSelected).user1HasUnreadMessage = false;
                    } else {
                        adapter.getDialogs().get(positionSelected).user2HasUnreadMessage = false;
                    }
                    adapter.notifyItemChanged(positionSelected);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.explore_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.explore_menu_refresh:
                refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        try{autoUpdateThread.interrupt();}catch (Exception e){e.printStackTrace();}
        super.onDestroy();
    }

    private void init() {
        initViews();
        initToolbar();
        initAdapter();
        initRecyclerView();
        initSwipeRefresh();
        initAutoUpdateThread();
    }

    private void initViews() {
        toolbar = findViewById(R.id.inbox_toolbar);
        emptyMessage = findViewById(R.id.inbox_empty);
        recyclerView = findViewById(R.id.inbox_recycler_view);
        swipeRefreshLayout = findViewById(R.id.inbox_swipe_refresh);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.inbox));
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initAdapter() {
        if (DataMediator.getDialogs().isEmpty()) emptyMessage.setVisibility(View.VISIBLE);
        adapter = new InboxRecyclerAdapter(DataMediator.getDialogs(), new InboxRecyclerAdapter.OnDialogClickListener() {
            @Override
            public void onDialogClicked(int position) {
                positionSelected = position;
                updateDialogOnClick(position);
                launchDialogActivity(adapter.getDialogs().get(position));
            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void initSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void initAutoUpdateThread(){
        autoUpdateThread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(3000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refresh();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        autoUpdateThread.start();
    }

    private void refresh() {
        FirebaseFirestore.getInstance().collection("dialogs")
                .whereEqualTo("user1Id", DataMediator.getUser().id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            querySnapshot1 = task.getResult();
                            firstLoaded = true;
                            filterDialogs();
                        } else {
                            querySnapshot1 = null;
                            firstLoaded = true;
                            Toast.makeText(InboxViewActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        FirebaseFirestore.getInstance().collection("dialogs")
                .whereEqualTo("user2Id", DataMediator.getUser().id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            querySnapshot2 = task.getResult();
                            secondLoaded = true;
                            filterDialogs();
                        } else {
                            querySnapshot2 = null;
                            secondLoaded = true;
                            Toast.makeText(InboxViewActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void filterDialogs() {
        if (firstLoaded && secondLoaded && querySnapshot1 != null && querySnapshot2 != null) {
            ArrayList<Dialog> dialogs = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot1) {
                Dialog dialog = ConvertHelper.convertToDialog(document);
                if (dialog.user1Id.equals(dialog.user2Id)) continue;
                dialogs.add(dialog);
            }
            for (DocumentSnapshot document : querySnapshot2) {
                Dialog dialog = ConvertHelper.convertToDialog(document);
                if (dialog.user1Id.equals(dialog.user2Id)) continue;
                dialogs.add(dialog);
            }

            DataMediator.setDialogs(dialogs);
            adapter.setDialogs(dialogs);
            adapter.notifyDataSetChanged();

            if (dialogs.isEmpty()) emptyMessage.setVisibility(View.VISIBLE);
            else emptyMessage.setVisibility(View.GONE);
        }
    }

    private void updateDialogOnClick(int position) {
        Dialog dialog = adapter.getDialogs().get(position);

        //change unread dialog to viewed
        if (DataMediator.getUser().id.equals(dialog.user1Id)) {
            adapter.getDialogs().get(position).user1HasUnreadMessage = false;
            FirebaseFirestore.getInstance().collection("dialogs").document(dialog.id).update("user1HasUnreadMessage", false);
        } else {
            adapter.getDialogs().get(position).user2HasUnreadMessage = false;
            FirebaseFirestore.getInstance().collection("dialogs").document(dialog.id).update("user2HasUnreadMessage", false);
        }

        adapter.notifyItemChanged(position);
    }

    private void launchDialogActivity(Dialog dialog) {
        Intent intent = new Intent(InboxViewActivity.this, DialogViewActivity.class);
        intent.putExtra("dialog", Parcels.wrap(dialog));
        startActivityForResult(intent, RequestCodes.REQUEST_CODE_EDIT);
    }
}
