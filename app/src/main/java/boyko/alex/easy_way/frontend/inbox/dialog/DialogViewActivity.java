package boyko.alex.easy_way.frontend.inbox.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Locale;

import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Dialog;
import boyko.alex.easy_way.backend.models.Message;
import boyko.alex.easy_way.libraries.DateHelper;

/**
 * Created by Sasha on 25.12.2017.
 * <p>
 * Activity with messages
 */

public class DialogViewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;

    private EditText messageEditText;
    private AppCompatImageView buttonSend;

    private Dialog dialog;

    private static final CollectionReference collectionReference =
            FirebaseFirestore.getInstance().collection("messages");

    private static Query query;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        if (savedInstanceState == null) {
            dialog = Parcels.unwrap(getIntent().getParcelableExtra("dialog"));
        } else {
            dialog = Parcels.unwrap(savedInstanceState.getParcelable("dialog"));
        }
        query = collectionReference.whereEqualTo("id_dialog", dialog.id).orderBy("sentAt", Query.Direction.DESCENDING);

        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    protected void onDestroy() {
        if(DataMediator.getUser().id.equals(dialog.user1Id)){
            FirebaseFirestore.getInstance().collection("dialogs").document(dialog.id).update("user1HasUnreadMessage", false);
            dialog.user1HasUnreadMessage = false;
        }else{
            FirebaseFirestore.getInstance().collection("dialogs").document(dialog.id).update("user2HasUnreadMessage", false);
            dialog.user2HasUnreadMessage = false;
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("dialog", Parcels.wrap(dialog));
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

    @Override
    public void onBackPressed() {
        setResult(RequestCodes.RESULT_CODE_OK);
        super.onBackPressed();
    }

    private void init() {
        initViews();
        initToolbar();
        initAdapter();
        initRecyclerView();
        initInputs();
    }

    private void initViews() {
        toolbar = findViewById(R.id.dialog_toolbar);
        recyclerView = findViewById(R.id.dialog_recycler_view);
        messageEditText = findViewById(R.id.dialog_message_edit_text);
        buttonSend = findViewById(R.id.dialog_send);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            if (dialog.user1Id.equals(DataMediator.getUser().id)) {
                getSupportActionBar().setTitle(dialog.user2FullName);
            } else {
                getSupportActionBar().setTitle(dialog.user1FullName);
            }
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initAdapter() {
        FirestoreRecyclerOptions<Message> options =
                new FirestoreRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter = new FirestoreRecyclerAdapter<Message, MessageHolder>(options) {
            @Override
            public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MessageHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull Message message) {
                //set message text
                holder.message.setText(message.text);
                //set date
                if (DateHelper.ifTimesFromOneDay(message.sentAt, DateHelper.getCurrentTime())) {
                    SimpleDateFormat simpleDateFormatArrivals = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    holder.date.setText(simpleDateFormatArrivals.format(message.sentAt));
                } else {
                    SimpleDateFormat simpleDateFormatArrivals = new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault());
                    holder.date.setText(simpleDateFormatArrivals.format(message.sentAt));
                }
                //set gravity and color
                if (message.senderId.equals(DataMediator.getUser().id)) {
                    holder.gravityLayout.setGravity(Gravity.END);
                    holder.formLayout.setBackground(getResources().getDrawable(R.drawable.drawable_message_to_user, null));
                    holder.message.setTextColor(ContextCompat.getColor(DialogViewActivity.this, R.color.icons));
                    holder.date.setTextColor(ContextCompat.getColor(DialogViewActivity.this, R.color.icons));
                } else {
                    holder.gravityLayout.setGravity(Gravity.START);
                    holder.formLayout.setBackground(getResources().getDrawable(R.drawable.drawable_message_from_user, null));
                    holder.message.setTextColor(ContextCompat.getColor(DialogViewActivity.this, R.color.primary_text));
                    holder.date.setTextColor(ContextCompat.getColor(DialogViewActivity.this, R.color.primary_text));
                }
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                //mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void initInputs() {
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    buttonSend.setColorFilter(ContextCompat.getColor(DialogViewActivity.this, R.color.secondary_text), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    buttonSend.setColorFilter(ContextCompat.getColor(DialogViewActivity.this, R.color.color_accent), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
        });

        if (messageEditText.getText().toString().isEmpty()) {
            buttonSend.setColorFilter(ContextCompat.getColor(DialogViewActivity.this, R.color.secondary_text), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            buttonSend.setColorFilter(ContextCompat.getColor(DialogViewActivity.this, R.color.color_accent), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!messageEditText.getText().toString().isEmpty()) {
                    addMessage();
                    messageEditText.setText("");
                }
            }
        });
    }

    private void addMessage() {
        Message message = new Message();
        message.senderId = DataMediator.getUser().id;
        message.text = messageEditText.getText().toString();
        message.sentAt = DateHelper.getCurrentTime();
        message.id_dialog = dialog.id;

        collectionReference.add(message).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DialogViewActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });

        if(dialog.user1Id.equals(DataMediator.getUser().id)){
            FirebaseFirestore.getInstance().collection("dialogs").document(dialog.id).update("lastMessage", message.text, "lastUpdate", DateHelper.getCurrentTime(), "user2HasUnreadMessage", true);
        }else{
            FirebaseFirestore.getInstance().collection("dialogs").document(dialog.id).update("lastMessage", message.text, "lastUpdate", DateHelper.getCurrentTime(), "user1HasUnreadMessage", true);
        }
    }

    private class MessageHolder extends RecyclerView.ViewHolder {
        LinearLayout gravityLayout, formLayout;
        TextView message, date;

        MessageHolder(View itemView) {
            super(itemView);
            gravityLayout = itemView.findViewById(R.id.item_message_gravity_layout);
            formLayout = itemView.findViewById(R.id.item_message_form_layout);
            message = itemView.findViewById(R.id.item_message_message);
            date = itemView.findViewById(R.id.item_message_date);
        }
    }
}
