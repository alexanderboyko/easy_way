package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Item {
    public long id;
    public String title;
    public float price;
    public String description, notes;
    public User user;
    public Category category;
    public ItemType itemType;
    public PriceType priceType;
    public ArrayList<String> photoUrls;

    public Item() {

    }
}
