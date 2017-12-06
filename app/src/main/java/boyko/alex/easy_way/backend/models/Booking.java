package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Booking {
    public String id, ownerId, clientId, itemId, priceTypeId;
    public long createdAt, updatedAt, startedAt, endAt;
    public boolean isCanceled, isFinished, isStarted;

    public boolean isConfirmed;
    public String ownerName, ownerPhoto, clientName, clientPhoto;

    public String itemTitle, itemPhoto;

    public Booking() {

    }

}
