package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class BookingBase {
    public long id;
    public long itemId;
    public long cratedAt, updatedAt, startedAt, endAt;
    public boolean isCanceled, isFinished, isStarted;

    public BookingBase(){

    }

    public BookingBase(long id, long itemId, long createdAt, long updatedAt, long startedAt, long endAt,
                       boolean isCanceled, boolean isFinished, boolean isStarted){
        this.id = id;
        this.itemId = itemId;
        this.cratedAt = createdAt;
        this.updatedAt = updatedAt;
        this.startedAt = startedAt;
        this.endAt = endAt;
        this.isCanceled = isCanceled;
        this.isFinished = isFinished;
        this.isStarted = isStarted;
    }

}
