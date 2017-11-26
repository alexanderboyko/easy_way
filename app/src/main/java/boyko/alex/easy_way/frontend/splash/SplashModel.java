package boyko.alex.easy_way.frontend.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.DummyGenerator;
import boyko.alex.easy_way.backend.SharedPreferencesStorage;
import boyko.alex.easy_way.backend.models.Category;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.ItemType;
import boyko.alex.easy_way.backend.models.PriceType;

import static boyko.alex.easy_way.backend.ConfigLoginParser.BUNDLE_KEY_TOKEN;
import static boyko.alex.easy_way.backend.ConfigLoginParser.BUNDLE_KEY_USERID;

/**
 * Created by PNazar on 30.05.2017.
 * <p>
 * Localised model (MVP) for ic_splash_logo screen. Interfaces the presenter with app backend. Follows a singleton pattern, so is not regenerated on config
 * changes. Does not cache data locally as there is no need to do that in here.
 */

class SplashModel {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private static SplashModel instanceOfSelf;
    private SplashPresenter splashPresenter;

    private boolean categoriesLoaded = false;
    private boolean itemTypesLoaded = false;
    private boolean priceTypesLoaded = false;

    private SplashModel(SplashPresenter instantiatingPresenter) {
        splashPresenter = instantiatingPresenter;
    }

    static SplashModel getInstance(SplashPresenter instantiatingPresenter) {
        if (instanceOfSelf == null) {
            instanceOfSelf = new SplashModel(instantiatingPresenter);
        }
        return instanceOfSelf;
    }

    void startLoadingData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Category> categories = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Category category = new Category();
                                category.id = document.getId();
                                category.name = document.getString("name");
                                category.iconUrl = document.getString("photoUrl");
                                category.parentCategoryId = document.getString("parentCategoryId");
                                categories.add(category);
                            }
                            DataMediator.setCategories(categories);
                            categoriesLoaded = true;
                            checkLoadingFinished();
                        } else {
                            Log.w(LOG_TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        db.collection("itemType")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<ItemType> itemTypes = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                ItemType itemType = new ItemType();
                                itemType.id = document.getId();
                                itemType.name = document.getString("name");
                                itemTypes.add(itemType);
                            }
                            DataMediator.setItemTypes(itemTypes);
                            itemTypesLoaded = true;
                            checkLoadingFinished();
                        } else {
                            Log.w(LOG_TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        db.collection("priceType")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<PriceType> priceTypes = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                PriceType priceType = new PriceType();
                                priceType.id = document.getId();
                                priceType.name = document.getString("name");
                                priceType.shortName = document.getString("shortName");
                                priceTypes.add(priceType);
                            }
                            DataMediator.setPriceTypes(priceTypes);
                            priceTypesLoaded = true;
                            checkLoadingFinished();
                        } else {
                            Log.w(LOG_TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void checkLoadingFinished(){
        if(categoriesLoaded && priceTypesLoaded && itemTypesLoaded){
            splashPresenter.loadingFinished();
        }
    }

    //Check if an account is logged in, returns boolean
    boolean checkAccountLogin() {
        return AccessToken.getCurrentAccessToken() != null;

//        Bundle credentialsBundle = SharedPreferencesStorage.getInstance().readUserCredentials();
//        return (credentialsBundle.getString(BUNDLE_KEY_USERID) != null) && (credentialsBundle.getString(BUNDLE_KEY_TOKEN) != null);
    }

    //TODO: Add login validity check with server to facilitate token/account invalidation

    void addAccountInformation(Bundle loginData) {
        String userId = loginData.getString(BUNDLE_KEY_USERID);
        String userToken = loginData.getString(BUNDLE_KEY_TOKEN);
        SharedPreferencesStorage.getInstance().writeUserCredentials(userId, userToken);
        //TODO: Consider overwriting variables with junk for increased security?
    }
}
