package boyko.alex.easy_way.frontend.item.item_edit;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * Created by Sasha on 06.12.2017.
 */

public class DataFragment extends Fragment {
    private ArrayList listPhotos;
    private Bitmap mainPhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setListPhotos(ArrayList listPhotos) {
        this.listPhotos = listPhotos;
    }

    public ArrayList getListPhotos() {
        return listPhotos;
    }

    public Bitmap getMainPhoto() {
        return mainPhoto;
    }

    public void setMainPhoto(Bitmap mainPhoto) {
        this.mainPhoto = mainPhoto;
    }
}
