package boyko.alex.easy_way.frontend.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Booking;
import boyko.alex.easy_way.backend.models.Message;
import boyko.alex.easy_way.backend.models.PriceType;
import boyko.alex.easy_way.frontend.item.item_details.ItemDetailsViewActivity;
import boyko.alex.easy_way.frontend.review.AddReviewActivity;
import boyko.alex.easy_way.libraries.DateHelper;

/**
 * Created by Sasha on 27.12.2017.
 * <p>
 * Bookings here
 */

public class BookingsViewFragment extends Fragment {
    final static int MODE_MY_BOOKINGS = 1, MODE_BOOKINGS_TO_MY_OFFERS = 2;
    private RecyclerView recyclerView;
    private BookingsRecyclerAdapter adapter;
    private TextView emptyMessage;

    private int mode = MODE_MY_BOOKINGS;
    private int positionClicked = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            mode = getArguments().getInt("mode");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.REQUEST_CODE_EDIT) {
            if (resultCode == RequestCodes.RESULT_CODE_OK) {
                Booking booking = adapter.getBookings().get(positionClicked);
                adapter.getBookings().get(positionClicked).isReviewAdded = true;
                adapter.notifyItemChanged(positionClicked);
                FirebaseFirestore.getInstance().collection("bookings").document(booking.id)
                        .update("isReviewAdded", true);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        emptyMessage = view.findViewById(R.id.bookings_empty);
        recyclerView = view.findViewById(R.id.bookings_recycler);
        if(adapter == null) {
            if (mode == MODE_MY_BOOKINGS) {
                adapter = new BookingsRecyclerAdapter(BookingsRecyclerAdapter.MODE_MY_BOOKINGS);
            } else {
                adapter = new BookingsRecyclerAdapter(BookingsRecyclerAdapter.MODE_BOOKINGS_TO_MY_OFFERS);
            }
            adapter.setListener(new BookingsRecyclerAdapter.OnBookingClickListener() {
                @Override
                public void onBookingClick(int position) {
                    positionClicked = position;
                    launchItemDetailsActivity(adapter.getBookings().get(position).itemId);
                }

                @Override
                public void onButtonAction1Clicked(int position) {
                    positionClicked = position;
                    onDeclineClicked(position);
                }

                @Override
                public void onButtonAction2Clicked(int position) {
                    positionClicked = position;
                    onAcceptClicked(position);
                }
            });
            adapter.setBookings(new ArrayList<Booking>());
            loadBookings();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void loadBookings() {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("bookings");

        if (mode == MODE_MY_BOOKINGS) {
            Query query = collectionReference.whereEqualTo("clientId", DataMediator.getUser().id);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        loadFinished(task);
                    } else {
                        Toast.makeText(BookingsViewFragment.this.getContext(), getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Query query = collectionReference.whereEqualTo("ownerId", DataMediator.getUser().id);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        loadFinished(task);
                    } else {
                        Toast.makeText(BookingsViewFragment.this.getContext(), getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void loadFinished(@NonNull Task<QuerySnapshot> task) {
        ArrayList<Booking> bookings = new ArrayList<>();
        for (DocumentSnapshot documentSnapshot : task.getResult()) {
            Booking booking = documentSnapshot.toObject(Booking.class);
            booking.id = documentSnapshot.getId();
            updateBookingFlags(booking);
            bookings.add(booking);
        }
        sortBookings(bookings);

        if (bookings.isEmpty()) {
            showEmptyMessage();
        } else {
            adapter.setBookings(bookings);
            adapter.notifyDataSetChanged();
        }
    }

    private void showEmptyMessage() {
        recyclerView.setVisibility(View.GONE);
        emptyMessage.setVisibility(View.VISIBLE);
        if (mode == MODE_MY_BOOKINGS) {
            emptyMessage.setText(getString(R.string.booking_empty_message_my_bookings));
        } else {
            emptyMessage.setText(getString(R.string.booking_empty_message_bookings_to_ny_offers));
        }
    }

    private void sortBookings(ArrayList<Booking> bookings){
        Collections.sort(bookings, new Comparator<Booking>(){
            public int compare(Booking o1, Booking o2){
                if(o1.createdAt == o2.createdAt)
                    return 0;
                return o1.createdAt > o2.createdAt ? -1 : 1;
            }
        });
    }

    private void updateBookingFlags(Booking booking) {
        if ((!booking.isStarted || !booking.isFinished) && !booking.isCanceled) {
            //if not started or not finished and not canceled - check and update flags if needed

            //update isStarted if need
            if (DateHelper.ifTimesFromOneDay(booking.startedAt, DateHelper.getCurrentTime()) || booking.startedAt < DateHelper.getCurrentTime()) {
                if (!booking.isStarted && !booking.isFinished) {
                    //set started
                    PriceType priceType = DataMediator.getPriceType(booking.priceTypeId);
                    if (priceType != null && priceType.shortName.equals("/day")) {
                        booking.isStarted = true;
                        FirebaseFirestore.getInstance().collection("bookings").document(booking.id).update("isStarted", true);
                    } else {
                        if (booking.startedAt < DateHelper.getCurrentTime()) {
                            booking.isStarted = true;
                            FirebaseFirestore.getInstance().collection("bookings").document(booking.id)
                                    .update("isStarted", true);
                        }
                    }
                }
            }

            if (booking.isStarted && !booking.isFinished) {
                if (DateHelper.ifTimesFromOneDay(booking.endAt, DateHelper.getCurrentTime()) || booking.endAt < DateHelper.getCurrentTime()) {
                    //set started
                    PriceType priceType = DataMediator.getPriceType(booking.priceTypeId);
                    if (priceType != null && priceType.shortName.equals("/day")) {
                        if (DateHelper.ifTimesFromOneDay(DateHelper.getNextDayTime(booking.endAt), DateHelper.getCurrentTime()) || booking.endAt < DateHelper.getCurrentTime()) {
                            booking.isFinished = true;
                            FirebaseFirestore.getInstance().collection("bookings").document(booking.id).update("isFinished", true);
                        }
                    } else {
                        if (booking.endAt < DateHelper.getCurrentTime()) {
                            booking.isFinished = true;
                            FirebaseFirestore.getInstance().collection("bookings").document(booking.id).update("isFinished", true);
                        }
                    }
                }
            }
        }
    }

    private void onDeclineClicked(int position) {
        Booking booking = adapter.getBookings().get(position);

        adapter.getBookings().get(position).isCanceled = true;
        adapter.notifyItemChanged(position);
        FirebaseFirestore.getInstance().collection("bookings").document(booking.id)
                .update("isCanceled", true);
        sendDeclineMessage(adapter.getBookings().get(position));
    }

    private void onAcceptClicked(int position) {
        Booking booking = adapter.getBookings().get(position);

        if (mode == MODE_MY_BOOKINGS) {
            //add review
            launchAddReviewActivity(position);
        } else {
            adapter.getBookings().get(position).isConfirmed = true;
            adapter.notifyItemChanged(position);
            FirebaseFirestore.getInstance().collection("bookings").document(booking.id)
                    .update("isConfirmed", true);
            sendAcceptMessage(adapter.getBookings().get(position));
        }
    }

    void sendAcceptMessage(final Booking booking) {
        final Message message = new Message();
        message.id_dialog = booking.dialogId;
        message.sentAt = DateHelper.getCurrentTime();

        String messageText = "";
        message.text = "Your request for item " + booking.itemTitle + " on dates ";
        PriceType priceType = DataMediator.getPriceType(booking.priceTypeId);
        if (priceType != null && priceType.shortName.equals("/day")) {
            messageText +=
                    DateHelper.getSmartFormattedDate(booking.startedAt)
                            + " - "
                            + DateHelper.getSmartFormattedDate(booking.endAt);
        } else {
            messageText +=
                    DateHelper.getSmartFormattedDateWithTime(booking.startedAt)
                            + " - "
                            + DateHelper.getSmartFormattedDateWithTime(booking.endAt);
        }
        messageText += " has been accepted. Check it in booking section.";
        message.text = messageText;
        message.senderId = booking.ownerId;

        FirebaseFirestore.getInstance().collection("messages").add(message).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    FirebaseFirestore.getInstance().collection("dialogs").document(booking.dialogId).update("lastMessage", message.text);
                }else{
                    Toast.makeText(getContext(), R.string.error_sending_accept_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void sendDeclineMessage(final Booking booking) {
        final Message message = new Message();
        message.id_dialog = booking.dialogId;
        message.sentAt = DateHelper.getCurrentTime();

        String messageText = "";
        message.text = "Your request for item " + booking.itemTitle + " on dates ";
        PriceType priceType = DataMediator.getPriceType(booking.priceTypeId);
        if (priceType != null && priceType.shortName.equals("/day")) {
            messageText +=
                    DateHelper.getSmartFormattedDate(booking.startedAt)
                            + " - "
                            + DateHelper.getSmartFormattedDate(booking.endAt);
        } else {
            messageText +=
                    DateHelper.getSmartFormattedDateWithTime(booking.startedAt)
                            + " - "
                            + DateHelper.getSmartFormattedDateWithTime(booking.endAt);
        }
        messageText += " has been declined. Check it in booking section.";
        message.text = messageText;
        message.senderId = booking.ownerId;

        FirebaseFirestore.getInstance().collection("messages").add(message).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    FirebaseFirestore.getInstance().collection("dialogs").document(booking.dialogId).update("lastMessage", message.text);
                }else{
                    Toast.makeText(getContext(), R.string.error_sending_accept_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void launchItemDetailsActivity(String itemId) {
        Intent intent = new Intent(getActivity(), ItemDetailsViewActivity.class);
        intent.putExtra("itemId", itemId);
        startActivity(intent);
    }

    void launchAddReviewActivity(int position) {
        Intent intent = new Intent(getActivity(), AddReviewActivity.class);
        intent.putExtra("itemId", adapter.getBookings().get(position).itemId);
        intent.putExtra("ownerId", adapter.getBookings().get(position).ownerId);
        intent.putExtra("booking", Parcels.wrap(adapter.getBookings().get(position)));
        startActivityForResult(intent, RequestCodes.REQUEST_CODE_EDIT);
    }
}
