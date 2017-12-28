package boyko.alex.easy_way.frontend.item.item_edit;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * Created by Sasha on 06.12.2017.
 */

public class DataFragment extends Fragment {
    private ArrayList<Object> items;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public ArrayList<Object> getItems() {
        return items;
    }

    public void setItems(ArrayList<Object> items) {
        this.items = items;
    }
}
