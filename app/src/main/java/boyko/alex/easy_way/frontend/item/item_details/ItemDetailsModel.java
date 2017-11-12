package boyko.alex.easy_way.frontend.item.item_details;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import boyko.alex.easy_way.backend.DummyGenerator;
import boyko.alex.easy_way.backend.models.ItemBase;
import boyko.alex.easy_way.backend.models.ItemExpanded;

/**
 * Created by Sasha on 12.11.2017.
 */

public class ItemDetailsModel {
    private ItemDetailsPresenter presenter;
    private static ItemDetailsModel model;

    private ItemDetailsModel(ItemDetailsPresenter presenter){
        this.presenter = presenter;
    }

    public static ItemDetailsModel getInstance(ItemDetailsPresenter presenter){
        if(model == null){
            model = new ItemDetailsModel(presenter);
        }
        return model;
    }

    void startLoadingItemExpanded(final ItemBase itemBase){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingItemExpandedFinished(DummyGenerator.getDummyItemExpanded(itemBase));
            }
        }, 500);
    }

    private void loadingItemExpandedFinished(ItemExpanded itemExpanded){
        presenter.loadingItemExpandedFinished(itemExpanded);
    }
}
