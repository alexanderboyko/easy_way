package boyko.alex.easy_way.frontend.item.item_edit;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;

/**
 * Created by Sasha on 02.12.2017.
 *
 * This is lsit of horizontal line photos
 */

class PhotosLineRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_PHOTO = 0, ITEM_ADD_ANOTHER = 1, ITEM_INFO = 2;
    private ArrayList<String> photos;
    private OnItemClickListener onItemClickListener;

    interface OnItemClickListener {
        void onItemClicked(int position);

        void onItemLongClick(int position);

        void onItemDeleteClicked(int position);
    }

    PhotosLineRecyclerAdapter(OnItemClickListener onItemClickListener) {
        photos = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }

    ArrayList<String> getPhotos() {
        return photos;
    }

    void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM_PHOTO) {
            View itemView = inflater.inflate(R.layout.item_photo_line, parent, false);
            viewHolder = new PhotoHolder(itemView);
        }
        if (viewType == ITEM_ADD_ANOTHER) {
            View itemView = inflater.inflate(R.layout.item_photo_line_add, parent, false);
            viewHolder = new AddAnotherHolder(itemView);
        }
        if (viewType == ITEM_INFO) {
            View itemView = inflater.inflate(R.layout.item_photo_line_info, parent, false);
            viewHolder = new InfoHolder(itemView);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final int itemType = getItemViewType(position);

        if (itemType == ITEM_PHOTO) {
            try {
                Glide.with(ApplicationController.getInstance())
                        .load(new URL(photos.get(position)))
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                        .apply(RequestOptions.noTransformation())
                        .into(((PhotoHolder) holder).photo);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            ((PhotoHolder) holder).photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClicked(holder.getAdapterPosition());
                }
            });
            ((PhotoHolder) holder).photo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.onItemLongClick(holder.getAdapterPosition());
                    return true;
                }
            });
            ((PhotoHolder) holder).delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemDeleteClicked(holder.getAdapterPosition());
                }
            });
        } else {
            if (itemType == ITEM_ADD_ANOTHER) {
                ((AddAnotherHolder) holder).add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onItemClicked(holder.getAdapterPosition());
                    }
                });
                ((AddAnotherHolder) holder).info.setText(photos.get(position));
            }
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (photos.get(position).length() > 5) {
            return ITEM_PHOTO;
        } else {
            if (photos.get(position).equals("info")) return ITEM_INFO;
            else return ITEM_ADD_ANOTHER;
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        AppCompatImageView photo, delete;

        PhotoHolder(View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.item_photo_line_photo);
            delete = itemView.findViewById(R.id.item_photo_line_photo_delete);
        }
    }

    private class AddAnotherHolder extends RecyclerView.ViewHolder {
        AppCompatImageView add;
        TextView info;

        AddAnotherHolder(View itemView) {
            super(itemView);
            add = itemView.findViewById(R.id.item_photo_line_add_icon);
            info = itemView.findViewById(R.id.item_photo_line_add_info);
        }
    }

    private class InfoHolder extends RecyclerView.ViewHolder {
        TextView info;

        InfoHolder(View itemView) {
            super(itemView);
            info = itemView.findViewById(R.id.item_photo_line_info);
        }
    }
}
