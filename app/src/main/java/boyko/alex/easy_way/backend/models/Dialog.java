package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Dialog {
    private long id;
    private User user1, user2;
    private Message lastMessage;

    public Dialog() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
