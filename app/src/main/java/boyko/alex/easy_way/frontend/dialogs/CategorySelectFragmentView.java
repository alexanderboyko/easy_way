package boyko.alex.easy_way.frontend.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.RequestCodes;
import boyko.alex.easy_way.backend.models.Category;

/**
 * Created by Sasha on 04.12.2017.
 * <p>
 * Category select fragment.
 */

public class CategorySelectFragmentView extends AppCompatActivity {
    private CategoryRecyclerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_category_select);

        RecyclerView recyclerView = findViewById(R.id.select_category_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new CategoryRecyclerAdapter(DataMediator.getParentsCategories());
        if(getIntent().getBooleanExtra("allCategories", false)){
            Category category = new Category();
            category.id = "allCategories";
            category.name = ApplicationController.getInstance().getString(R.string.all_categories);
            adapter.getCategories().add(0, category);
        }
        adapter.setListener(new CategoryRecyclerAdapter.OnCategorySelectedListener() {
            @Override
            public void onCategoryClicked(int position) {
                onCategorySelected(position);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    void onCategorySelected(int position) {
        Category category = adapter.getCategories().get(position);
        ArrayList<Category> childCategories = DataMediator.getChildCategories(category.id);

        if (childCategories == null || childCategories.isEmpty()) {
            Intent intent = new Intent();
            intent.putExtra("categoryId", category.id);
            setResult(RequestCodes.RESULT_CODE_SELECTED, intent);
            finish();
        } else {
            adapter.setCategories(new ArrayList<Category>());
            adapter.notifyDataSetChanged();
            adapter.setCategories(childCategories);
            adapter.notifyItemRangeInserted(0, childCategories.size());
        }
    }
}
