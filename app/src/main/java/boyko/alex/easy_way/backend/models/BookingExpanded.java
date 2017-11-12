package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 11.11.2017.
 */

@Parcel
public class BookingExpanded extends BookingBase {
    public UserBase owner, client;
    public ItemBase item;

    public BookingExpanded(){

    }

    public BookingExpanded(BookingBase bookingBase){
        super(bookingBase.id, bookingBase.itemId, bookingBase.cratedAt, bookingBase.updatedAt, bookingBase.startedAt,
                bookingBase.endAt, bookingBase.isCanceled, bookingBase.isFinished, bookingBase.isStarted);
    }
}
