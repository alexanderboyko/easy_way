package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Sasha on 11.11.2017.
 */

@Parcel
public class UserExpanded extends UserBase{
    public String about;
    public byte gender;
    public long birthday;
    public Address address;
    public ArrayList<String> photos;
    public ArrayList<ItemBase> items;
    public ArrayList<Review> reviews;

    public UserExpanded(){

    }

    public UserExpanded(UserBase userBase){
        super(userBase.id, userBase.name, userBase.surname, userBase.photo);
    }

    public UserExpanded(long id, String name, String surname, String photo,
                        String about, byte gender, long birthday, Address address,
                        ArrayList<String> photos, ArrayList<ItemBase> items, ArrayList<Review> reviews){
        super(id,name,surname,photo);
        this.about = about;
        this.gender = gender;
        this.birthday = birthday;
        this.address = address;
        this.photos = photos;
        this.items = items;
        this.reviews = reviews;
    }
}

