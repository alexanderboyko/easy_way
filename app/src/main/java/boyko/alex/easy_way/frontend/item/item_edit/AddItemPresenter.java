package boyko.alex.easy_way.frontend.item.item_edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.vansuita.pickimage.bean.PickResult;

import java.util.ArrayList;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Category;
import boyko.alex.easy_way.backend.models.PriceType;

/**
 * Created by Sasha on 02.12.2017.
 * <p>
 * Add item logic contains here.
 */

class AddItemPresenter {
    private final String LOG_TAG = getClass().getSimpleName();

    private AddItemViewActivity view;
    private static AddItemPresenter presenter;

    private boolean onMainPhotoPick = false;

    private int photosAdded = 0;
    private int photosUploaded = 0;

    private AddItemPresenter(AddItemViewActivity view) {
        this.view = view;
    }

    static AddItemPresenter getInstance(AddItemViewActivity view) {
        if (presenter == null) {
            presenter = new AddItemPresenter(view);
        } else {
            presenter.view = view;
        }
        return presenter;
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.REQUEST_CODE_ADDRESS) {
            if (resultCode == Activity.RESULT_OK) {
                onAddressSelected(ConvertHelper.convertPlaceToAddress(PlaceAutocomplete.getPlace(view, data)));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                onAddressSelectError(PlaceAutocomplete.getStatus(view, data));
            }
        }

