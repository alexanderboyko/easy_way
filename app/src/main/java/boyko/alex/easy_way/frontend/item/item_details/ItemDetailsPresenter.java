package boyko.alex.easy_way.frontend.item.item_details;

import boyko.alex.easy_way.backend.models.ItemBase;
import boyko.alex.easy_way.backend.models.ItemExpanded;

/**
 * Created by Sasha on 11.11.2017.
 */

public class ItemDetailsPresenter {
    private ItemDetailsViewActivity view;
    private static ItemDetailsPresenter presenter;

    private ItemDetailsPresenter(ItemDetailsViewActivity view){
        this.view = view;
    }

    public static ItemDetailsPresenter getInstance(ItemDetailsViewActivity view){
        if(presenter == null){
            presenter = new ItemDetailsPresenter(view);
        }else{
            presenter.view = view;
        }
        return presenter;
    }

    void startLoadingItemExpanded(ItemBase itemBase){
        ItemDetailsModel.getInstance(this).startLoadingItemExpanded(itemBase);
    }

    void loadingItemExpandedFinished(ItemExpanded itemExpanded){
        view.setItemExpanded(itemExpanded);
    }
}
