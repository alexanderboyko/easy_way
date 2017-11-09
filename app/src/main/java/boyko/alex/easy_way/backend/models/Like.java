package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */
@Parcel
public class Like {
    private long id, id_item, id_user;

    public Like() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId_item() {
        return id_item;
    }

    public void setId_item(long id_item) {
        this.id_item = id_item;
    }

    public long getId_user() {
        return id_user;
    }

    public void setId_user(long id_user) {
        this.id_user = id_user;
    }
}
