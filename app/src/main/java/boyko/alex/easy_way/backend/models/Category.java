package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Category {
    public long id;
    public String name;
    public String iconUrl;
    public Category parentCategory;

    public Category(){

    }

}
