package com.edusoho.kuozhi.v3.view.photo;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import java.util.List;
import photoview.PhotoView;
import photoview.PhotoViewAttacher;

public class ViewPagerActivity extends ActionBarBaseActivity {

    private ViewPager mViewPager;
    private TextView mViewPaperLabel;
    private int mIndex;
    private int mTotalCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setWindowAnimations(R.style.WindowZoomAnimation);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_layout);
        initView();
    }

    private void initView() {
        mViewPaperLabel = (TextView) findViewById(R.id.images_label);
        mViewPager = (HackyViewPager) findViewById(R.id.images_pager);
        Intent dataIntent = getIntent();
        mIndex = dataIntent.getIntExtra("index", 0);
        String[] images;
        if (dataIntent.hasExtra("imageList")) {
            ArrayList<String> list = dataIntent.getStringArrayListExtra("imageList");
            images = getImageUrls(list);
        } else {
            images = (String[]) dataIntent.getSerializableExtra("images");
        }

        mTotalCount = images.length;
        if (images != null && images.length > 0) {
            SamplePagerAdapter adapter = new SamplePagerAdapter(images);
            mViewPager.setAdapter(adapter);
            mViewPager.setOnPageChangeListener(adapter);
            mViewPager.setCurrentItem(mIndex);
            updateViewPaperLabel();
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
            if (mImages[position].contains(EdusohoApp.getChatCacheFile() + Const.UPLOAD_IMAGE_CACHE_FILE)) {
                ImageLoader.getInstance().displayImage("file://" + mImages[position], photoView, EdusohoApp.app.mOptions);
            } else {
                ImageLoader.getInstance().displayImage(mImages[position], photoView, EdusohoApp.app.mOptions);
            }
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    finish();
                }
            });

            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
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
            mIndex = position;
            updateViewPaperLabel();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    protected void updateViewPaperLabel() {
        mViewPaperLabel.setText(String.format("%d/%d", mIndex + 1, mTotalCount));
    }
}
