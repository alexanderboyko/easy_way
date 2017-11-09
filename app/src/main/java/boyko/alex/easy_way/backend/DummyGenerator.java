package boyko.alex.easy_way.backend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Category;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.ItemType;
import boyko.alex.easy_way.backend.models.PriceType;
import boyko.alex.easy_way.backend.models.User;

/**
 * Created by Sasha on 04.11.2017.
 */

public class DummyGenerator {
    private static long itemsGenerated = 0;
    private static long usersGenerated = 0;

    private static long TIME = Calendar.getInstance().getTimeInMillis();
    private static Random random = new Random(TIME);
    private static String itemPhotoUrl1 = "https://3xdhz2pebjxel8j01uc6vr19-wpengine.netdna-ssl.com/wp-content/uploads/2016/03/3DR-Solo-Crop2.jpg";
    private static String itemPhotoUrl2 = "https://3xdhz2pebjxel8j01uc6vr19-wpengine.netdna-ssl.com/wp-content/uploads/2016/03/Phantom-4-Drone.jpg";
    private static String itemPhotoUrl3 = "https://www.parrot.com/us/sites/default/files/styles/product_teaser_hightlight/public/parrot_ar_drone_gps_edition.png?itok=0shlzcXW";
    private static String itemPhotoUrl4 = "https://preska.wideinfo.org/wp-content/uploads/2017/09/bebop_drone_2_packshot_white_front_01_no_bkgd-2.png";
    private static String itemPhotoUrl5 = "https://target.scene7.com/is/image/Target/50906088";
    private static String itemPhotoUrl6 = "https://brain-images-ssl.cdn.dixons.com/9/4/10026049/l_10026049_002.jpg";
    private static String itemPhotoUrl7 = "https://cdn.thewirecutter.com/wp-content/uploads/2016/10/mirrorless-camera-fujifilm-x-t2-lowres-2024-570x380.jpg";
    private static String itemPhotoUrl8 = "https://www.bhphotovideo.com/images/categoryImages/desktop/325x325/21008-Mirrorless-System-Cameras.jpg";

    private static Category getCategory() {
        Category category = new Category();
        category.setId(random.nextInt(100));
        category.setName("Category " + (random.nextInt(5) + 1));
        return category;
    }

    private static PriceType getPriceType() {
        PriceType priceType = new PriceType();
        priceType.setId(random.nextInt(100));
        priceType.setName("per day");
        priceType.setShortName("day");
        return priceType;
    }

    private static ItemType getItemType() {
        ItemType itemType = new ItemType();
        itemType.setId(random.nextInt(100));
        itemType.setName("stuff");
        return itemType;
    }

    public static Item getItem() {
        Item item = new Item();
        item.setId(random.nextInt(100));
        item.setCategory(getCategory());
        item.setDescription("Some stuff to share");
        item.setItemType(getItemType());
        item.setNotes("Some notes here");
        item.setPrice(50.0f);
        item.setTitle("Some item " + itemsGenerated);
        item.setUser(getUser());
        item.setPriceType(getPriceType());

        ArrayList<String> photos = new ArrayList<>();
        photos.add(getRandomItemPhoto());
        item.setPhotoUrls(photos);

        itemsGenerated++;
        return item;
    }

    public static User getUser() {
        User user = new User();
        user.setId(random.nextInt(100));
        user.setName("Alex");
        user.setSurname("Boyko");
        user.setAbout("About info here About info here About info here About info here About info here");
        user.setGender((byte) 1);
        user.setBirthday(TIME);
        user.setAddress(getAddress());

        usersGenerated++;
        return user;
    }

    private static Address getAddress() {
        Address address = new Address();
        address.setId(random.nextInt(100));
        address.setName("Lublin, Poland");
        return address;
    }

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

    public static ArrayList<Item> getItems() {
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) items.add(getItem());
        return items;
    }

}
