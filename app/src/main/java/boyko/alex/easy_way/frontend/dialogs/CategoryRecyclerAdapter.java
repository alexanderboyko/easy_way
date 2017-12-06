package boyko.alex.easy_way.frontend.dialogs;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.models.Category;

/**
 * Created by Sasha on 05.12.2017.
 *
 * Categories recycler adapter
 */

class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryHolder> {
    private ArrayList<Category> categories;
    private OnCategorySelectedListener listener;

    interface OnCategorySelectedListener{
        void onCategoryClicked(int position);
    }

    CategoryRecyclerAdapter(ArrayList<Category> categories){
        this.categories = categories;
    }

    void setListener(OnCategorySelectedListener listener){
        this.listener = listener;
    }

    ArrayList<Category> getCategories(){
        return categories;
    }

    void setCategories(ArrayList<Category> categories){
        this.categories = categories;
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoryHolder holder, int position) {
        Category category = categories.get(position);

        if(category.iconUrl != null){
            holder.icon.setVisibility(View.VISIBLE);
            Glide.with(ApplicationController.getInstance())
                    .load(category.iconUrl)
                    .apply(RequestOptions.fitCenterTransform())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                    .apply(RequestOptions.noTransformation())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.icon.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.icon);
        }else{
            holder.icon.setVisibility(View.GONE);
        }

        holder.title.setText(category.name);
        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCategoryClicked(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryHolder extends RecyclerView.ViewHolder {
        AppCompatImageView icon;
        TextView title;
        LinearLayout background;

        CategoryHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.item_category_icon);
            title = itemView.findViewById(R.id.item_category_title);
            background = itemView.findViewById(R.id.item_category_background);
        }
    }
}
