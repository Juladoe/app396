package com.edusoho.kuozhi.v3.view.photo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import photoview.PhotoView;

public class ViewPagerActivity extends ActionBarBaseActivity {

    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_layout);
        initView();
    }

    private void initView() {
        mViewPager = (HackyViewPager) findViewById(R.id.images_pager);
        Intent dataIntent = getIntent();
        int index = dataIntent.getIntExtra("index", 0);
        String[] images;
        if (dataIntent.hasExtra("imageList")) {
            ArrayList<String> list = dataIntent.getStringArrayListExtra("imageList");
            images = getImageUrls(list);
        } else {
            images = (String[]) dataIntent.getSerializableExtra("images");
        }

        if (images != null && images.length > 0) {
            SamplePagerAdapter adapter = new SamplePagerAdapter(images);
            mViewPager.setAdapter(adapter);
            mViewPager.setOnPageChangeListener(adapter);
            mViewPager.setCurrentItem(index);
        }
    }

    private String[] getImageUrls(List<String> list) {
        String[] imageUrls = new String[list.size()];
        list.toArray(imageUrls);

        return imageUrls;
    }

    public static void start(Context context, int index, String[] imageArray) {
        Intent intent = new Intent();
        intent.setClass(context, ViewPagerActivity.class);
        intent.putExtra("index", index);
        intent.putExtra("images", imageArray);
        context.startActivity(intent);
    }

    public class SamplePagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

        private String[] mImages;

        public SamplePagerAdapter(String[] images) {
            mImages = images;
        }

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            ImageLoader.getInstance().displayImage(mImages[position], photoView, EdusohoApp.app.mOptions);
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }


    }
}
