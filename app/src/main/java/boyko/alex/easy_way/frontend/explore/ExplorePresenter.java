package boyko.alex.easy_way.frontend.explore;

import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.View;

import java.util.ArrayList;

import boyko.alex.easy_way.backend.models.ItemBase;

/**
 * Created by Sasha on 05.11.2017.
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

    void startLoading() {
        ExploreModel.getInstance(this).startLoading();
    }

    private void startLoadingNext() {
        isNextItemLoading = true;
        ExploreModel.getInstance(this).startLoadingNext();
    }

    void loadingFinished(ArrayList<Object> items) {
        view.setItems(items);
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
        view.launchSearchActivity();
    }

    void onItemClicked(ItemBase item) {
        view.launchItemDetailsActivity();
    }

    void onProfileEditClicked(){
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
}
