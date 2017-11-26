package boyko.alex.easy_way.backend;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;

import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Booking;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.Review;
import boyko.alex.easy_way.backend.models.User;

/**
 * Created by Sasha on 20.11.2017.
 */

public class ConvertHelper {
    private final static String LOG_TAG = "CONVERT";
    public static ArrayList<String> stringsToArrayList(@Nullable String string) {
        if (string != null && !string.isEmpty() && !string.equals("[]")) {
            String formattedString = string.substring(string.indexOf("[") + 1, string.indexOf("]"));
            ArrayList<String> items = new ArrayList<>();
            items.addAll(Arrays.asList(formattedString.split(",")));
            return items;
        }
        return null;
    }

    public static Item convertToItem(DocumentSnapshot document) {
        Item item = new Item();
        item.id = document.getId();
        item.title = document.getString("title");
        item.description = document.getString("description");
        item.notes = document.getString("notes");
        item.price = document.getDouble("price");
        item.ratingSum = document.getDouble("ratingSum");
        item.ratingAverage = document.getDouble("ratingAverage");
        item.categoryId = document.getString("categoryId");
        item.itemTypeId = document.getString("itemTypeId");
        item.priceTypeId = document.getString("priceTypeId");

        Address address = new Address();
        address.name = document.getString("address.name");
        address.fullName = document.getString("address.fullName");
        address.latitude = document.getDouble("address.latitude");
        address.longitude = document.getDouble("address.longitude");
        address.northeastLatitude = document.getDouble("address.northeastLatitude");
        address.northeastLongitude = document.getDouble("address.northeastLongitude");
        address.southwestLatitude = document.getDouble("address.southwestLatitude");
        address.southwestLongitude = document.getDouble("address.southwestLongitude");

        item.address = address;
        item.photos = ConvertHelper.stringsToArrayList(document.getData().get("photos").toString());
        return item;
    }

    public static Booking convertToBooking(DocumentSnapshot document) {
        Booking booking = new Booking();
        booking.id = document.getId();
        booking.clientId = document.getString("clientId");
        booking.ownerId = document.getString("ownerId");
        booking.itemId = document.getString("itemId");
        booking.isCanceled = document.getBoolean("isCanceled");
        booking.isFinished = document.getBoolean("isFinished");
        booking.isStarted = document.getBoolean("isStarted");
        booking.createdAt = document.getDate("createdAt").getTime();
        booking.updatedAt = document.getDate("updatedAt").getTime();
        booking.startedAt = document.getDate("startedAt").getTime();
        booking.endAt = document.getDate("endAt").getTime();
        return booking;
    }

    public static Review convertToReview(DocumentSnapshot document) {
        Review review = document.toObject(Review.class);
        review.id = document.getId();
        return review;
    }

    public static User convertToUser(DocumentSnapshot document){
        User user = new User();
        user.id = document.getId();
        user.birthday = document.getDate("birthday").getTime();

        Address address = new Address();
        address.name = document.getString("address.name");
        address.fullName = document.getString("address.fullName");
        address.latitude = document.getDouble("address.latitude");
        address.longitude = document.getDouble("address.longitude");
        address.northeastLatitude = document.getDouble("address.northeastLatitude");
        address.northeastLongitude = document.getDouble("address.northeastLongitude");
        address.southwestLatitude = document.getDouble("address.southwestLatitude");
        address.southwestLongitude = document.getDouble("address.southwestLongitude");

        user.photos = ConvertHelper.stringsToArrayList(document.getData().get("photos").toString());
        user.name = document.getString("name");
        user.surname = document.getString("surname");
        user.about = document.getString("about");
        user.gender = Byte.parseByte(String.valueOf(document.getLong("gender")));

        return user;
    }
}
