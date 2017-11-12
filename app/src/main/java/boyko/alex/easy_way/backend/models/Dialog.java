package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Dialog {
    public long id;
    public UserBase user1, user2;
    public Message lastMessage;

    public Dialog() {

    }

}
