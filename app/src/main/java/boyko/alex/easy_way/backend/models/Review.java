package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Review {
    public String id;
    public String text;
    public byte mark;
    public long bookingId, itemId, userId;
    public String userName, userPhoto;

    public long createdAt;
    public Review(){

    }
}
