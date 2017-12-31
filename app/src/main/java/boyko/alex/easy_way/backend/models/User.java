package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class User implements Serializable{
    public String id;
    public String email;
    public String name, surname;
    public String about;
    public int gender;
    public long birthday;
    public Address address;
    public String photo;

    public User(){

    }

    public String getFullName(){
        return name + " " + surname;
    }
}
