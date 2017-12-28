package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Message {
    public String id;
    public String id_dialog;
    public String text;
    public long sentAt;
    public String senderId;

    public Message(){

    }

}
