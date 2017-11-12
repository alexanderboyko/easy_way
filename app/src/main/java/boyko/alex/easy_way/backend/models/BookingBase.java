package boyko.alex.easy_way.backend.models;

import org.parceler.Parcel;

/**
 * Created by Sasha on 04.11.2017.
 */

@Parcel
public class Booking {
    public long id;
    public long cratedAt, updatedAt, startedAt, endAt;
    public boolean isCanceled, isFinished, isStarted;
    public User owner, client;
    public ItemBase item;

    public Booking(){

    }

}
