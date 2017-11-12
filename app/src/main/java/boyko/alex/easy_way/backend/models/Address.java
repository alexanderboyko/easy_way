package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Address {
    public long id;
    public String name, fullName;
    public double latitude, longitude, southwestLongitude, southwestLatitude, northeastLatitude, northeastLongitude;

    public Address(){

    }
}
