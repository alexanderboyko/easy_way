package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 05.12.2017.
 */
@Parcel
public class Photo {
    public String photo;
    public boolean isMain;

    public Photo(){

    }

    public Photo(String photo, boolean isMain){
        this.photo = photo;
        this.isMain = isMain;
    }
}
