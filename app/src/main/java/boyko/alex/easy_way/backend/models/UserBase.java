package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class UserBase {
    public long id;
    public String name, surname;
    public String photo;

    public UserBase(){

    }

    public UserBase(long id, String name, String surname, String photo){
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.photo = photo;
    }

    public String getFullName(){
        return name + " " + surname;
    }
}
