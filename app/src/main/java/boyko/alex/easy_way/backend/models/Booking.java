package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Booking {
    public String id, ownerId, clientId, itemId, priceTypeId, dialogId;
    public long createdAt, updatedAt, startedAt, endAt;
    public boolean isCanceled, isFinished, isStarted;

    public double price;

    public boolean isConfirmed, isReviewAdded;

    public String itemTitle, itemPhoto;

    public Booking() {

    }

}
