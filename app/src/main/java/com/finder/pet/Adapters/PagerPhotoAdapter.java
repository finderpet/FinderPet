package com.finder.pet.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.finder.pet.R;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class PagerPhotoAdapter extends PagerAdapter {

    private Context context;
    private String[] imageUrls;

    public PagerPhotoAdapter(Context context, String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return imageUrls.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        //imageView.setScaleType(ImageView.ScaleType.MATRIX);
        Glide.with(context)
                .load(imageUrls[position])
                .placeholder(R.drawable.sin_imagen)
                .into(imageView);
        container.addView(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


}
