package boyko.alex.easy_way.frontend.explore;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.models.Category;
import boyko.alex.easy_way.backend.models.Item;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.models.PriceType;

/**
 * Created by Sasha on 04.11.2017.
 *
 * This is the adapter for items. Can be 4 modes.
 */

public class ItemsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int MODE_GRID = 0, MODE_LINEAR_VERTICAL = 1, MODE_LINEAR_HORIZONTAL = 2, MODE_MY_OFFERS = 3;
    private final int ITEM_ITEM = 0, ITEM_LOADING = 1;

    private ArrayList<Object> items;
    private OnItemClickListener onItemClickListener;
    private int mode;

    public interface OnItemClickListener {
        void onItemClicked(int position);
        void onLikeClicked(int position);
        void onEditClicked(int position);
        void onDeleteClicked(int position);
    }

    public ItemsRecyclerAdapter(int mode) {
        items = new ArrayList<>();
        this.mode = mode;
    }

    public ArrayList<Object> getItems() {
        return items;
    }

    public int getMode() {
        return mode;
    }

    void setMode(int mode) {
        this.mode = mode;
    }

    public void setItems(ArrayList<Object> items) {
        this.items = items;
    }

    public void addItems(ArrayList<Object> items) {
        this.items.addAll(items);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM_ITEM) {
            if (mode == MODE_MY_OFFERS) {
                View itemView = inflater.inflate(R.layout.item_explore_with_actions, parent, false);
                viewHolder = new ItemHolderWithActions(itemView);
            } else {
                if (mode == MODE_GRID || mode == MODE_LINEAR_VERTICAL) {
                    View itemView = inflater.inflate(R.layout.item_explore_grid, parent, false);
                    viewHolder = new ItemHolder(itemView);
                } else {
                    View itemView = inflater.inflate(R.layout.item_explore_linear, parent, false);
                    viewHolder = new ItemHolder(itemView);
                }
            }
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
            if(mode == MODE_MY_OFFERS) bindItemWithActions((ItemHolderWithActions)holder, position);
            else bindItem((ItemHolder) holder, position);
        } else {
            ((HolderLoading) holder).progressBar.setIndeterminateTintList(ColorStateList.valueOf(ContextCompat.getColor(ApplicationController.getInstance(), R.color.color_accent)));
            if (mode == MODE_GRID) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
                holder.itemView.setLayoutParams(layoutParams);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Item) {
            return ITEM_ITEM;
        }
        return ITEM_LOADING;
    }

    private void bindItem(final ItemHolder holder, int position) {
        Item item = (Item) items.get(position);
        holder.photo.requestLayout();
        if (item.mainPhoto != null) {
            holder.photo.setVisibility(View.VISIBLE);
            holder.noPhotoLayout.setVisibility(View.GONE);
            try {
                Glide.with(ApplicationController.getInstance())
                        .load(new URL(item.mainPhoto))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                        .apply(RequestOptions.noTransformation())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.photo.setVisibility(View.GONE);
                                holder.noPhotoLayout.setVisibility(View.VISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(holder.photo);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            holder.photo.setVisibility(View.GONE);
            holder.noPhotoLayout.setVisibility(View.VISIBLE);
        }
        if (item.title != null) {
            holder.title.setText(item.title);
        }

        if (item.price != 0) {
            String formattedPrice = String.format(Locale.getDefault(), "%.2f", item.price) + " zł";
            PriceType priceType = DataMediator.getPriceType(item.priceTypeId);
            if (priceType != null && priceType.shortName != null)
                formattedPrice += priceType.shortName;
            holder.price.setText(formattedPrice);
        }

        Category category = DataMediator.getCategory(item.categoryId);
        if (category != null && category.name != null) {
            holder.category.setText(category.name);
        }

        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClicked(holder.getAdapterPosition());
            }
        });

        if (item.isLiked) holder.like.setImageResource(R.drawable.ic_favorite_red_24px);
        else holder.like.setImageResource(R.drawable.ic_favorite_border_white_24px);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onLikeClicked(holder.getAdapterPosition());
            }
        });
    }

    private void bindItemWithActions(final ItemHolderWithActions holder, int position){
        Item item = (Item) items.get(position);
        holder.photo.requestLayout();
        if (item.mainPhoto != null) {
            holder.photo.setVisibility(View.VISIBLE);
            holder.noPhotoLayout.setVisibility(View.GONE);
            try {
                Glide.with(ApplicationController.getInstance())
                        .load(new URL(item.mainPhoto))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                        .apply(RequestOptions.noTransformation())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.photo.setVisibility(View.GONE);
                                holder.noPhotoLayout.setVisibility(View.VISIBLE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(holder.photo);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            holder.photo.setVisibility(View.GONE);
            holder.noPhotoLayout.setVisibility(View.VISIBLE);
        }
        if (item.title != null) {
            holder.title.setText(item.title);
        }

        if (item.price != 0) {
            String formattedPrice = String.format(Locale.getDefault(), "%.2f", item.price) + " zł";
            PriceType priceType = DataMediator.getPriceType(item.priceTypeId);
            if (priceType != null && priceType.shortName != null)
                formattedPrice += priceType.shortName;
            holder.price.setText(formattedPrice);
        }

        Category category = DataMediator.getCategory(item.categoryId);
        if (category != null && category.name != null) {
            holder.category.setText(category.name);
        }

        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClicked(holder.getAdapterPosition());
            }
        });

        holder.like.setVisibility(View.GONE);
//        if (item.isLiked) holder.like.setImageResource(R.drawable.ic_favorite_red_24px);
//        else holder.like.setImageResource(R.drawable.ic_favorite_border_white_24px);
//
//        holder.like.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onItemClickListener.onLikeClicked(holder.getAdapterPosition());
//            }
//        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onEditClicked(holder.getAdapterPosition());
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onDeleteClicked(holder.getAdapterPosition());
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

    private class ItemHolderWithActions extends RecyclerView.ViewHolder {
        RelativeLayout background;
        LinearLayout noPhotoLayout;
        AppCompatImageView photo, like;
        TextView title, price, category;
        Button edit, delete;

        ItemHolderWithActions(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.explore_item_background);
            noPhotoLayout = itemView.findViewById(R.id.explore_no_photo_layout);
            photo = itemView.findViewById(R.id.explore_item_photo);
            like = itemView.findViewById(R.id.explore_item_like);
            title = itemView.findViewById(R.id.explore_item_title);
            price = itemView.findViewById(R.id.explore_item_price);
            category = itemView.findViewById(R.id.explore_item_category);
            edit = itemView.findViewById(R.id.item_explore_edit_button);
            delete = itemView.findViewById(R.id.item_explore_delete_button);
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
