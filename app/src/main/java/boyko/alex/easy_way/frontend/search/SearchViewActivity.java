package boyko.alex.easy_way.frontend.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import boyko.alex.easy_way.R;

/**
 * Created by Sasha on 09.11.2017.
 */

public class SearchViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_down_translate, R.anim.activity_close_scale);
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_down_translate);
    }
}
