package boyko.alex.easy_way.frontend.explore;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
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
import boyko.alex.easy_way.backend.models.Review;
import boyko.alex.easy_way.libraries.DateHelper;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sasha on 27.11.2017.
 */

public class ReviewsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int MODE_ITEM_DETAILS = 0;
    private ArrayList<Review> reviews;
    private int mode;

    public ReviewsRecyclerAdapter(int mode){
        this.mode = mode;
        reviews = new ArrayList<>();
    }

    public ArrayList<Review> getReviews(){
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews){
        this.reviews = reviews;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (mode == MODE_ITEM_DETAILS) {
            View itemView = inflater.inflate(R.layout.item_review, parent, false);
            viewHolder = new ReviewHolder(itemView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Review review = reviews.get(position);

        if(mode == MODE_ITEM_DETAILS){
            bindItemDetailsReview((ReviewHolder)holder, review);
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    private void bindItemDetailsReview(final ReviewHolder holder, Review review){
        if(review.userName != null) holder.userName.setText(review.userName);
        if(review.createdAt != 0) holder.date.setText(DateHelper.getFormattedDateWithTime(review.createdAt));
        if(review.text != null) holder.text.setText(review.text);
        if (review.userPhoto != null) {
            holder.userPhoto.setVisibility(View.VISIBLE);
            holder.userNoPhotoLayout.setVisibility(View.GONE);
            Glide.with(ApplicationController.getInstance())
                    .load(review.userPhoto)
                    .apply(RequestOptions.fitCenterTransform())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                    .apply(RequestOptions.noTransformation())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.userPhoto.setVisibility(View.GONE);
                            holder.userNoPhotoLayout.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.userPhoto);
        }else{
            holder.userPhoto.setVisibility(View.GONE);
            holder.userNoPhotoLayout.setVisibility(View.VISIBLE);
        }
    }

    private class ReviewHolder extends RecyclerView.ViewHolder{
        CircleImageView userPhoto;
        LinearLayout userNoPhotoLayout;
        TextView userName, date, text;
        ReviewHolder(View itemView) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.item_review_user_photo);
            userNoPhotoLayout = itemView.findViewById(R.id.item_review_no_photo_layout);
            userName = itemView.findViewById(R.id.item_review_user_name);
            date = itemView.findViewById(R.id.item_review_date);
            text = itemView.findViewById(R.id.item_review_text);
        }
    }
}
