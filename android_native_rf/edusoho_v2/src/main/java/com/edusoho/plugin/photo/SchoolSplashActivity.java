
package com.edusoho.plugin.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.course.SchoolCourseActivity;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

public class SchoolSplashActivity extends ActionBarBaseActivity {

    private ViewPager mViewPager;
    private Context mContext;
    private Bitmap cacheBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        setContentView(R.layout.sch_splash_layout);
        cacheBitmap = app.query.getCachedImage(R.drawable.defaultpic);
        hideActionBar();
        initView();
    }

    private void initView()
    {
        mViewPager = (ViewPager) findViewById(R.id.images_pager);
        Intent dataIntent = getIntent();
        String[] images = (String[]) dataIntent.getSerializableExtra("images");

        if (images != null && images.length > 0) {
            SamplePagerAdapter adapter = new SamplePagerAdapter(mContext, images);
            mViewPager.setAdapter(adapter);
            mViewPager.setOnPageChangeListener(adapter);
            return;
        }

        startMain();
    }

    private void startMain()
    {
        app.mEngine.runNormalPlugin("DefaultPageActivity", mActivity, null);
        finish();
    }

    public static void start(Context context, String schoolName, String[] imageArray)
    {
        Intent intent = new Intent();
        intent.setClass(context, SchoolSplashActivity.class);
        if (!checkIsSaveSchool(context, schoolName)) {
            intent.putExtra("images", imageArray);
        }
        saveSchoolHistory(context, schoolName);
        context.startActivity(intent);
    }

    private static boolean checkIsSaveSchool(Context context, String schoolName)
    {
        SharedPreferences sp = context.getSharedPreferences("school_history", Context.MODE_APPEND);
        int count = sp.getInt(schoolName, 0);
        return count > 0 ? true: false;
    }

    private static void saveSchoolHistory(Context context, String schoolName)
    {
        SharedPreferences sp = context.getSharedPreferences("school_history", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(schoolName, 1);
        editor.commit();
    }

    public class SamplePagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

        private Context mContext;
        private String[] mImages;
        private int mCurrentIndex;
        private LinearLayout mIndexLayout;
        private ArrayList<View> mIndexViewList;
        private View mEnterSchBtn;

        public SamplePagerAdapter(Context context, String[] images)
        {
            mImages = images;
            mContext = context;
            mIndexViewList = new ArrayList<View>();
            mIndexLayout = (LinearLayout) findViewById(R.id.viewpager_index_layout);
            for (int i = 0; i < images.length; i++) {
                ImageView indexView = new ImageView(mContext);
                indexView.setLayoutParams(new LayoutParams(15, 15));
                indexView.setPadding(2, 2, 2, 2);
                if (i == 0) {
                    indexView.setImageResource(R.drawable.viewpager_index_bg_sel);
                } else {
                    indexView.setImageResource(R.drawable.viewpager_index_bg_normal);
                }

                mIndexLayout.addView(indexView);
                mIndexViewList.add(indexView);
            }

            mEnterSchBtn = findViewById(R.id.enter_sch_btn);
            mEnterSchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startMain();
                }
            });
        }

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setScaleType(ImageView.ScaleType.FIT_XY);
            app.query.id(photoView).image(mImages[position], false, true, 0, 0, cacheBitmap, AQuery.FADE_IN);
            container.addView(photoView);
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
        public void onPageSelected(int position) {
            mCurrentIndex = position;
            ImageView selView = null;
            for (int i = 0; i < mIndexViewList.size(); i++) {
                selView = (ImageView) mIndexViewList.get(i);
                if (i == position) {
                    selView.setImageResource(R.drawable.viewpager_index_bg_sel);
                } else {
                    selView.setImageResource(R.drawable.viewpager_index_bg_normal);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int currentIndex, float arg1, int offset) {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
