
package com.edusoho.plugin.photo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;

import java.util.ArrayList;
import java.util.List;

import photoview.PhotoView;

public class ViewPagerActivity extends ActionBarBaseActivity{

    private ViewPager mViewPager;
    private Bitmap cacheBitmap;
    private StringBuffer mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_layout);
        cacheBitmap = app.query.getCachedImage(R.drawable.defaultpic);
        initView();
    }

    private void initView()
    {
        mViewPager = (HackyViewPager) findViewById(R.id.images_pager);
        Intent dataIntent = getIntent();
        int index = dataIntent.getIntExtra("index", 0);
        String[] images = null;
        if (dataIntent.hasExtra("imageList")) {
            ArrayList<String> list = dataIntent.getStringArrayListExtra("imageList");
            images = getImageUrls(list);
        } else {
            images = (String[]) dataIntent.getSerializableExtra("images");
        }

        setBackMode(BACK, "图片预览");
        if (images != null && images.length > 0) {
            SamplePagerAdapter adapter = new SamplePagerAdapter(images);
            mViewPager.setAdapter(adapter);
            mViewPager.setOnPageChangeListener(adapter);
            mViewPager.setCurrentItem(index);
        }

        mTitle = new StringBuffer();
        mTitle.append("图片预览 ")
                .append(index + 1)
                .append("/")
                .append(images.length);
        setTitle(mTitle.toString());
    }

    private String[] getImageUrls(List<String> list)
    {
        String[] imageUrls = new String[list.size()];
        list.toArray(imageUrls);

        return imageUrls;
    }

    public static void start(Context context, int index, String[] imageArray)
    {
        Intent intent = new Intent();
        intent.setClass(context, ViewPagerActivity.class);
        intent.putExtra("index", index);
        intent.putExtra("images", imageArray);
        context.startActivity(intent);
    }

    public class SamplePagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener  {

        private String[] mImages;

        public SamplePagerAdapter(String[] images)
        {
            mImages = images;
        }

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            app.query.id(photoView).image(mImages[position], false, true, 0, 0, cacheBitmap, AQuery.FADE_IN);
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
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
            mTitle = new StringBuffer();
            mTitle.append("图片预览 ")
                    .append(position + 1)
                    .append("/")
                    .append(mImages.length);
            setTitle(mTitle.toString());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
