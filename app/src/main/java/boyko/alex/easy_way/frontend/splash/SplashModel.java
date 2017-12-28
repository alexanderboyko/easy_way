package boyko.alex.easy_way.frontend.splash;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.models.Category;
import boyko.alex.easy_way.backend.models.Dialog;
import boyko.alex.easy_way.backend.models.ItemType;
import boyko.alex.easy_way.backend.models.Like;
import boyko.alex.easy_way.backend.models.PriceType;
import boyko.alex.easy_way.frontend.login.LoginHelper;

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
    private boolean userLoaded = false;
    private boolean likesLoaded = false;
    private boolean dialogsLoaded = false;

    private boolean firstDialogLoaded = false, secondDialogLoaded = false;
    private QuerySnapshot querySnapshot1, querySnapshot2;

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

        db.collection("user")
                .whereEqualTo("email", LoginHelper.getCurrentUserEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().getDocuments().isEmpty()) {
                                DataMediator.setUser(ConvertHelper.convertToUser(task.getResult().getDocuments().get(0)));
                            }
                            loadDialogs();
                            userLoaded = true;
                            checkLoadingFinished();
                        } else {
                            Log.w(LOG_TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        db.collection("likes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Like> likes = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Like like = new Like();
                                like.id = document.getId();
                                like.itemId = document.getString("itemId");
                                like.userId = document.getString("userId");
                                likes.add(like);
                            }
                            DataMediator.setLikes(likes);
                            likesLoaded = true;
                            checkLoadingFinished();
                        } else {
                            Log.w(LOG_TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    private void loadDialogs() {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("dialogs");
        Query query = collectionReference.whereEqualTo("user1Id", DataMediator.getUser().id);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    querySnapshot1 = task.getResult();
                    firstDialogLoaded = true;
                    filterDialogs();
                } else {
                    querySnapshot1 = null;
                    firstDialogLoaded = true;
                }
            }
        });

        query = collectionReference.whereEqualTo("user2Id", DataMediator.getUser().id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    querySnapshot2 = task.getResult();
                    secondDialogLoaded = true;
                    filterDialogs();
                } else {
                    querySnapshot2 = null;
                    secondDialogLoaded = true;
                }
            }
        });
    }

    private void filterDialogs() {
        if (firstDialogLoaded && secondDialogLoaded && querySnapshot1 != null && querySnapshot2 != null) {
            ArrayList<Dialog> dialogs = new ArrayList<>();
            for (DocumentSnapshot document : querySnapshot1) {
                Dialog dialog = ConvertHelper.convertToDialog(document);
                if (dialog.user1Id.equals(dialog.user2Id)) continue;
                dialogs.add(dialog);
            }
            for (DocumentSnapshot document : querySnapshot2) {
                dialogs.add(ConvertHelper.convertToDialog(document));
            }

            DataMediator.setDialogs(dialogs);
            dialogsLoaded = true;
            checkLoadingFinished();
        }
    }

    private void checkLoadingFinished() {
        if (categoriesLoaded && priceTypesLoaded && itemTypesLoaded && userLoaded && likesLoaded && dialogsLoaded) {
            splashPresenter.loadingFinished();
        }
    }

}
