package boyko.alex.easy_way.frontend.explore;

import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.backend.models.ItemBase;
import boyko.alex.rentit.R;

/**
 * Created by Sasha on 04.11.2017.
 */

public class ItemsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_ITEM = 0, ITEM_LOADING = 1;
    private ArrayList<Object> items;
    private OnItemClickListener onItemClickListener;

    interface OnItemClickListener {
        void onItemClicked(int position);
    }

    ItemsRecyclerAdapter() {
        items = new ArrayList<>();
    }

    ArrayList<Object> getItems() {
        return items;
    }

    void setItems(ArrayList<Object> items) {
        this.items = items;
    }

    void addItems(ArrayList<Object> items) {
        this.items.addAll(items);
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM_ITEM) {
            View itemView = inflater.inflate(R.layout.item_explore, parent, false);
            viewHolder = new ItemHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.item_loading, parent, false);
            viewHolder = new HolderLoading(itemView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int itemType = getItemViewType(position);

        if (itemType == ITEM_ITEM) {
            bindItem((ItemHolder) holder, position);
        } else {
            ((HolderLoading) holder).progressBar.setIndeterminateTintList(ColorStateList.valueOf(ContextCompat.getColor(ApplicationController.getInstance(), R.color.color_accent)));
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
            holder.itemView.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof ItemBase) {
            return ITEM_ITEM;
        }
        return ITEM_LOADING;
    }

    private void bindItem(final ItemHolder holder, int position) {
        ItemBase item = (ItemBase) items.get(position);
        holder.photo.requestLayout();
        if (item.photo != null && !item.photo.isEmpty()) {
            holder.photo.setVisibility(View.VISIBLE);
            holder.noPhotoLayout.setVisibility(View.GONE);
            Glide.with(ApplicationController.getInstance())
                    .load(item.photo)
                    .fitCenter()
                    .crossFade()
                    .skipMemoryCache(true)
                    .dontTransform()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(holder.photo);
        }else{
            holder.photo.setVisibility(View.GONE);
            holder.noPhotoLayout.setVisibility(View.VISIBLE);
        }
        if (item.title != null) {
            holder.title.setText(item.title);
        }

        if (item.price != 0) {
            String formattedPrice = item.price + item.priceType.shortName;
            holder.price.setText(formattedPrice);
        }
        if (item.category != null) {
            holder.category.setText(item.category.name);
        }

        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null) onItemClickListener.onItemClicked(holder.getAdapterPosition());
            }
        });
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        RelativeLayout background;
        LinearLayout noPhotoLayout;
        AppCompatImageView photo, like;
        TextView title, price, category;

        ItemHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.explore_item_background);
            noPhotoLayout = itemView.findViewById(R.id.explore_no_photo_layout);
            photo = itemView.findViewById(R.id.explore_item_photo);
            like = itemView.findViewById(R.id.explore_item_like);
            title = itemView.findViewById(R.id.explore_item_title);
            price = itemView.findViewById(R.id.explore_item_price);
            category = itemView.findViewById(R.id.explore_item_category);
        }
    }

    private class HolderLoading extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        HolderLoading(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.item_loading_progress_bar);
        }
    }
}
