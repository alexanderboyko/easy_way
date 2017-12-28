package boyko.alex.easy_way.frontend.bookings;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.models.Booking;
import boyko.alex.easy_way.backend.models.PriceType;
import boyko.alex.easy_way.libraries.DateHelper;

/**
 * Created by Sasha on 27.12.2017.
 * <p>
 * Booking list adapter
 */

class BookingsRecyclerAdapter extends RecyclerView.Adapter<BookingsRecyclerAdapter.BookingHolder> {
    final static int MODE_MY_BOOKINGS = 1, MODE_BOOKINGS_TO_MY_OFFERS = 2;

    private int mode;
    private ArrayList<Booking> bookings;
    private OnBookingClickListener listener;

    interface OnBookingClickListener {
        void onBookingClick(int position);

        void onButtonAction1Clicked(int position);

        void onButtonAction2Clicked(int position);
    }

    BookingsRecyclerAdapter(int mode) {
        this.mode = mode;
    }

    ArrayList<Booking> getBookings() {
        return bookings;
    }

    void setBookings(ArrayList<Booking> bookings) {
        this.bookings = bookings;
    }

    void setListener(OnBookingClickListener onBookingClickListener){
        listener = onBookingClickListener;
    }
    @Override
    public BookingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookingHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_reserved, parent, false));
    }

    @Override
    public void onBindViewHolder(final BookingHolder holder, int position) {
        Booking booking = bookings.get(position);

        setPhoto(holder, booking.itemPhoto);
        setDates(holder, booking);
        setStatus(holder, booking);
        holder.title.setText(booking.itemTitle);
        holder.price.setText(String.valueOf(booking.price));

        if (mode == MODE_MY_BOOKINGS) {
            setButtonsMyBooking(holder, booking);
        } else {
            setButtonsMyReservedBooking(holder, booking);
        }

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onButtonAction2Clicked(holder.getAdapterPosition());
            }
        });
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onButtonAction1Clicked(holder.getAdapterPosition());
            }
        });
        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onBookingClick(holder.getAdapterPosition());
            }
        });
    }

    private void setPhoto(final BookingHolder holder, String photo) {
        if (photo != null) {
            holder.photo.setVisibility(View.VISIBLE);
            holder.noPhotoLayout.setVisibility(View.GONE);
            try {
                Glide.with(ApplicationController.getInstance())
                        .load(new URL(photo))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                        .apply(RequestOptions.noTransformation())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.photo.setVisibility(View.GONE);
                                holder.noPhotoLayout.setVisibility(View.VISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(holder.photo);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            holder.photo.setVisibility(View.GONE);
            holder.noPhotoLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setDates(BookingHolder holder, Booking booking) {
        PriceType priceType = DataMediator.getPriceType(booking.priceTypeId);
        if (priceType != null && priceType.shortName.equals("/day")) {
            holder.dates.setText(
                    DateHelper.getFormattedDateWithoutTime(booking.startedAt)
                            + " - "
                            + DateHelper.getFormattedDateWithoutTime(booking.endAt));
        } else {
            holder.dates.setText(
                    DateHelper.getFormattedDateWithTime(booking.startedAt)
                            + " - "
                            + DateHelper.getFormattedDateWithTime(booking.endAt));
        }
    }

    private void setStatus(final BookingHolder holder, Booking booking) {
        if (booking.isStarted) {
            //started
            if (booking.isFinished) {
                //finished
                if (booking.isReviewAdded) {
                    //finished and review added
                    holder.status.setText(ApplicationController.getInstance().getString(R.string.booking_status_)
                            + " " +
                            ApplicationController.getInstance().getString(R.string.booking_status_finished));
                    holder.status.setTextColor(ContextCompat.getColor(ApplicationController.getInstance(), R.color.primary_text));
                } else {
                    //finished - button to add review
                    holder.status.setText(ApplicationController.getInstance().getString(R.string.booking_status_)
                            + " " +
                            ApplicationController.getInstance().getString(R.string.booking_status_finished));
                    holder.status.setTextColor(ContextCompat.getColor(ApplicationController.getInstance(), R.color.primary_text));

                }
            } else {
                //in progress
                holder.status.setText(ApplicationController.getInstance().getString(R.string.booking_status_)
                        + " " +
                        ApplicationController.getInstance().getString(R.string.booking_status_in_progress));
                holder.status.setTextColor(ContextCompat.getColor(ApplicationController.getInstance(), R.color.color_accent));
            }
        } else {
            //not started
            if (booking.isCanceled) {
                //canceled
                holder.status.setText(ApplicationController.getInstance().getString(R.string.booking_status_)
                        + " " +
                        ApplicationController.getInstance().getString(R.string.booking_status_canceled));
                holder.status.setTextColor(ContextCompat.getColor(ApplicationController.getInstance(), R.color.likeColor));
            } else {
                if (booking.isConfirmed) {
                    //confirmed
                    holder.status.setText(ApplicationController.getInstance().getString(R.string.booking_status_)
                            + " " +
                            ApplicationController.getInstance().getString(R.string.booking_status_confiremed));
                    holder.status.setTextColor(ContextCompat.getColor(ApplicationController.getInstance(), R.color.color_accent));
                } else {
                    //requested
                    holder.status.setText(ApplicationController.getInstance().getString(R.string.booking_status_)
                            + " " +
                            ApplicationController.getInstance().getString(R.string.booking_status_requested));
                    holder.status.setTextColor(ContextCompat.getColor(ApplicationController.getInstance(), R.color.primary_text));
                }
            }
        }
    }

    private void setButtonsMyBooking(final BookingHolder holder, Booking booking) {
        holder.decline.setVisibility(View.VISIBLE);
        holder.decline.setVisibility(View.VISIBLE);
        if (booking.isStarted) {
            //started
            if (booking.isFinished) {
                //finished
                if (booking.isReviewAdded) {
                    //finished and review added
                    holder.decline.setVisibility(View.GONE);
                    holder.accept.setVisibility(View.GONE);
                } else {
                    //finished - button to add review
                    holder.decline.setVisibility(View.GONE);
                    holder.accept.setText(ApplicationController.getInstance().getString(R.string.add_review));
                }
            } else {
                //in progress
                holder.decline.setVisibility(View.GONE);
                holder.accept.setVisibility(View.GONE);
            }
        } else {
            //not started
            if (booking.isCanceled) {
                //canceled
                holder.decline.setVisibility(View.GONE);
                holder.accept.setVisibility(View.GONE);
            } else {
                if (booking.isConfirmed) {
                    //confirmed
                    holder.decline.setVisibility(View.VISIBLE);
                    holder.decline.setText(ApplicationController.getInstance().getString(R.string.booking_cancel));
                    holder.accept.setVisibility(View.GONE);
                } else {
                    //requested
                    holder.decline.setVisibility(View.VISIBLE);
                    holder.decline.setText(ApplicationController.getInstance().getString(R.string.booking_cancel));
                    holder.accept.setVisibility(View.GONE);
                }
            }
        }
    }

    private void setButtonsMyReservedBooking(final BookingHolder holder, Booking booking) {
        holder.decline.setVisibility(View.VISIBLE);
        holder.decline.setVisibility(View.VISIBLE);
        holder.decline.setText(ApplicationController.getInstance().getString(R.string.booking_decline));
        if (booking.isStarted) {
            //started
            if (booking.isFinished) {
                //finished
                if (booking.isReviewAdded) {
                    //finished and review added
                    holder.decline.setVisibility(View.GONE);
                    holder.accept.setVisibility(View.GONE);
                } else {
                    //finished - button to add review
                    holder.decline.setVisibility(View.GONE);
                    holder.accept.setVisibility(View.GONE);
                }
            } else {
                //in progress
                holder.decline.setVisibility(View.GONE);
                holder.accept.setVisibility(View.GONE);
            }
        } else {
            //not started
            if (booking.isCanceled) {
                //canceled
                holder.decline.setVisibility(View.GONE);
                holder.accept.setVisibility(View.GONE);
            } else {
                if (booking.isConfirmed) {
                    //confirmed
                    holder.decline.setVisibility(View.VISIBLE);
                    holder.accept.setVisibility(View.GONE);
                } else {
                    //requested
                    holder.decline.setVisibility(View.VISIBLE);
                    holder.accept.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class BookingHolder extends RecyclerView.ViewHolder {
        RelativeLayout background;
        LinearLayout noPhotoLayout;
        AppCompatImageView photo;
        TextView title, price, dates, status;
        Button decline, accept;

        BookingHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.item_booking_background);
            noPhotoLayout = itemView.findViewById(R.id.item_booking_no_photo_layout);
            photo = itemView.findViewById(R.id.item_booking_photo);
            title = itemView.findViewById(R.id.item_booking_title);
            price = itemView.findViewById(R.id.item_booking_price);
            dates = itemView.findViewById(R.id.item_booking_dates);
            status = itemView.findViewById(R.id.item_booking_status);
            decline = itemView.findViewById(R.id.item_booking_decline);
            accept = itemView.findViewById(R.id.item_booking_accept);
        }
    }
}
