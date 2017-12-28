package boyko.alex.easy_way.frontend.inbox;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import boyko.alex.easy_way.ApplicationController;
import boyko.alex.easy_way.R;
import boyko.alex.easy_way.backend.DataMediator;
import boyko.alex.easy_way.backend.models.Dialog;
import boyko.alex.easy_way.backend.models.User;
import boyko.alex.easy_way.libraries.DateHelper;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sasha on 24.12.2017.
 * <p>
 * Adapter of dialogs
 */

class InboxRecyclerAdapter extends RecyclerView.Adapter<InboxRecyclerAdapter.DialogHolder> {
    private ArrayList<Dialog> dialogs;
    private OnDialogClickListener listener;

    interface OnDialogClickListener{
        void onDialogClicked(int position);
    }

    InboxRecyclerAdapter(ArrayList<Dialog> dialogs, OnDialogClickListener listener) {
        this.dialogs = dialogs;
        this.listener = listener;
    }

    public ArrayList<Dialog> getDialogs() {
        return dialogs;
    }

    public void setDialogs(ArrayList<Dialog> dialogs) {
        this.dialogs = dialogs;
    }

    @Override
    public DialogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DialogHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inbox, parent, false));
    }

    @Override
    public void onBindViewHolder(final DialogHolder holder, int position) {
        Dialog dialog = dialogs.get(position);
        User currentUser = DataMediator.getUser();

        setLastMessage(holder, dialog.lastMessage);
        setLastUpdate(holder, dialog.lastUpdate);

        if(!currentUser.id.equals(dialog.user1Id)){
            setPhoto(holder, dialog.user1Photo);
            setName(holder, dialog.user1FullName);
            setNotification(holder, dialog.user2HasUnreadMessage);
        }else{
            setPhoto(holder, dialog.user2Photo);
            setName(holder, dialog.user2FullName);
            setNotification(holder, dialog.user1HasUnreadMessage);
        }

        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDialogClicked(holder.getAdapterPosition());
            }
        });
    }

    private void setPhoto(final DialogHolder holder, String photo){
        if (photo != null) {
            holder.photo.setVisibility(View.VISIBLE);
            holder.noPhotoLayout.setVisibility(View.GONE);
            try {
                Glide.with(ApplicationController.getInstance())
                        .load(new URL(photo))
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
    }

    private void setName(DialogHolder holder, String name){
        holder.userName.setText(name);
    }

    private void setLastMessage(DialogHolder holder, String lastMessage){
        holder.lastMessage.setText(lastMessage);
    }

    private void setLastUpdate(DialogHolder holder, long lastUpdate){
        if(DateHelper.ifTimesFromOneDay(lastUpdate, DateHelper.getCurrentTime())){
            SimpleDateFormat simpleDateFormatArrivals = new SimpleDateFormat("HH:mm", Locale.getDefault());
            holder.lastUpdate.setText(simpleDateFormatArrivals.format(lastUpdate));
        }else{
            SimpleDateFormat simpleDateFormatArrivals = new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault());
            holder.lastUpdate.setText(simpleDateFormatArrivals.format(lastUpdate));
        }
    }

    private void setNotification(DialogHolder holder, boolean hasUnreadMessages){
        if(hasUnreadMessages){
            holder.lastMessage.setTypeface(null, Typeface.BOLD);
            holder.lastUpdate.setTypeface(null, Typeface.BOLD);
            holder.lastMessage.setTextColor(ContextCompat.getColor(ApplicationController.getInstance(), R.color.color_accent));
            holder.lastUpdate.setTextColor(ContextCompat.getColor(ApplicationController.getInstance(), R.color.color_accent));
        }else{
            holder.lastMessage.setTypeface(null, Typeface.NORMAL);
            holder.lastUpdate.setTypeface(null, Typeface.NORMAL);
            holder.lastMessage.setTextColor(ContextCompat.getColor(ApplicationController.getInstance(), R.color.secondary_text));
            holder.lastUpdate.setTextColor(ContextCompat.getColor(ApplicationController.getInstance(), R.color.secondary_text));
        }
    }

    @Override
    public int getItemCount() {
        return dialogs.size();
    }

    class DialogHolder extends RecyclerView.ViewHolder {
        RelativeLayout background;
        CircleImageView photo;
        LinearLayout noPhotoLayout;
        TextView userName, lastMessage, lastUpdate;

        DialogHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.item_inbox_background);
            photo = itemView.findViewById(R.id.item_inbox_photo);
            noPhotoLayout = itemView.findViewById(R.id.item_inbox_no_photo_layout);
            userName = itemView.findViewById(R.id.item_inbox_user_name);
            lastMessage = itemView.findViewById(R.id.item_inbox_message);
            lastUpdate = itemView.findViewById(R.id.item_inbox_data);
        }
    }
}
