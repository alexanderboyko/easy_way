package boyko.alex.easy_way.backend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Item;

/**
 * Created by Sasha on 04.11.2017.
 */

public class DummyGenerator {
    private static long itemsGenerated = 0;
    private static long usersGenerated = 0;
    private static long bookingsGenerated = 0;
    private static long reviewsGenerated = 0;


    private static long TIME = Calendar.getInstance().getTimeInMillis();
    private static Random random = new Random(TIME);
    private static String itemPhotoUrl1 = "https://3xdhz2pebjxel8j01uc6vr19-wpengine.netdna-ssl.com/wp-content/uploads/2016/03/3DR-Solo-Crop2.jpg";
    private static String itemPhotoUrl2 = "https://3xdhz2pebjxel8j01uc6vr19-wpengine.netdna-ssl.com/wp-content/uploads/2016/03/Phantom-4-Drone.jpg";
    private static String itemPhotoUrl3 = "https://www.parrot.com/us/sites/default/files/styles/product_teaser_hightlight/public/parrot_ar_drone_gps_edition.png?itok=0shlzcXW";
    private static String itemPhotoUrl4 = "https://preska.wideinfo.org/wp-content/uploads/2017/09/bebop_drone_2_packshot_white_front_01_no_bkgd-2.png";
    private static String itemPhotoUrl5 = "https://target.scene7.com/is/image/Target/50906088";
    private static String itemPhotoUrl6 = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQCHsqKc45epcC7lfU-7ZBq4RlBkVOoXCAOFVPOhgN52EnCW-6m";
    private static String itemPhotoUrl7 = "https://assets.pcmag.com/media/images/524599-panasonic-lumix-dmc-g7.jpg?thumb=y";
    private static String itemPhotoUrl8 = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSZIYH3sLUcQuzzaiA4ObyRH-h5uezQb4D1nmBqvfxx-KtKlQFy";
//
//    /**
//     * ITEMS
//     */
//
    public static Item getDummyItem() {
        int nextRandom = (int)itemsGenerated + random.nextInt(100);

        Item item = new Item();
        item.price = 10.0f + (random.nextFloat()*10f);
        item.title = "Some item " + nextRandom;
        item.priceTypeId = DataMediator.getPriceTypes().get(random.nextInt(DataMediator.getPriceTypes().size())).id;
        item.categoryId = DataMediator.getCategories().get(random.nextInt(DataMediator.getCategories().size())).id;
        item.photos = getRandomPhotos();
        item.mainPhoto = getRandomItemPhoto();
        item.address = getAddress();
        item.description = ApplicationController.getInstance().getString(R.string.default_very_long);
        item.notes = ApplicationController.getInstance().getString(R.string.default_long);
        item.ownerId = "PbILqY0GA6sjqIS727Tz";

        itemsGenerated++;
        return item;
    }
//
//    public static ItemExpanded getDummyItemExpanded(Item itemBase) {
//        ItemExpanded itemExtended = new ItemExpanded(itemBase);
//        itemExtended.description = "Some stuff to share";
//        itemExtended.notes = "Some notes here";
//        ArrayList<String> photo = new ArrayList<>();
//        photo.add(getRandomItemPhoto());
//        itemExtended.user = getDummyUserBase();
//        itemExtended.photosUrls = photo;
//        return itemExtended;
//    }
//
    public static ArrayList<Item> getDummyItems() {
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) items.add(getDummyItem());
        return items;
    }
//
//    /**
//     * USERS
//     */
//    public static User getDummyUserBase() {
//        User user = new User();
//        user.id = random.nextInt(100);
//        user.name = "Alex";
//        user.surname = "Boyko";
//
//
//        usersGenerated++;
//        return user;
//    }
//
//    public static UserExpanded getDummyUserExpanded(User userBase) {
//        UserExpanded userExpanded = new UserExpanded(userBase);
//        userExpanded.about = "About info here About info here About info here About info here About info here";
//        userExpanded.gender = (byte) 1;
//        userExpanded.birthday = TIME;
//        userExpanded.address = getAddress();
//        userExpanded.photo = getRandomPhotos();
//        userExpanded.items = getDummyItemsBase();
//        userExpanded.reviews = getDummyReviews();
//        usersGenerated++;
//        return userExpanded;
//    }
//
//    public static UserSystem getDummyUserSystem() {
//        UserSystem userSystem = new UserSystem(getDummyUserExpanded(getDummyUserBase()));
//        userSystem.likedItems = getDummyItemsBase();
//        usersGenerated++;
//        return userSystem;
//    }
//
//
    /**
     * PHOTOS
     */

    private static String getRandomItemPhoto() {
        switch (random.nextInt(8) + 1) {
            case 1:
                return itemPhotoUrl1;
            case 2:
                return itemPhotoUrl2;
            case 3:
                return itemPhotoUrl3;
            case 4:
                return itemPhotoUrl4;
            case 5:
                return itemPhotoUrl5;
            case 6:
                return itemPhotoUrl6;
            case 7:
                return itemPhotoUrl7;
            case 8:
                return itemPhotoUrl8;
        }
        return null;
    }

    private static ArrayList<String> getRandomPhotos() {
        ArrayList<String> photos = new ArrayList<>();
        int n = random.nextInt(3) + 1;
        for (int i = 0; i < n; i++) {
            photos.add(getRandomItemPhoto());
        }
        return photos;
    }
