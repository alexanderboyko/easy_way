package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Message {
    public String id;
    public long id_dialog;
    public String text;
    public long sentAt;
    public long senderId;

    public Message(){

    }

}
