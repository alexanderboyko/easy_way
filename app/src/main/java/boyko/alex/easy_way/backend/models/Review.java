package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Review {
    public long id;
    public String text;
    public byte mark;
    public UserBase userFrom;
    public BookingBase booking;
    public ItemBase item;

    public Review(){

    }
}
