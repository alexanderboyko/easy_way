package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Booking {
    public String id, ownerId, clientId, itemId;
    public long createdAt, updatedAt, startedAt, endAt;
    public boolean isCanceled, isFinished, isStarted;

    public String itemTitle, itemPhoto;

    public Booking() {

    }

}
