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
    public String categoryId, itemTypeId, priceTypeId, ownerId;
    public Address address;
    public ArrayList<String> photos;

    public Item() {

    }

    public Item(String id, String title, double price, String categoryId, String itemTypeId, String priceTypeId, Address address, ArrayList<String> photos, String description, String notes) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.categoryId = categoryId;
        this.itemTypeId = itemTypeId;
        this.priceTypeId = priceTypeId;
        this.address = address;
        this.photos = photos;
        this.description = description;
        this.notes = notes;
    }

    public Item(String title, double price, String categoryId, String itemTypeId, String priceTypeId, Address address, ArrayList<String> photos, String description, String notes) {
        this.title = title;
        this.price = price;
        this.categoryId = categoryId;
        this.itemTypeId = itemTypeId;
        this.priceTypeId = priceTypeId;
        this.address = address;
        this.photos = photos;
        this.description = description;
        this.notes = notes;
    }
}