//
//    /**
//     * REVIEWS
//     */
//
//    public static Review getDummyReview() {
//        Review review = new Review();
//        review.id = reviewsGenerated;
//        review.booking = getDummyBookingBase();
//        review.item = getDummyItem();
//        review.mark = 4;
//        review.userFrom = getDummyUserBase();
//        review.text = ApplicationController.getInstance().getString(R.string.default_very_long);
//
//        reviewsGenerated++;
//
//        return review;
//    }
//
//    public static ArrayList<Review> getDummyReviews() {
//        ArrayList<Review> reviews = new ArrayList<>();
//        int n = random.nextInt(10) + 1;
//        for (int i = 0; i < n; i++) reviews.add(getDummyReview());
//        return reviews;
//    }
//
//
//    /**
//     * BOOKINGS
//     */
//
//    public static Booking getDummyBookingBase() {
//        Booking bookingBase = new Booking();
//        bookingBase.id = bookingsGenerated;
//        bookingBase.itemId = itemsGenerated;
//        bookingBase.isStarted = false;
//        bookingBase.isCanceled = false;
//        bookingBase.isFinished = false;
//        bookingBase.createdAt = TIME;
//        bookingBase.endAt = DateHelper.getNextDayTime(TIME);
//        bookingBase.startedAt = TIME;
//        bookingBase.updatedAt = TIME;
//
//        bookingsGenerated++;
//
//        return bookingBase;
//    }
//
//    public static ArrayList<Booking> getDummyBookingsBase() {
//        ArrayList<Booking> bookingsBase = new ArrayList<>();
//        int n = random.nextInt(4);
//        for (int i = 0; i < n; i++) bookingsBase.add(getDummyBookingBase());
//        return bookingsBase;
//    }
//
//    public static BookingExpanded getDummyBookingExpanded() {
//        BookingExpanded bookingExpanded = new BookingExpanded(getDummyBookingBase());
//        bookingExpanded.item = getDummyItem();
//        bookingExpanded.owner = getDummyUserBase();
//        bookingExpanded.client = getDummyUserBase();
//
//        return bookingExpanded;
//    }
//
//    public static ArrayList<BookingExpanded> getDummyBookingsExpanded() {
//        ArrayList<BookingExpanded> bookingsExpanded = new ArrayList<>();
//        int n = random.nextInt();
//        for (int i = 0; i < n; i++) bookingsExpanded.add(getDummyBookingExpanded());
//        return bookingsExpanded;
//    }
//
//    /**
//     * ADDRESS
//     */
    private static Address getAddress() {
        Address address = new Address();
        address.name = "Lublin";
        address.fullName = "Lublin, Poland";
        address.latitude = 51.246453599999995;
        address.longitude = 22.568446299999998;
        address.southwestLatitude = 51.1397334;
        address.southwestLongitude = 22.454009199999998;
        address.northeastLatitude = 51.2964846;
        address.northeastLongitude = 22.6735312;
        return address;
    }
//
//    private static Category getCategory() {
//        Category category = new Category();
//        category.id = random.nextInt(100);
//        category.name = "Category " + (random.nextInt(5) + 1);
//        return category;
//    }
//
//    private static PriceType getPriceType() {
//        PriceType priceType = new PriceType();
//        priceType.id = random.nextInt(100);
//        priceType.name = "per day";
//        priceType.shortName = "day";
//        return priceType;
//    }
//
//    private static ItemType getItemType() {
//        ItemType itemType = new ItemType();
//        itemType.id = random.nextInt(100);
//        itemType.name = "stuff";
//        return itemType;
//    }
}
