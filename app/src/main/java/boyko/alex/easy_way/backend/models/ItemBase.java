package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class ItemBase {
    public long id;
    public String title;
    public float price;
    public Category category;
    public ItemType itemType;
    public PriceType priceType;
    public Address address;
    public String photo;

    public ItemBase() {

    }

    public ItemBase(long id, String title, float price, Category category, ItemType itemType, PriceType priceType, Address address, String photo){
        this.id = id;
        this.title = title;
        this.price = price;
        this.category = category;
        this.itemType = itemType;
        this.priceType = priceType;
        this.address = address;
        this.photo = photo;
    }
}
