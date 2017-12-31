package boyko.alex.easy_way.frontend.explore;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Item;

/**
 * Created by Sasha on 05.11.2017.
 *
 * Explore uploading here
 */

class ExploreModel {
    private final String LOG_TAG = getClass().getSimpleName();
    private ExplorePresenter presenter;
    private static ExploreModel model;
    private Query query;
    private DocumentSnapshot lastLoadedItem;
    private boolean allDataLoaded = false;

    private String categoryId;
    private Address address;
    private double priceFrom, priceTo;
    private Query.Direction order;

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
        //for(int i = 0; i<8;i++) db.collection("items").add(DummyGenerator.getDummyItem());
        boolean ordered = false;
        query = db.collection("items").limit(20);
        if (categoryId != null) {
            query = query.whereEqualTo("categoryId", categoryId);
        }
        if (address != null) {
            query = query.whereEqualTo("address.id", address.id);
        }

        if (priceFrom != 0 || priceTo != 0) {
            ordered = true;
            if (priceFrom != 0 && priceTo != 0) {
                if(order != null){
                    if(order.equals(Query.Direction.ASCENDING)){
                        query = query.whereGreaterThan("price", priceFrom).whereLessThan("price", priceTo).orderBy("price", Query.Direction.ASCENDING);
                    }else{
                        query = query.whereGreaterThan("price", priceFrom).whereLessThan("price", priceTo).orderBy("price", Query.Direction.DESCENDING);
                    }
                }else{
                    query = query.whereGreaterThan("price", priceFrom).whereLessThan("price", priceTo).orderBy("price", Query.Direction.DESCENDING);
                }

            }else{
                if (priceFrom != 0) {
                    if(order != null){
                        if(order.equals(Query.Direction.ASCENDING)){
                            query = query.whereGreaterThan("price", priceFrom).orderBy("price", Query.Direction.ASCENDING);
                        }else{
                            query = query.whereGreaterThan("price", priceFrom).orderBy("price", Query.Direction.DESCENDING);
                        }
                    }else{
                        query = query.whereGreaterThan("price", priceFrom).orderBy("price", Query.Direction.DESCENDING);
                    }
                }

                if (priceTo != 0) {
                    if(order != null){
                        if(order.equals(Query.Direction.ASCENDING)){
                            query = query.whereLessThan("price", priceTo).orderBy("price", Query.Direction.ASCENDING);
                        }else{
                            query = query.whereLessThan("price", priceTo).orderBy("price", Query.Direction.DESCENDING);
                        }
                    }else{
                        query = query.whereLessThan("price", priceTo).orderBy("price", Query.Direction.DESCENDING);
                    }

                }
            }

        }

        if (order == null) {
            query = query.orderBy("createdAt", Query.Direction.DESCENDING);
        } else {
            if(!ordered) {
                if (order == Query.Direction.DESCENDING) {
                    query = query.orderBy("price", Query.Direction.DESCENDING);
                } else {
                    query = query.orderBy("price", Query.Direction.ASCENDING);
                }
            }
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() != 0) {
                        lastLoadedItem = task.getResult().getDocuments()
                                .get(task.getResult().size() - 1);
                    }

                    ArrayList<Object> items = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        items.add(ConvertHelper.convertToItem(document));
                    }
                    if (items.size() >= 20) items.add("loading");
                    else allDataLoaded = true;
                    loadingFinished(items);
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
                        if (task.getResult().size() != 0) {
                            lastLoadedItem = task.getResult().getDocuments()
                                    .get(task.getResult().size() - 1);


                            ArrayList<Object> items = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                items.add(ConvertHelper.convertToItem(document));
                            }
                            items.add("loading");
                            loadingNextFinished(items);
                        } else {
                            allDataLoaded = false;
                        }
                    }
                }
            });
        }
    }

    private void loadingFinished(ArrayList<Object> items) {
        for (Object object : items) {
            if (object instanceof Item) {
                if (DataMediator.getLike(((Item) object).id) != null) {
                    ((Item) object).isLiked = true;
                }
            }
        }
        presenter.loadingFinished(items);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        for(int i = 0; i<10;i++) db.collection("items").add(DummyGenerator.getDummyItem());
    }

    private void loadingNextFinished(ArrayList<Object> items) {
        for (Object object : items) {
            if (object instanceof Item) {
                if (DataMediator.getLike(((Item) object).id) != null)
                    ((Item) object).isLiked = true;
            }
        }
        presenter.loadingNextFinished(items);
    }

    String getCategoryId() {
        return categoryId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }


    double getPriceFrom() {
        return priceFrom;
    }

    double getPriceTo() {
        return priceTo;
    }

    Query.Direction getOrder() {
        return order;
    }

    void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    void setPriceFrom(double priceFrom) {
        this.priceFrom = priceFrom;
    }

    void setPriceTo(double priceTo) {
        this.priceTo = priceTo;
    }

    void setOrder(Query.Direction order) {
        this.order = order;
    }

}
