package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Sasha on 11.11.2017.
 */
@Parcel
public class UserSystem extends UserExpanded {
    public ArrayList<ItemBase> likedItems;

    public UserSystem() {

    }

    public UserSystem(UserExpanded userExpanded) {
        super(userExpanded.id, userExpanded.name, userExpanded.surname, userExpanded.photo,
                userExpanded.about, userExpanded.gender, userExpanded.birthday, userExpanded.address,
                userExpanded.photos, userExpanded.items, userExpanded.reviews);
    }
}
