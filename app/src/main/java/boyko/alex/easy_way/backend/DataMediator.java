package boyko.alex.easy_way.backend;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import boyko.alex.easy_way.backend.models.Category;
import boyko.alex.easy_way.backend.models.ItemType;
import boyko.alex.easy_way.backend.models.Like;
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
    private static ArrayList<Like> likes;

    public static void setUser(User user1) {
        user = user1;
    }

    public static void setCategories(ArrayList<Category> categories1) {
        categories = categories1;
    }

    public static void setItemTypes(ArrayList<ItemType> itemTypes1) {
        itemTypes = itemTypes1;
    }

    public static void setPriceTypes(ArrayList<PriceType> priceTypes1) {
        priceTypes = priceTypes1;
    }

    public static void setLikes(ArrayList<Like> likes1) {
        likes = likes1;
    }

    public static ArrayList<Category> getCategories() {
        return categories;
    }

    public static ArrayList<ItemType> getItemTypes() {
        return itemTypes;
    }

    public static ArrayList<PriceType> getPriceTypes() {
        return priceTypes;
    }

    public static ArrayList<Like> getLikes() {
        return likes;
    }

    public static User getUser() {
        return user;
    }

    @Nullable
    public static Category getCategory(String id) {
        if (categories != null) {
            for (Category category : categories) {
                if (category.id.equals(id)) return category;
            }
        }
        return null;
    }

    public static ArrayList<Category> getChildCategories(String parentCategoryId) {
        if(categories == null || parentCategoryId == null) return new ArrayList<>();
        ArrayList<Category> temp = new ArrayList<>();
        for (Category category : categories) {
            if (category.parentCategoryId.equals(parentCategoryId)) temp.add(category);
        }
        return temp;
    }

    public static ArrayList<Category> getParentsCategories() {
        if(categories == null) return new ArrayList<>();

        ArrayList<Category> temp = new ArrayList<>();
        for (Category category : categories) {
            if (category.parentCategoryId == null || category.parentCategoryId.isEmpty()) temp.add(category);
        }
        return temp;
    }



    @Nullable
    public static ItemType getItemType(String id) {
        if (itemTypes != null) {
            for (ItemType itemType : itemTypes) {
                if (itemType.id.equals(id)) return itemType;
            }
        }
        return null;
    }

    @Nullable
    public static PriceType getPriceType(String id) {
        if (priceTypes != null) {
            for (PriceType priceType : priceTypes) {
                if (priceType.id.equals(id)) return priceType;
            }
        }
        return null;
    }

    @Nullable
    public static PriceType getPriceTypeByName(String name) {
        if (priceTypes != null) {
            for (PriceType priceType : priceTypes) {
                if (priceType.name.equals(name)) return priceType;
            }
        }
        return null;
    }

    @Nullable
    public static Like getLike(String itemId) {
        if (likes != null) {
            for (Like like : likes) {
                if (like.itemId.equals(itemId)) {
                    return like;
                }
            }
        }
        return null;
    }

    public static void removeLike(String likeId) {
        if (likes != null) {
            for (Like like : likes) {
                if (like.id.equals(likeId)) {
                    likes.remove(like);
                    break;
                }
            }
        }
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
