package boyko.alex.easy_way.frontend.explore;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import boyko.alex.easy_way.backend.DummyGenerator;

/**
 * Created by Sasha on 05.11.2017.
 */

class ExploreModel {
    private ExplorePresenter presenter;
    private static ExploreModel model;

    private ExploreModel(ExplorePresenter presenter){
        this.presenter = presenter;
    }

    static ExploreModel getInstance(ExplorePresenter presenter){
        if(model == null){
            model = new ExploreModel(presenter);
        }
        return model;
    }

    void startLoading(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<Object> items = new ArrayList<Object>();
                items.addAll(DummyGenerator.getDummyItemsBase());
                items.add("loading");
                loadingFinished(items);
            }
        }, 500);
    }

    void startLoadingNext(){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<Object> items = new ArrayList<Object>();
                items.addAll(DummyGenerator.getDummyItemsBase());
                items.add("loading");
                loadingNextFinished(items);
            }
        }, 500);
    }

    private void loadingFinished(ArrayList<Object> items){
        presenter.loadingFinished(items);
    }

    private void loadingNextFinished(ArrayList<Object> items){
        presenter.loadingNextFinished(items);
    }
}
