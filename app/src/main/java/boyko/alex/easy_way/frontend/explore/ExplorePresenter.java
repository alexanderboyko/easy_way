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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Category;
import boyko.alex.easy_way.backend.models.Dialog;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.Like;

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
    private boolean isStartItemsLoading = false;

    private boolean firstDialogLoaded = false, secondDialogLoaded = false;
    private QuerySnapshot querySnapshot1, querySnapshot2;

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

    void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            //launching activity
            isNextItemLoading = false;
            isStartItemsLoading = false;
            ExploreModel.getInstance(this).setCategoryId(null);
            ExploreModel.getInstance(this).setAddress(null);
            ExploreModel.getInstance(this).setPriceFrom(0);
            ExploreModel.getInstance(this).setPriceTo(0);
            ExploreModel.getInstance(this).setOrder(null);

            view.setFilterCategory(ApplicationController.getInstance().getString(R.string.all_categories));
            view.clearFiltersAddress();
            view.clearFiltersPrices();
            view.setFilterOrder(ApplicationController.getInstance().getString(R.string.newest));

            startLoading();
        } else {
            //restoring activity
            if (isStartItemsLoading) {
                isNextItemLoading = false;
                isStartItemsLoading = false;
                ExploreModel.getInstance(this).setCategoryId(null);
                ExploreModel.getInstance(this).setAddress(null);
                ExploreModel.getInstance(this).setPriceFrom(0);
                ExploreModel.getInstance(this).setPriceTo(0);
                ExploreModel.getInstance(this).setOrder(null);

                view.setFilterCategory(ApplicationController.getInstance().getString(R.string.all_categories));
                view.clearFiltersAddress();
                view.clearFiltersPrices();
                view.setFilterOrder(ApplicationController.getInstance().getString(R.string.newest));

                startLoading();
            } else {
                //restore items
                loadingFinished((ArrayList<Object>) Parcels.unwrap(savedInstanceState.getParcelable("items")));

                //restore view mode
                int mode = savedInstanceState.getInt("mode");
                if (mode == ItemsRecyclerAdapter.MODE_GRID) {
                    view.setFiltersViewCards();
                } else {
                    view.setFiltersViewList();
                }

                onRestoreOrder(ExploreModel.getInstance(this).getOrder());
                onRestoreAddress(ExploreModel.getInstance(this).getAddress());
                onRestoreCategory(ExploreModel.getInstance(this).getCategoryId());
                onRestorePrice(ExploreModel.getInstance(this).getPriceFrom(), ExploreModel.getInstance(this).getPriceTo());

                if (isNextItemLoading) {
                    startLoadingNext();
                }
            }
        }
    }

    void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("items", Parcels.wrap(view.getItems()));
        outState.putInt("mode", view.getMode());
    }

    void startLoading() {
        isStartItemsLoading = true;
        view.setLoading(true);
        ExploreModel.getInstance(this).startLoading();
    }

    void onRefresh() {
        isStartItemsLoading = true;
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

        if(requestCode == RequestCodes.REQUEST_CODE_DETAILS){
            updateLikes();
        }

        if(requestCode == RequestCodes.REQUEST_CODE_EDIT){
            if(resultCode == RequestCodes.RESULT_CODE_OK){
                startLoading();
            }
        }

        if(requestCode == RequestCodes.REQUEST_CODE_SETTINGS){
            if(resultCode == RequestCodes.RESULT_CODE_PROFILE_EDITED){
                view.initUser();
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

        if (items == null || items.isEmpty()) view.setNoItemsMessageVisibility(View.VISIBLE);
        else view.setNoItemsMessageVisibility(View.GONE);

        isStartItemsLoading = false;
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

    void onItemClicked(Item item) {
        view.launchItemDetailsActivity(item);
    }

    void onLikeClicked(String itemId){
        if (DataMediator.getLike(itemId) == null) {
            final Like like1 = new Like();
            like1.itemId = itemId;
            like1.userId = DataMediator.getUser().id;
            FirebaseFirestore.getInstance().collection("likes").add(like1).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        like1.id = task.getResult().getId();
                        DataMediator.getLikes().add(like1);
                    }
                }
            });
        }else{
            Like like = DataMediator.getLike(itemId);
            if(like != null && like.id != null) {
                FirebaseFirestore.getInstance().collection("likes").document(like.id).delete();
                DataMediator.removeLike(like.id);
            }
        }
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

    private void onRestoreCategory(String categoryId) {
        if (categoryId != null) {
            //category selected
            Category category = DataMediator.getCategory(categoryId);
            if (category != null) {
                view.setFilterCategory(category.name);
            }
        }else{
            view.setFilterCategory(ApplicationController.getInstance().getString(R.string.all_categories));
        }
    }

    private void onAddressSelected(@NonNull Address address) {
        ExploreModel.getInstance(this).setAddress(address);
        if (address.fullName != null) view.setFilterAddress(address.fullName);
        startLoading();
    }

    private void onRestoreAddress(Address address) {
        if (address != null && address.fullName != null) view.setFilterAddress(address.fullName);
        else view.clearFiltersAddress();
    }

    private void onAddressSelectError(Status status) {
        if (status != null && status.getStatusMessage() != null)
            view.showError(status.getStatusMessage());
        else view.showError(ApplicationController.getInstance().getString(R.string.error_message));
    }

    void onFiltersClearAddressClicked() {
        view.clearFiltersAddress();
        ExploreModel.getInstance(this).setAddress(null);
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

    private void onRestorePrice(double from, double to) {
        if (from == 0 && to == 0) {
            view.setFiltersPriceIcon(false);
        } else {
            if (from != 0 && to != 0) {
                if (from >= to) {
                    view.setFiltersPriceToError(ApplicationController.getInstance().getString(R.string.should_be_bigger_than_from_price));
                } else {
                    view.setFiltersPriceIcon(true);
                }
                return;
            }

            if (from == 0) {
                if (to > 0) {
                    view.setFiltersPriceIcon(true);
                } else {
                    view.setFiltersPriceToError(ApplicationController.getInstance().getString(R.string.shouldnt_be_0));
                }
            }

            if (to == 0) {
                view.setFiltersPriceIcon(true);
            }
        }
    }

    void onViewChangedToCards(int currentMode) {
        if (currentMode != ItemsRecyclerAdapter.MODE_GRID) {
            view.setFiltersViewCards();
        }
    }

    void onViewChangedToList(int currentMode) {
        if (currentMode != ItemsRecyclerAdapter.MODE_LINEAR_VERTICAL) {
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

    private void onRestoreOrder(Query.Direction direction){
        if(direction == null){
            view.setFilterOrder(ApplicationController.getInstance().getString(R.string.newest));
        }else{
            if(direction == Query.Direction.ASCENDING) view.setFilterOrder(ApplicationController.getInstance().getString(R.string.from_cheapest));
            else view.setFilterOrder(ApplicationController.getInstance().getString(R.string.from_most_expensive));
        }
    }

    private void updateLikes(){
        ArrayList<Object> items = view.getItems();
        for (int i =0;i<items.size();i++) {
            Object object = items.get(i);
            if (object instanceof Item) {
                if (DataMediator.getLike(((Item) object).id) != null) {
                    ((Item) object).isLiked = true;
                    view.getAdapter().notifyItemChanged(i);
                }else{
                    ((Item) object).isLiked = false;
                    view.getAdapter().notifyItemChanged(i);
                }
            }
        }
    }

    void checkNewMessages(){
        FirebaseFirestore.getInstance().collection("dialogs")
                .whereEqualTo("user1Id", DataMediator.getUser().id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            querySnapshot1 = task.getResult();
                            firstDialogLoaded = true;
                            onDialogLoaded();
                        } else {
                            querySnapshot1 = null;
                            firstDialogLoaded = true;
                        }
                    }
                });

        FirebaseFirestore.getInstance().collection("dialogs")
                .whereEqualTo("user2Id", DataMediator.getUser().id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            querySnapshot2 = task.getResult();
                            secondDialogLoaded = true;
                            onDialogLoaded();
                        } else {
                            querySnapshot2 = null;
                            secondDialogLoaded = true;
                        }
                    }
                });
    }

    private void onDialogLoaded() {
        if (firstDialogLoaded && secondDialogLoaded && querySnapshot1 != null && querySnapshot2 != null) {
            ArrayList<Dialog> dialogs = new ArrayList<>();
            int unreadMessages = 0;
            for (DocumentSnapshot document : querySnapshot1) {
                Dialog dialog = ConvertHelper.convertToDialog(document);
                if (dialog.user1Id.equals(dialog.user2Id)) continue;
                if(dialog.user1HasUnreadMessage) unreadMessages++;
                dialogs.add(dialog);
            }
            for (DocumentSnapshot document : querySnapshot2) {
                Dialog dialog = ConvertHelper.convertToDialog(document);
                if (dialog.user1Id.equals(dialog.user2Id)) continue;
                if(dialog.user2HasUnreadMessage) unreadMessages++;
                dialogs.add(dialog);
            }

            DataMediator.setDialogs(dialogs);

            if (unreadMessages == 0) {
                view.setUnreadMessagesCount(null);
            }else{
                view.setUnreadMessagesCount(String.valueOf(unreadMessages));
            }

        }
    }
}
