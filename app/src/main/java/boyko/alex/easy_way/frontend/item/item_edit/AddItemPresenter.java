package boyko.alex.easy_way.frontend.item.item_edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.vansuita.pickimage.bean.PickResult;

import org.parceler.Parcels;

import java.util.ArrayList;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.ConvertHelper;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Address;
import boyko.alex.easy_way.backend.models.Category;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.backend.models.PriceType;

/**
 * Created by Sasha on 02.12.2017.
 * <p>
 * Add item logic contains here.
 */

class AddItemPresenter {
    //private final String LOG_TAG = getClass().getSimpleName();

    private AddItemViewActivity view;
    private static AddItemPresenter presenter;

    private boolean onMainPhotoPick = false;
    private boolean isPhotoLoading = false;
    private boolean isUploading = false;

    private int photosAdded = 0;

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

    void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            if (view.getIntent().getParcelableExtra("item") != null) {
                //mode edit
                photosAdded = 0;
                isPhotoLoading = false;
                isUploading = false;
                onMainPhotoPick = false;

                Item item = Parcels.unwrap(view.getIntent().getParcelableExtra("item"));

                AddItemModel.getInstance(this).setMode(AddItemModel.MODE_EDIT);
                AddItemModel.getInstance(this).setItem(item);
                AddItemModel.getInstance(this).setItemBeforeEdit(item);

                view.setToolbarTitle(ApplicationController.getInstance().getString(R.string.edit));
                view.setName(item.title);
                view.setPrice(String.valueOf(item.price));

                PriceType priceType = DataMediator.getPriceType(item.priceTypeId);
                if (priceType != null && priceType.name != null) {
                    view.setPriceType(priceType.name);
                }

                AddItemModel.getInstance(this).loadAddressById(item.address.id);

                Category category = DataMediator.getCategory(item.categoryId);
                if (category != null && category.name != null) view.setCategory(category.name);

                if (item.description != null) view.setDescription(item.description);
                if (item.notes != null) view.setNotes(item.notes);

                if (item.mainPhoto != null) {
                    view.setMainPhoto(item.mainPhoto);
                    photosAdded++;
                    view.addPhoto(photosAdded + "/3");
                    view.addPhoto("info");
                }

                if (item.photos != null && !item.photos.isEmpty()) {
                    photosAdded += item.photos.size();
                    view.setPhotosCountInfo(photosAdded + "/3");
                    view.addPhotos(item.photos);
                }

                view.setButtonText(ApplicationController.getInstance().getString(R.string.save));
            } else {
                //mode create
                AddItemModel.getInstance(this).initItem();
                AddItemModel.getInstance(this).setMode(AddItemModel.MODE_CREATE);
                photosAdded = 0;

                isPhotoLoading = false;
                isUploading = false;
                onMainPhotoPick = false;

                PriceType priceType = DataMediator.getPriceTypes().get(0);

                if (priceType != null && priceType.name != null) {
                    AddItemModel.getInstance(this).setPriceType(DataMediator.getPriceTypes().get(0));
                    if (priceType.name != null) view.setPriceType(priceType.name);
                }

                AddItemModel.getInstance(this).loadAddressById(DataMediator.getUser().address.id);
            }
        } else {
            if (photosAdded > 0) {
                String mainPhoto = AddItemModel.getInstance(this).getItem().mainPhoto;
                if (mainPhoto != null) {
                    view.setMainPhoto(mainPhoto);
                    view.addPhoto(photosAdded + "/3");
                    view.addPhoto("info");
                }

                ArrayList<String> listPhotos = AddItemModel.getInstance(this).getItem().photos;
                if (listPhotos != null && !listPhotos.isEmpty()) view.addPhotos(listPhotos);
            }
            if(AddItemModel.getInstance(this).getMode() == AddItemModel.MODE_EDIT){
                view.setToolbarTitle(ApplicationController.getInstance().getString(R.string.edit));
                view.setButtonText(ApplicationController.getInstance().getString(R.string.save));
            }
        }
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

    boolean onBackPressed(){
        if(!isPhotoLoading && !isUploading){
            if(AddItemModel.getInstance(this).getMode() == AddItemModel.MODE_CREATE){
                if(isAnyDataHasBeenInputted()) view.launchCloseConfirmationDialog(false, true);
                else onFinish(false);
            }else{
                setItemDataToModel();
                if(AddItemModel.getInstance(this).isItemChanged()){
                    view.launchCloseConfirmationDialog(true, true);
                }else{
                    onFinish(false);
                }
            }
        }

        return false;
    }

    void onFinish(boolean isDataChanged){
        if(AddItemModel.getInstance(this).getMode() == AddItemModel.MODE_CREATE){
            AddItemModel.getInstance(this).deleteAllPhotos();
        }else{
            if(isDataChanged){
                AddItemModel.getInstance(this).deleteAllPhotosOnCancelEdit();
            }
        }
        view.setResult(RequestCodes.RESULT_CODE_CANCELED);
        view.finish();
    }

    void onPickMainPhoto() {
        onMainPhotoPick = true;
        view.launchPickPhotoDialog();
    }

    void onPickResult(PickResult pickResult) {
        if (pickResult.getError() == null) {
            if (pickResult.getBitmap() != null) {
                startUploadPhoto(pickResult.getBitmap());
            } else view.showDefaultError();
        } else {
            view.showDefaultError();
        }
    }

    void onMainPhotoClicked() {
        view.launchFullscreenGallery(AddItemModel.getInstance(this).getAllPhotos(), 0);
    }

    void onMainPhotoEditClicked() {
        onMainPhotoPick = true;
        view.launchPickPhotoDialog();
    }

    void onMainPhotoDeleteConfirmed() {
        //delete main photo from server if need
        if(AddItemModel.getInstance(this).getMode() == AddItemModel.MODE_CREATE) {
            AddItemModel.getInstance(this).deletePhoto(AddItemModel.getInstance(this).getItem().mainPhoto);
        }
        else {
            if(!AddItemModel.getInstance(this).isBeforeEditItemHasPhoto(AddItemModel.getInstance(this).getItem().mainPhoto)){
                AddItemModel.getInstance(this).deletePhoto(AddItemModel.getInstance(this).getItem().mainPhoto);
            }
        }

        if (photosAdded > 1) {
            photosAdded--;

            //set next photo as main in model and view
            AddItemModel.getInstance(this).getItem().mainPhoto = AddItemModel.getInstance(this).getItem().photos.get(0);
            view.setMainPhoto(AddItemModel.getInstance(this).getItem().photos.get(0));

            //remove 1 photo from model and view, because it's main now
            AddItemModel.getInstance(this).getItem().photos.remove(0);
            view.removePhoto(1);

            //update photos count in view
            view.setPhotosCountInfo(photosAdded + "/3");
            //add info item if only main photo left
            if (photosAdded == 1) view.addPhoto("info");
        } else {
            AddItemModel.getInstance(this).getItem().mainPhoto = null;
            AddItemModel.getInstance(this).getItem().photos = null;

            view.removeMainPhoto();
            photosAdded = 0;
        }
    }

    void onListPhotoClicked(int position) {
        if (view.getPhotos().get(position).length() > 5) {
            view.launchFullscreenGallery(AddItemModel.getInstance(this).getAllPhotos(), position);
        } else {
            if (!view.getPhotos().get(position).equals("info")) {
                if (photosAdded < 3) {
                    view.launchPickPhotoDialog();
                } else {
                    view.showLimitPhotosError();
                }
            }
        }
    }

    void onListPhotoLongClicked(int position) {
        //// TODO: 23.12.2017
        //view.launchFullscreenGallery(getBitmaps(mainPhoto, listPhotos), position);
    }

    void onListPhotoDeleteConfirmed(int position) {
        //remove from server if need
        if(AddItemModel.getInstance(this).getMode() == AddItemModel.MODE_CREATE) {
            AddItemModel.getInstance(this).deletePhoto(AddItemModel.getInstance(this).getItem().photos.get(position-1));
        }
        else {
            if(!AddItemModel.getInstance(this).isBeforeEditItemHasPhoto(AddItemModel.getInstance(this).getItem().photos.get(position-1))){
                AddItemModel.getInstance(this).deletePhoto(AddItemModel.getInstance(this).getItem().photos.get(position-1));
            }
        }
        AddItemModel.getInstance(this).getItem().photos.remove(position-1);

        photosAdded--;
        view.removePhoto(position);
        view.setPhotosCountInfo(photosAdded + "/3");
    }

    private void startUploadPhoto(@NonNull Bitmap bitmap) {
        isPhotoLoading = true;
        AddItemModel.getInstance(this).uploadPhoto(bitmap);
        view.showProgressBar();
        view.setUploadMessage(ApplicationController.getInstance().getString(R.string.uploading_photo));
    }

    void finishedUploadPhoto(String url) {
        view.hideProgressBar();
        if (url != null) {
            if (onMainPhotoPick) {
                if (photosAdded == 0) {
                    //set main photo
                    photosAdded++;
                    AddItemModel.getInstance(this).addPhoto(url);
                    view.setMainPhoto(url);
                    view.addPhoto(photosAdded + "/3");
                    view.addPhoto("info");
                } else {
                    //edit main photo
                    if (AddItemModel.getInstance(this).getItem().mainPhoto != null) {
                        if(AddItemModel.getInstance(this).getMode() == AddItemModel.MODE_CREATE) AddItemModel.getInstance(this).deletePhoto(AddItemModel.getInstance(this).getItem().mainPhoto);
                    }
                    view.setMainPhoto(url);
                    AddItemModel.getInstance(this).getItem().mainPhoto = url;
                }
            } else {
                //adding next photo
                photosAdded++;
                AddItemModel.getInstance(this).addPhoto(url);
                view.addPhoto(url);
                view.setPhotosCountInfo(photosAdded + "/3");
            }
        } else {
            view.showDefaultError();
        }
        isPhotoLoading = false;
        onMainPhotoPick = false;
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
            isUploading = true;
            view.showProgressBar();
            view.setUploadMessage(ApplicationController.getInstance().getString(R.string.uploading_item));

            setItemDataToModel();

            if(AddItemModel.getInstance(this).getMode() == AddItemModel.MODE_CREATE) AddItemModel.getInstance(this).uploadItem();
            else AddItemModel.getInstance(this).updateItem();
        }
    }

    void onAddressSelected(@NonNull Address address) {
        AddItemModel.getInstance(this).setAddress(address);
        if (address.fullName != null) view.setAddress(address.fullName);
    }

    void onAddressSelectError(Status status) {
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

    void onItemUploaded(Item item) {
        isUploading = false;
        Intent intent = new Intent();
        intent.putExtra("item", Parcels.wrap(item));
        view.setResult(RequestCodes.RESULT_CODE_OK, intent);
        view.finish();
    }

    void onItemUploadedError(){
        view.showDefaultError();
        isUploading = false;
    }

    private void setItemDataToModel(){
        AddItemModel.getInstance(this).setTitle(view.getItemTitle());
        AddItemModel.getInstance(this).setPrice(Double.valueOf(view.getItemPrice()));
        AddItemModel.getInstance(this).setDescription(view.getItemDescription());
        AddItemModel.getInstance(this).setNotes(view.getItemNotes());
        AddItemModel.getInstance(this).setOwner(DataMediator.getUser());
    }

    private boolean isAnyDataHasBeenInputted(){
        return !view.getItemTitle().isEmpty()
                || !view.getItemPrice().isEmpty()
                || !view.getItemDescription().isEmpty()
                || !view.getItemNotes().isEmpty()
                || AddItemModel.getInstance(this).getItem().categoryId != null
                || !AddItemModel.getInstance(this).getAllPhotos().isEmpty();
    }
}
