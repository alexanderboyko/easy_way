package boyko.alex.easy_way.backend;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import boyko.alex.easy_way.backend.models.Category;
import boyko.alex.easy_way.backend.models.ItemType;
import boyko.alex.easy_way.backend.models.PriceType;
import boyko.alex.easy_way.backend.models.User;

/**
 * Created by Sasha on 20.11.2017.
 */

public class DataMediator {
    private static User user;
    private static ArrayList<Category> categories;
    private static ArrayList<ItemType> itemTypes;
    private static ArrayList<PriceType> priceTypes;

    private static void init() {
        categories = new ArrayList<>();
        itemTypes = new ArrayList<>();
        priceTypes = new ArrayList<>();
        user = new User();
    }

    private static void checkInit() {
        if (categories == null) init();
    }

    public static void setUser(User user1) {
        user = user1;
    }

    public static void setCategories(ArrayList<Category> categories1) {
        checkInit();
        categories = categories1;
    }

    public static void setItemTypes(ArrayList<ItemType> itemTypes1) {
        checkInit();
        itemTypes = itemTypes1;
    }

    public static void setPriceTypes(ArrayList<PriceType> priceTypes1) {
        checkInit();
        priceTypes = priceTypes1;
    }

    public static ArrayList<Category> getCategories() {
        checkInit();
        return categories;
    }

    public static ArrayList<ItemType> getItemTypes() {
        checkInit();
        return itemTypes;
    }

    public static ArrayList<PriceType> getPriceTypes() {
        checkInit();
        return priceTypes;
    }

    public static User getUser() {
        return user;
    }

    @Nullable
    public static Category getCategory(String id) {
        checkInit();
        for (Category category : categories) {
            if (category.id.equals(id)) return category;
        }
        return null;
    }

    @Nullable
    public static ItemType getItemType(String id) {
        checkInit();
        for (ItemType itemType : itemTypes) {
            if (itemType.id.equals(id)) return itemType;
        }
        return null;
    }

    @Nullable
    public static PriceType getPriceType(String id) {
        checkInit();
        for (PriceType priceType : priceTypes) {
            if (priceType.id.equals(id)) return priceType;
        }
        return null;
    }

    public static void printData() {
        for (Category category : categories)
            Log.i("DATA", category.id + " " + category.name + " " + category.iconUrl + " " + category.parentCategoryId);
        for (ItemType itemType : itemTypes)
            Log.i("DATA", itemType.id + " " + itemType.name);
        for (PriceType priceType : priceTypes)
            Log.i("DATA", priceType.id + " " + priceType.name + " " + priceType.shortName);
    }

}
