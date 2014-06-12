
package com.edusoho.plugin.photo;
import uk.co.senab.photoview.PhotoView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.edusoho.kowzhi.R;
import com.edusoho.kowzhi.ui.BaseActivity;

public class ViewPagerActivity extends BaseActivity {

    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_layout);
        initView();
    }

    private void initView()
    {
        setBackMode("图片预览", true, null);
        mViewPager = (HackyViewPager) findViewById(R.id.images_pager);
        Intent dataIntent = getIntent();
        int index = dataIntent.getIntExtra("index", 0);
        String[] images = (String[]) dataIntent.getSerializableExtra("images");

        if (images != null && images.length > 0) {
            SamplePagerAdapter adapter = new SamplePagerAdapter(images);
            mViewPager.setAdapter(adapter);
            mViewPager.setCurrentItem(index);
        }
    }

    public static void start(Context context, int index, String[] imageArray)
    {
        Intent intent = new Intent();
        intent.setClass(context, ViewPagerActivity.class);
        intent.putExtra("index", index);
        intent.putExtra("images", imageArray);
        context.startActivity(intent);
    }

    public class SamplePagerAdapter extends PagerAdapter {

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
            app.query.id(photoView).image(R.drawable.defaultpic).image(mImages[position], false, true);
            // Now just add PhotoView to ViewPager and return it
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
    }

}
