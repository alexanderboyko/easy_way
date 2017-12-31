package boyko.alex.easy_way.frontend.item.item_details;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.models.Booking;
import boyko.alex.easy_way.backend.models.PriceType;
import boyko.alex.easy_way.libraries.DateHelper;

/**
 * Created by Sasha on 26.11.2017.
 *
 * This adapter shows bookings in bottomSheet when user click in calendar in item details
 */

class BookingsInfoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static final int MODE_INFO = 0;
    private int mode;
    private ArrayList<Booking> bookings;

    BookingsInfoRecyclerAdapter(int mode) {
        this.mode = mode;
        bookings = new ArrayList<>();
    }

    public void setBookings(ArrayList<Booking> bookings) {
        this.bookings = bookings;
    }

    public ArrayList<Booking> getBookings() {
        return bookings;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (mode == MODE_INFO) {
            View itemView = inflater.inflate(R.layout.item_booking_info, parent, false);
            viewHolder = new BookingHolder(itemView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Booking booking = bookings.get(position);

        if (mode == MODE_INFO) bindInfoHolder((BookingHolder) holder, booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    private void bindInfoHolder(BookingHolder holder, Booking booking) {
        if(!booking.isCanceled){
            holder.info.setText(ApplicationController.getInstance().getString(R.string.booking_canceled));
        }
        PriceType priceType = DataMediator.getPriceType(booking.priceTypeId);
        if (priceType != null && priceType.shortName.equals("/day")) {
            String formattedText = ApplicationController.getInstance().getString(R.string.reserved_for_all_day);
            if (!DateHelper.ifTimesFromOneDay(booking.startedAt, booking.endAt)) {
                formattedText += " (" + DateHelper.getSmartFormattedDate((booking.startedAt))
                        + " - " + DateHelper.getSmartFormattedDate(booking.endAt) + ")";
            }
            holder.info.setText(formattedText);
        } else {
            String text = "Booking from " + DateHelper.getFormattedTime(booking.startedAt) + " till " + DateHelper.getFormattedTime(booking.endAt);
            holder.info.setText(text);
        }
    }

    private class BookingHolder extends RecyclerView.ViewHolder {
        TextView info;

        BookingHolder(View itemView) {
            super(itemView);
            info = itemView.findViewById(R.id.item_booking_info);
        }
    }
}
