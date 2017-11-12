package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Sasha on 11.11.2017.
 */

@Parcel
public class ItemExpanded extends ItemBase {
    public String description, notes;
    public UserBase user;
    public ArrayList<String> photosUrls;
    public ArrayList<BookingBase> bookings;
    public ArrayList<Review> reviews;
    public ArrayList<ItemBase> similarItems;

    public ItemExpanded() {

    }

    public ItemExpanded(ItemBase itemBase){
        super(itemBase.id, itemBase.title, itemBase.price, itemBase.category,
                itemBase.itemType, itemBase.priceType, itemBase.address, itemBase.photo);
    }
}
