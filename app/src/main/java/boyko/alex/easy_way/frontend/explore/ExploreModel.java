package boyko.alex.easy_way.frontend.explore;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.DummyGenerator;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Category;
import boyko.alex.easy_way.backend.models.Item;

/**
 * Created by Sasha on 05.11.2017.
 */

class ExploreModel {
    private final String LOG_TAG = getClass().getSimpleName();
    private ExplorePresenter presenter;
    private static ExploreModel model;
    private Query query;
    private DocumentSnapshot lastLoadedItem;
    private boolean allDataLoaded = false;

    private ExploreModel(ExplorePresenter presenter) {
        this.presenter = presenter;
    }

    static ExploreModel getInstance(ExplorePresenter presenter) {
        if (model == null) {
            model = new ExploreModel(presenter);
        }
        return model;
    }

    void startLoading() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //for(int i = 0; i<3;i++) db.collection("items").add(DummyGenerator.getDummyItem());
        query = db.collection("items").limit(20);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().size() != 0) {
                        lastLoadedItem = task.getResult().getDocuments()
                                .get(task.getResult().size() - 1);
                    }

                    ArrayList<Object> items = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        items.add(ConvertHelper.convertToItem(document));
                    }
                    if(items.size() == 20) items.add("loading");
                    loadingFinished(items);
                } else {
                    Log.w(LOG_TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    void startLoadingNext() {
        if (!allDataLoaded) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            query = db.collection("items").limit(20).startAfter(lastLoadedItem);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if(task.getResult().size() != 0){
                            lastLoadedItem = task.getResult().getDocuments()
                                    .get(task.getResult().size() - 1);


                            ArrayList<Object> items = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                items.add(ConvertHelper.convertToItem(document));
                            }
                            items.add("loading");
                            loadingNextFinished(items);
                        }else{
                            allDataLoaded = false;
                        }
                    } else {
                        Log.w(LOG_TAG, "Error getting documents.", task.getException());
                    }
                }
            });
        }
    }

    private void loadingFinished(ArrayList<Object> items) {
        presenter.loadingFinished(items);
        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        //for(int i = 0; i<50;i++) db.collection("items").add(DummyGenerator.getDummyItem());
    }

    private void loadingNextFinished(ArrayList<Object> items) {
        presenter.loadingNextFinished(items);
    }
}
