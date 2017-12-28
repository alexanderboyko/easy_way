package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Item implements Serializable{
    public String id;
    public String title,description, notes;
    public double price, ratingSum, ratingAverage;
    public String categoryId, priceTypeId, ownerId;
    public Address address;
    public ArrayList<String> photos;
    public String mainPhoto;
    public long createdAt;

    public boolean isLiked = false;

    public Item() {

    }

    public Item(Item item){
        this.id = item.id;
        this.title = item.title;
        this.description = item.description;
        this.notes = item.notes;
        this.price = item.price;
        this.priceTypeId = item.priceTypeId;
        this.ratingSum = item.ratingSum;
        this.ratingAverage = item.ratingAverage;
        this.categoryId = item.categoryId;
        this.ownerId = item.ownerId;
        this.address = item.address;
        this.photos = item.photos;
        this.mainPhoto = item.mainPhoto;
        this.createdAt = item.createdAt;
    }
}