        if (requestCode == RequestCodes.REQUEST_CODE_SELECT) {
            if (resultCode == RequestCodes.RESULT_CODE_SELECTED) {
                if (data != null) {
                    String categoryId = data.getStringExtra("categoryId");
                    if (categoryId != null) {
                        Category category = DataMediator.getCategory(categoryId);
                        if (category != null) {
                            AddItemModel.getInstance(this).setCategory(category);
                            view.setCategory(category.name);
                        }
                    }
                }
            }
        }
    }

    void onPickMainPhoto() {
        onMainPhotoPick = true;
        view.launchPickPhotoDialog();
    }

    void onPickResult(PickResult pickResult) {
        if (pickResult.getError() == null) {
            if (onMainPhotoPick) {
                if (photosAdded == 0) {
                    //add main photo (first image)
                    photosAdded++;
                    view.setMainPhoto(pickResult.getBitmap());
                    view.addPhoto(photosAdded + "/3");
                    view.addPhoto("info");
                } else {
                    //changing main photo
                    view.setMainPhoto(pickResult.getBitmap());
                }
            } else {
                //adding next photo
                photosAdded++;
                view.addPhoto(pickResult.getBitmap());
                view.setPhotosCountInfo(photosAdded + "/3");
            }
        } else {
            view.showDefaultError();
        }
        onMainPhotoPick = false;
    }

    void onMainPhotoClicked(Bitmap mainPhoto, ArrayList<Object> listPhotos) {
        view.launchFullscreenGallery(getBitmaps(mainPhoto, listPhotos), 0);
    }

    void onMainPhotoEditClicked() {
        onMainPhotoPick = true;
        view.launchPickPhotoDialog();
    }

    void onMainPhotoDeleteConfirmed(ArrayList<Object> listPhotos) {
        if (photosAdded > 1) {
            if (listPhotos.get(1) instanceof Bitmap) {
                photosAdded--;
                view.setMainPhoto((Bitmap) listPhotos.get(1));
                view.removePhoto(1);

                view.setPhotosCountInfo(photosAdded + "/3");
                if (photosAdded == 1) view.addPhoto("info");
            }
        } else {
            view.removeMainPhoto();
            photosAdded = 0;
        }
    }

    void onListPhotoClicked(Bitmap mainPhoto, ArrayList<Object> listPhotos, int position) {
        if (listPhotos.get(position) instanceof String) {
            if (!listPhotos.get(position).equals("info")) {
                if (photosAdded < 3) {
                    view.launchPickPhotoDialog();
                } else {
                    view.showLimitPhotosError();
                }
            }
        } else {
            view.launchFullscreenGallery(getBitmaps(mainPhoto, listPhotos), position);
        }
    }

    void onListPhotoLongClicked(int position) {
        //view.launchFullscreenGallery(getBitmaps(mainPhoto, listPhotos), position);
    }

    void onListPhotoDeleteConfirmed(int position) {
        photosAdded--;
        view.removePhoto(position);
        view.setPhotosCountInfo(photosAdded + "/3");
    }

    void onPriceTypeClicked(View view) {
        ArrayList<PriceType> priceTypes = DataMediator.getPriceTypes();

        PopupMenu popup = new PopupMenu(ApplicationController.getInstance(), view);
        for (int i = 0; i < priceTypes.size(); i++) {
            popup.getMenu().add(0, Menu.NONE, Menu.NONE, priceTypes.get(i).name);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onPriceTypeSelected(DataMediator.getPriceTypeByName(item.getTitle().toString()));
                return false;
            }
        });
        popup.show();
    }

    void onCategoryClicked() {
        view.launchCategorySelectDialog();
    }

    void onSaveClicked() {
        if (validate()) {
            view.showProgressBar();

            AddItemModel.getInstance(this).setTitle(view.getItemTitle());
            AddItemModel.getInstance(this).setPrice(Double.valueOf(view.getItemPrice()));
            AddItemModel.getInstance(this).setDescription(view.getItemDescription());
            AddItemModel.getInstance(this).setNotes(view.getItemNotes());
            AddItemModel.getInstance(this).setOwner(DataMediator.getUser());

            if(photosAdded > 0) {
                if (view.getMainPhoto() != null)
                    AddItemModel.getInstance(this).uploadPhoto(view.getMainPhoto(), true);
                if (photosAdded > 1) {
                    ArrayList<Object> photos = view.getListPhotos();
                    for (Object object : photos) {
                        if (object instanceof Bitmap) {
                            AddItemModel.getInstance(this).uploadPhoto((Bitmap) object, false);
                        }
                    }
                }
            }else{
                AddItemModel.getInstance(this).uploadItem();
            }
        }
    }

    private void onAddressSelected(@NonNull Address address) {
        AddItemModel.getInstance(this).setAddress(address);
        if (address.fullName != null) view.setAddress(address.fullName);
    }

    private void onAddressSelectError(Status status) {
        if (status != null && status.getStatusMessage() != null)
            view.showSelectAddressError(status.getStatusMessage());
        else view.showDefaultError();
    }

    private void onPriceTypeSelected(PriceType priceType) {
        if (priceType != null) {
            AddItemModel.getInstance(this).setPriceType(priceType);
            if (priceType.name != null) view.setPriceType(priceType.name);
        }
    }

    private ArrayList<Bitmap> getBitmaps(Bitmap mainPhoto, ArrayList<Object> listPhotos) {
        ArrayList<Bitmap> images = new ArrayList<>();
        images.add(mainPhoto);
        if (photosAdded > 1) {
            for (int i = 1; i < listPhotos.size(); i++) images.add((Bitmap) listPhotos.get(i));
        }
        return images;
    }

    private boolean validate() {
        if (view.getItemTitle().isEmpty()) {
            view.setTitleError(ApplicationController.getInstance().getString(R.string.error_empty));
            return false;
        }

        if (view.getItemPrice().isEmpty()) {
            view.setPriceError(ApplicationController.getInstance().getString(R.string.error_empty));
            return false;
        }

        if (view.getItemCategory().isEmpty()) {
            view.setCategoryError(ApplicationController.getInstance().getString(R.string.error_empty));
            return false;
        }
        view.disableErrors();
        return true;
    }

    void photoUploaded(){
        photosUploaded ++;
        if(photosUploaded == photosAdded){
            AddItemModel.getInstance(this).uploadItem();
        }
    }

    void onItemUploaded(){
        view.setResult(RequestCodes.RESULT_CODE_OK);
        view.finish();
    }
}
