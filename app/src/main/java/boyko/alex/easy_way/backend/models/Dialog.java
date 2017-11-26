package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Dialog {
    public String id;
    public long user1Id, user2Id;
    public String user1FullName, user2FullName, user1Photo, user2Photo;
    public Message lastMessage;

    public Dialog() {

    }

}
