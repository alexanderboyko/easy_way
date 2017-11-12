package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class User {
    public long id;
    public String name, surname, about;
    public byte gender;
    public long birthday;
    public Address address;
    public ArrayList<String> photoUrls;

    public User(){

    }
}
