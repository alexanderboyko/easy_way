package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Item {
    public String id;
    public String title,description, notes;
    public double price, ratingSum, ratingAverage;
    public String categoryId, priceTypeId, ownerId;
    public Address address;
    public ArrayList<String> photos;
    public String mainPhoto;

    public boolean isLiked = false;

    public Item() {

    }
}
