package boyko.alex.easy_way.frontend.item.item_edit;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.ArrayList;

import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.frontend.dialogs.CategorySelectFragmentView;
import boyko.alex.easy_way.frontend.gallery.GalleryFragment;

/**
 * Created by Sasha on 10.11.2017.
 */

public class AddItemViewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RelativeLayout addPhotoLayout;
    private AppCompatImageView mainPhoto, mainPhotoEdit, mainPhotoDelete;
    private RecyclerView photosRecycler;
    private PhotosLineRecyclerAdapter photosAdapter;

    private TextInputEditText title, price, priceType, address, category, descriptions, notes;
    private Button save;

    private View uploadBackground;
    private RelativeLayout progressBarLayout;

    private Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_add_item);

        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AddItemPresenter.getInstance(AddItemViewActivity.this).onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        initViews();
        initToolbar();
        initAddPhotoLayout();
        initPhotosRecycler();
        initInputs();
    }

    private void initViews() {
        toolbar = findViewById(R.id.add_item_toolbar);
        addPhotoLayout = findViewById(R.id.add_item_add_photo_layout);
        mainPhoto = findViewById(R.id.add_item_photo_main);
        mainPhotoDelete = findViewById(R.id.add_item_photo_main_delete);
        mainPhotoEdit = findViewById(R.id.add_item_photo_main_edit);
        photosRecycler = findViewById(R.id.add_item_photos_recycler);
        title = findViewById(R.id.add_item_title);
        price = findViewById(R.id.add_item_price);
        priceType = findViewById(R.id.add_item_price_type);
        address = findViewById(R.id.add_item_address);
        category = findViewById(R.id.add_item_category);
        descriptions = findViewById(R.id.add_item_description);
        notes = findViewById(R.id.add_item_notes);
        save = findViewById(R.id.add_offer_save);
        uploadBackground = findViewById(R.id.add_item_load_background);
        progressBarLayout = findViewById(R.id.add_item_progressbar_layout);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.new_offer));
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initAddPhotoLayout() {
        addPhotoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItemPresenter.getInstance(AddItemViewActivity.this).onPickMainPhoto();
            }
        });
        mainPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItemPresenter.getInstance(AddItemViewActivity.this).onMainPhotoClicked(((BitmapDrawable) mainPhoto.getDrawable()).getBitmap(), photosAdapter.getPhotos());
            }
        });
        mainPhotoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItemPresenter.getInstance(AddItemViewActivity.this).onMainPhotoEditClicked();
            }
        });
        mainPhotoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDeleteConfirmationDialog(true, -1);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItemPresenter.getInstance(AddItemViewActivity.this).onSaveClicked();
            }
        });
    }

    private void initPhotosRecycler() {
        photosRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photosAdapter = new PhotosLineRecyclerAdapter(new PhotosLineRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                AddItemPresenter.getInstance(AddItemViewActivity.this).onListPhotoClicked(((BitmapDrawable) mainPhoto.getDrawable()).getBitmap(), photosAdapter.getPhotos(), position);
            }

            @Override
            public void onItemLongClick(int position) {
                AddItemPresenter.getInstance(AddItemViewActivity.this).onListPhotoLongClicked(position);
            }

            @Override
            public void onItemDeleteClicked(int position) {
                launchDeleteConfirmationDialog(false, position);
            }
        });
        photosRecycler.setAdapter(photosAdapter);
    }

    private void initInputs() {
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSelectAddressActivity();
            }
        });

        priceType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItemPresenter.getInstance(AddItemViewActivity.this).onPriceTypeClicked(view);
            }
        });
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItemPresenter.getInstance(AddItemViewActivity.this).onCategoryClicked();
            }
        });
    }

    void setMainPhoto(Bitmap bitmap) {
        addPhotoLayout.setVisibility(View.GONE);
        photosRecycler.setVisibility(View.VISIBLE);

        mainPhoto.setVisibility(View.VISIBLE);
        mainPhotoDelete.setVisibility(View.VISIBLE);
        mainPhotoEdit.setVisibility(View.VISIBLE);
        mainPhoto.setImageBitmap(bitmap);
    }

    void removeMainPhoto() {
        addPhotoLayout.setVisibility(View.VISIBLE);
        photosRecycler.setVisibility(View.GONE);

        mainPhoto.setVisibility(View.GONE);
        mainPhotoDelete.setVisibility(View.GONE);
        mainPhotoEdit.setVisibility(View.GONE);

        //remove object from photos list
        photosAdapter.setPhotos(new ArrayList<>());
        photosAdapter.notifyDataSetChanged();
    }

    void addPhoto(Object object) {
        if (photosAdapter.getItemCount() == 2 && photosAdapter.getPhotos().get(1) instanceof String) {
            photosAdapter.getPhotos().remove(1);
            photosAdapter.notifyItemRemoved(1);
        }
        photosAdapter.getPhotos().add(object);
        photosAdapter.notifyItemInserted(photosAdapter.getPhotos().size());
    }

    void setPhotosCountInfo(String info) {
        photosAdapter.getPhotos().set(0, info);
        photosAdapter.notifyItemChanged(0);
    }

    void removePhoto(int position) {
        photosAdapter.getPhotos().remove(position);
        photosAdapter.notifyItemRemoved(position);

        if (photosAdapter.getItemCount() == 1) {
            photosAdapter.getPhotos().add("info");
            photosAdapter.notifyItemInserted(photosAdapter.getItemCount());
        }
    }

