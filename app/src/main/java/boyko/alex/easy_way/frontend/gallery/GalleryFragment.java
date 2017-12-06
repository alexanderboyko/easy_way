package boyko.alex.easy_way.frontend.gallery;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bogdwellers.pinchtozoom.view.ImageViewPager;

import java.util.ArrayList;

import boyko.alex.easy_way.R;

/**
 * Created by Sasha on 03.12.2017.
 */

public class GalleryFragment extends DialogFragment {
    private ArrayList<Bitmap> images;
    private ImageViewPager viewPager;
    private GalleryViewPagerAdapter adapter;
    private AppCompatImageView backIcon;
    private TextView title;
    private int selectedPosition = 0;

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fullscreen_images, container, false);

        images = getArguments().getParcelableArrayList("images");
        selectedPosition = getArguments().getInt("position");
        viewPager = v.findViewById(R.id.fragment_fullscreen_images_viewpager);
        title = v.findViewById(R.id.fragment_fullscreen_images_title);
        backIcon = v.findViewById(R.id.fragment_fullscreen_images_back);

        adapter = new GalleryViewPagerAdapter();

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                title.setText((position+1) + " of " + images.size());
                selectedPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(selectedPosition);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GalleryFragment.this.dismiss();
            }
        });

        return v;
    }

    private class GalleryViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        GalleryViewPagerAdapter(){}

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_fullscreen_image, container, false);

            AppCompatImageView imageViewPreview = view.findViewById(R.id.item_fullscreen_image_image);
            imageViewPreview.setImageBitmap(images.get(position));
            imageViewPreview.setOnTouchListener(new ImageMatrixTouchHandler(container.getContext()));
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;

            AppCompatImageView imageView = view.findViewById(R.id.item_fullscreen_image_image);
            imageView.setImageResource(0);

            container.removeView((View) object);
        }

        @Override
        public int getItemPosition (Object object) {
            return POSITION_NONE;
        }
    }
}
