package boyko.alex.easy_way.backend;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.places.Place;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Booking;
import boyko.alex.easy_way.backend.models.Dialog;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.Review;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.libraries.DateHelper;

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

    public static Item convertToItem(DocumentSnapshot document){
        try {


            Item item = new Item();
            item.id = document.getId();
            item.title = document.getString("title");
            item.description = document.getString("description");
            item.notes = document.getString("notes");
            item.price = document.getDouble("price");
            item.ratingSum = document.getDouble("ratingSum");
            item.ratingAverage = document.getDouble("ratingAverage");
            item.categoryId = document.getString("categoryId");
            item.priceTypeId = document.getString("priceTypeId");
            item.createdAt = document.getLong("createdAt");

            Address address = new Address();
            address.id = document.getString("address.id");
            address.name = document.getString("address.name");
            address.fullName = document.getString("address.fullName");
            address.latitude = document.getDouble("address.latitude");
            address.longitude = document.getDouble("address.longitude");
            address.northeastLatitude = document.getDouble("address.northeastLatitude");
            address.northeastLongitude = document.getDouble("address.northeastLongitude");
            address.southwestLatitude = document.getDouble("address.southwestLatitude");
            address.southwestLongitude = document.getDouble("address.southwestLongitude");

            item.address = address;
            item.ownerId = document.getString("ownerId");
            item.mainPhoto = document.getString("mainPhoto");
            if (document.getData().get("photos") != null)
                item.photos = ConvertHelper.stringsToArrayList(document.getData().get("photos").toString());
            return item;
        }catch (Exception e){
            return null;
        }
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
        booking.isConfirmed = document.getBoolean("isConfirmed");
        booking.priceTypeId = document.getString("priceTypeId");
        return booking;
    }

    public static Review convertToReview(DocumentSnapshot document) {
        Review review = document.toObject(Review.class);
        review.id = document.getId();
        return review;
    }

    public static User convertToUser(DocumentSnapshot document) {
        User user = new User();
        user.id = document.getId();
        user.birthday = document.getLong("birthday");

        Address address = new Address();
        address.id = document.getString("address.id");
        address.name = document.getString("address.name");
        address.fullName = document.getString("address.fullName");
        address.latitude = document.getDouble("address.latitude");
        address.longitude = document.getDouble("address.longitude");
        address.northeastLatitude = document.getDouble("address.northeastLatitude");
        address.northeastLongitude = document.getDouble("address.northeastLongitude");
        address.southwestLatitude = document.getDouble("address.southwestLatitude");
        address.southwestLongitude = document.getDouble("address.southwestLongitude");

        user.address = address;
        user.email = document.getString("email");
        //ser.photo = ConvertHelper.stringsToArrayList(document.getData().get("photo").toString());
        user.photo = document.getString("photo");
        user.name = document.getString("name");
        user.surname = document.getString("surname");
        user.about = document.getString("about");
        user.gender = Byte.parseByte(String.valueOf(document.getLong("gender")));

        return user;
    }

    public static ArrayList<Event> convertBookingsToEvents(ArrayList<Booking> bookings) {
        ArrayList<Event> events = new ArrayList<>();
        for (Booking booking : bookings) {
            if(!booking.isCanceled) events.addAll(getEventsFromTimeRange(booking.startedAt, booking.endAt));
        }
        return events;
    }

    private static ArrayList<Event> getEventsFromTimeRange(long startTime, long endTime) {
        ArrayList<Event> events = new ArrayList<>();
        if (DateHelper.ifTimesFromOneDay(startTime, endTime)) {
            events.add(new Event(ContextCompat.getColor(ApplicationController.getInstance(), R.color.color_accent), startTime));
            return events;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startTime);
            events.add(new Event(ContextCompat.getColor(ApplicationController.getInstance(), R.color.color_accent), calendar.getTimeInMillis()));
            while (true) {
                calendar.add(Calendar.DATE, 1);
                events.add(new Event(ContextCompat.getColor(ApplicationController.getInstance(), R.color.color_accent), calendar.getTimeInMillis()));
                if (DateHelper.ifTimesFromOneDay(calendar.getTimeInMillis(), endTime)) {
                    break;
                }
            }
            return events;
        }
    }

    public static ArrayList<Booking> getBookingsForDate(ArrayList<Booking> bookings, long date) {
        ArrayList<Booking> dayBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (DateHelper.ifTimesFromOneDay(booking.startedAt, booking.endAt) && DateHelper.ifTimesFromOneDay(booking.startedAt, date)) {
                dayBookings.add(booking);
                continue;
            }
            if (DateHelper.ifTimesFromOneDay(booking.startedAt, date) || DateHelper.ifTimesFromOneDay(booking.endAt, date)) {
                dayBookings.add(booking);
                continue;
            }

            if (DateHelper.isDayInRange(date, booking.startedAt, booking.endAt)) {
                dayBookings.add(booking);
            }
        }
        return dayBookings;
    }

    public static Address convertPlaceToAddress(Place place) {
        Address address = new Address();
        if (place.getId() != null) address.id = place.getId();
        if (place.getName() != null) address.name = place.getName().toString();
        if (place.getAddress() != null) address.fullName = place.getAddress().toString();
        if (place.getLatLng() != null) {
            address.latitude = place.getLatLng().latitude;
            address.longitude = place.getLatLng().longitude;
        }
        if (place.getViewport() != null) {
            address.southwestLatitude = place.getViewport().southwest.latitude;
            address.southwestLongitude = place.getViewport().southwest.longitude;
            address.northeastLatitude = place.getViewport().northeast.latitude;
            address.northeastLongitude = place.getViewport().northeast.longitude;
        }
        return address;
    }

    public static User convertFacebookJSONToUser(JSONObject object) {
        User user = new User();
        if (object != null) {
            try {
                //Log.i(LOG_TAG, object.toString());
                if (object.getString("first_name") != null) {
                    user.name = object.getString("first_name");
                }
                if (object.getString("last_name") != null) {
                    user.surname = object.getString("last_name");
                }
                if (object.getString("gender") != null) {
                    user.gender = object.getString("gender").equals("male") ? 1 : 0;
                }
                if (object.getString("birthday") != null) {
                    try {
                        String dateString = "30/09/2014";
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date date = sdf.parse(dateString);
                        user.birthday = date.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e){
                return user;
            }
        }
        return user;
    }

    public static User convertGoogleResponseToUser(GoogleSignInAccount googleSignInAccount) {
        User user = new User();
        if(googleSignInAccount != null){
            if(googleSignInAccount.getEmail() != null) user.email = googleSignInAccount.getEmail();
        }
        return user;
    }

    public static Dialog convertToDialog(DocumentSnapshot document) {
        Dialog dialog = document.toObject(Dialog.class);
        dialog.id = document.getId();
        return dialog;
    }
}
