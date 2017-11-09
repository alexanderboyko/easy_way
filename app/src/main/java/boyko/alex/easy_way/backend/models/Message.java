package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Message {
    private long id, id_dialog;
    private String text;
    private long sentAt;
    private User sender;

    private Message(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId_dialog() {
        return id_dialog;
    }

    public void setId_dialog(long id_dialog) {
        this.id_dialog = id_dialog;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }
}
