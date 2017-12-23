package boyko.alex.easy_way.frontend.explore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Category;
import boyko.alex.easy_way.backend.models.Item;

/**
 * Created by Sasha on 05.11.2017.
 * <p>
 * All explore activity logic are here
 */

class ExplorePresenter {
    private ExploreViewActivity view;
    private static ExplorePresenter presenter;

    //makes true if we starting loading next items to list
    //makes false if we finished loading next items
    //needs to not starting loading next items several times, but only one and waiting until loading finished
    private boolean isNextItemLoading = false;

    private ExplorePresenter(ExploreViewActivity view) {
        this.view = view;
    }

    static ExplorePresenter getInstance(ExploreViewActivity view) {
        if (presenter == null) {
            presenter = new ExplorePresenter(view);
        } else {
            presenter.view = view;
        }
        return presenter;
    }

    void onCreate(@Nullable Bundle savedInstanceState){
        if(savedInstanceState == null){
            //launching activity
            isNextItemLoading = false;
            ExploreModel.getInstance(this).setCategoryId(null);
            ExploreModel.getInstance(this).setAddressId(null);
            ExploreModel.getInstance(this).setPriceFrom(0);
            ExploreModel.getInstance(this).setPriceTo(0);
            ExploreModel.getInstance(this).setOrder(null);

            view.setFilterCategory(ApplicationController.getInstance().getString(R.string.all_categories));
            view.clearFiltersAddress();
            view.clearFiltersPrices();
            view.setFilterOrder(ApplicationController.getInstance().getString(R.string.newest));

            startLoading();
        }else{
            //restoring activity
        }
    }

    void onSaveInstanceState(Bundle outState) {

    }

    void startLoading() {
        view.setLoading(true);
        ExploreModel.getInstance(this).startLoading();
    }