//    void hideAddPhotoButton() {
//        photosAdapter.getPhotos().remove(0);
//        photosAdapter.notifyItemRemoved(0);
//    }

    void setAddress(@NonNull String fullName) {
        address.setText(fullName);
    }

    void setPriceType(String name) {
        priceType.setText(name);
    }

    void setCategory(String category) {
        this.category.setText(category);
    }

    String getItemTitle() {
        return title.getText().toString();
    }

    String getItemPrice() {
        return price.getText().toString();
    }

    String getItemCategory(){
        return category.getText().toString();
    }

    String getItemDescription() {
        return descriptions.getText().toString();
    }

    String getItemNotes() {
        return notes.getText().toString();
    }

    Bitmap getMainPhoto() {
        return ((BitmapDrawable) mainPhoto.getDrawable()).getBitmap();
    }

    ArrayList<Object> getListPhotos() {
        return photosAdapter.getPhotos();
    }

    void disableErrors() {
        title.setError(null);
        price.setError(null);
        address.setError(null);
        category.setError(null);
    }

    void setTitleError(String titleError) {
        title.setError(titleError);
    }

    void setPriceError(String error) {
        price.setError(error);
    }

    void setCategoryError(String error) {
        category.setError(error);
    }

    void showSelectAddressError(@NonNull String message) {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    void showDefaultError() {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(this, getString(R.string.error_message), Toast.LENGTH_SHORT);
        toast.show();
    }

    void showLimitPhotosError() {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(this, getString(R.string.photos_limit_message), Toast.LENGTH_SHORT);
        toast.show();
    }

    void showProgressBar(){
        uploadBackground.setVisibility(View.VISIBLE);
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    void hideProgressBar(){
        uploadBackground.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.GONE);
    }

    void launchPickPhotoDialog() {
        PickImageDialog.build(new PickSetup()).setOnPickResult(new IPickResult() {
            @Override
            public void onPickResult(PickResult pickResult) {
                AddItemPresenter.getInstance(AddItemViewActivity.this).onPickResult(pickResult);
            }
        }).show(this);
    }

    void launchSelectAddressActivity() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                    .setCountry("PL")
                    .build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(AddItemViewActivity.this);
            startActivityForResult(intent, RequestCodes.REQUEST_CODE_ADDRESS);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            showSelectAddressError(e.getMessage());
        }
    }

    void launchDeleteConfirmationDialog(final boolean isMainPhoto, final int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(R.string.confirmation_delete_photo);
        builder1.setCancelable(true);
        builder1.setTitle(R.string.confirmation_delete_photo_title);

        builder1.setPositiveButton(
                R.string.delete,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (isMainPhoto) {
                            AddItemPresenter.getInstance(AddItemViewActivity.this).onMainPhotoDeleteConfirmed(photosAdapter.getPhotos());
                        } else {
                            AddItemPresenter.getInstance(AddItemViewActivity.this).onListPhotoDeleteConfirmed(position);
                        }
                    }
                });

        builder1.setNegativeButton(
                R.string.dismiss,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    void launchFullscreenGallery(ArrayList<Bitmap> images, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("images", images);
        bundle.putInt("position", position);

        GalleryFragment newFragment = GalleryFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "fullscreenGallery");
    }

    void launchCategorySelectDialog() {
        Intent intent = new Intent(AddItemViewActivity.this, CategorySelectFragmentView.class);
        startActivityForResult(intent, RequestCodes.REQUEST_CODE_SELECT);
    }
}