    void onRefresh(){
        ExploreModel.getInstance(this).startLoading();
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.REQUEST_CODE_SELECT) {
            if (resultCode == RequestCodes.RESULT_CODE_SELECTED) {
                if (data != null) {
                    onCategorySelected(data.getStringExtra("categoryId"));
                }
            }
        }
        if (requestCode == RequestCodes.REQUEST_CODE_ADDRESS) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null)
                    onAddressSelected(ConvertHelper.convertPlaceToAddress(PlaceAutocomplete.getPlace(view, data)));
                else
                    view.showError(ApplicationController.getInstance().getString(R.string.error_message));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                if (data != null) onAddressSelectError(PlaceAutocomplete.getStatus(view, data));
            }
        }
    }

    private void startLoadingNext() {
        isNextItemLoading = true;
        ExploreModel.getInstance(this).startLoadingNext();
    }

    void loadingFinished(ArrayList<Object> items) {
        view.setItems(items);
        view.setLoading(false);

        if(items == null || items.isEmpty()) view.setNoItemsMessageVisibility(View.VISIBLE);
        else view.setNoItemsMessageVisibility(View.GONE);
    }

    void loadingNextFinished(ArrayList<Object> items) {
        view.addItems(items);
        isNextItemLoading = false;
    }

    boolean onBackPressed() {
        if (view.bottomSheetBehavior().getState() == BottomSheetBehavior.STATE_EXPANDED) {
            view.bottomSheetBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
            return false;
        }
        if (view.drawerLayout().isDrawerOpen(Gravity.START)) {
            view.drawerLayout().closeDrawer(Gravity.START);
            return false;
        }

        return true;
    }

    void onBottomSheetStateChanged(int newState) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            view.shadow().setVisibility(View.GONE);
            view.drawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
            view.drawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    void onBottomSheetSlide(float offset) {
        view.shadow().setVisibility(View.VISIBLE);
        view.shadow().setAlpha(offset);
        view.openBottomSheetButton().setRotation(180f * offset);
    }

    void onBottomSheetInfoPanelClicked() {
        if (view.bottomSheetBehavior().getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            view.bottomSheetBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            view.bottomSheetBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    void onDrawerStateChanged(int newState) {
        if (newState == DrawerLayout.STATE_DRAGGING) {
            if (view.bottomSheetBehavior().getState() == BottomSheetBehavior.STATE_EXPANDED) {
                view.bottomSheetBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    void onShadowClicked() {
        view.bottomSheetBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    void onSearchClicked() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        for(int i = 0; i<50;i++) db.collection("items").add(DummyGenerator.getDummyItem());
        view.launchSearchActivity();
    }

    void onItemClicked(Item item) {
        view.launchItemDetailsActivity(item);
    }

    void onProfileEditClicked() {
        view.launchEditProfileActivity();
    }

    /**
     * On list scroll event caught
     *
     * @param layoutManager lost layout manager
     * @param dy            scrolled Y coordinates
     */
    void onListScrolled(StaggeredGridLayoutManager layoutManager, int dy) {
        if (dy > 0 && !isNextItemLoading) {
            int array[] = new int[2];
            array = layoutManager.findFirstVisibleItemPositions(array);
            if (array[0] + layoutManager.getChildCount() >= layoutManager.getItemCount()
                    || array[1] + layoutManager.getChildCount() >= layoutManager.getItemCount()) {
                startLoadingNext();
            }
        }
    }

    void onListScrolled(LinearLayoutManager layoutManager, int dy) {
        if (dy > 0 && !isNextItemLoading) {
            if (layoutManager.findFirstVisibleItemPosition() + layoutManager.getChildCount()
                    >= layoutManager.getItemCount()) {
                isNextItemLoading = true;
                startLoadingNext();
            }
        }
    }

    void onFiltersCategoryClicked() {
        view.launchCategorySelectDialog();
    }

    private void onCategorySelected(String categoryId) {
        if (categoryId != null) {
            //all categories selected
            if (categoryId.equals("allCategories")) {
                ExploreModel.getInstance(this).setCategoryId(null);
                view.setFilterCategory(ApplicationController.getInstance().getString(R.string.all_categories));
                startLoading();
                return;
            }
            //category selected
            Category category = DataMediator.getCategory(categoryId);
            if (category != null) {
                ExploreModel.getInstance(this).setCategoryId(categoryId);
                view.setFilterCategory(category.name);
                startLoading();
            }
        }
    }

    private void onAddressSelected(@NonNull Address address) {
        ExploreModel.getInstance(this).setAddressId(address.id);
        if (address.fullName != null) view.setFilterAddress(address.fullName);
        startLoading();
    }

    private void onAddressSelectError(Status status) {
        if (status != null && status.getStatusMessage() != null)
            view.showError(status.getStatusMessage());
        else view.showError(ApplicationController.getInstance().getString(R.string.error_message));
    }

    void onFiltersClearAddressClicked() {
        view.clearFiltersAddress();
        ExploreModel.getInstance(this).setAddressId(null);
        startLoading();
    }

    void onPriceChanged(String from, String to) {
        if (from.isEmpty() && to.isEmpty()) {
            ExploreModel.getInstance(this).setPriceFrom(0);
            ExploreModel.getInstance(this).setPriceTo(0);
            view.setFiltersPriceIcon(false);
        } else {
            if (!from.isEmpty() && !to.isEmpty()) {
                double fromPrice = Double.valueOf(from);
                double toPrice = Double.valueOf(to);
                if (fromPrice >= toPrice) {
                    view.setFiltersPriceToError(ApplicationController.getInstance().getString(R.string.should_be_bigger_than_from_price));
                } else {
                    ExploreModel.getInstance(this).setPriceFrom(fromPrice);
                    ExploreModel.getInstance(this).setPriceTo(toPrice);
                    view.setFiltersPriceIcon(true);
                    startLoading();
                }
                return;
            }

            if (from.isEmpty()) {
                ExploreModel.getInstance(this).setPriceFrom(0);
                if (Double.valueOf(to) != 0 && Double.valueOf(to) > 0) {
                    ExploreModel.getInstance(this).setPriceTo(Double.valueOf(to));
                    view.setFiltersPriceIcon(true);
                    startLoading();
                } else {
                    view.setFiltersPriceToError(ApplicationController.getInstance().getString(R.string.shouldnt_be_0));
                }
            }

            if (to.isEmpty()) {
                ExploreModel.getInstance(this).setPriceTo(0);
                ExploreModel.getInstance(this).setPriceFrom(Double.valueOf(from));
                view.setFiltersPriceIcon(true);
                startLoading();
            }
        }
    }

    void onViewChangedToCards(int currentMode) {
        if(currentMode != ItemsRecyclerAdapter.MODE_GRID){
            view.setFiltersViewCards();
        }
    }

    void onViewChangedToList(int currentMode) {
        if(currentMode != ItemsRecyclerAdapter.MODE_LINEAR_VERTICAL){
            view.setFiltersViewList();
        }
    }

    void onFiltersClearPrice() {
        ExploreModel.getInstance(this).setPriceFrom(0);
        ExploreModel.getInstance(this).setPriceTo(0);
        view.clearFiltersPrices();
    }

    void onFiltersOrderClicked(View view) {
        ArrayList<String> orderTypes = new ArrayList<>();
        orderTypes.add(ApplicationController.getInstance().getString(R.string.newest));
        orderTypes.add(ApplicationController.getInstance().getString(R.string.from_cheapest));
        orderTypes.add(ApplicationController.getInstance().getString(R.string.from_most_expensive));


        PopupMenu popup = new PopupMenu(ApplicationController.getInstance(), view);
        for (int i = 0; i < orderTypes.size(); i++) {
            popup.getMenu().add(0, Menu.NONE, Menu.NONE, orderTypes.get(i));
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onOrderTypeSelected(item.getTitle().toString());
                return false;
            }
        });
        popup.show();
    }

    private void onOrderTypeSelected(String orderType) {
        if (orderType.equals(ApplicationController.getInstance().getString(R.string.newest))) {
            ExploreModel.getInstance(this).setOrder(null);
            view.setFilterOrder(ApplicationController.getInstance().getString(R.string.newest));
        }
        if (orderType.equals(ApplicationController.getInstance().getString(R.string.from_cheapest))) {
            ExploreModel.getInstance(this).setOrder(Query.Direction.ASCENDING);
            view.setFilterOrder(ApplicationController.getInstance().getString(R.string.from_cheapest));
        }
        if (orderType.equals(ApplicationController.getInstance().getString(R.string.from_most_expensive))) {
            ExploreModel.getInstance(this).setOrder(Query.Direction.DESCENDING);
            view.setFilterOrder(ApplicationController.getInstance().getString(R.string.from_most_expensive));
        }

        startLoading();
    }
}
