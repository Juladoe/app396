package com.edusoho.kuozhi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.ViewPagerAdapter;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.ui.SplashActivity;
import com.edusoho.kuozhi.v3.view.photo.HackyViewPager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by howzhi on 14-8-6.
 */
public class CustomSplashActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private TextView mViewPaperLabel;
    private int mIndex;
    private int mTotalCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
            ViewPagerAdapter adapter = new ViewPagerAdapter(images, new ViewPagerAdapter.ViewPagerAdapterListener() {
                @Override
                public void onFinish() {
                    finish();
                }
            });
            mViewPager.setAdapter(adapter);
            mViewPager.setCurrentItem(mIndex);
            mViewPager.setOnPageChangeListener(this);
            updateViewPaperLabel(mIndex);
        }
    }

    @Override
    protected void onDestroy() {
        MessageEngine.getInstance().sendMsg(SplashActivity.INIT_APP, null);
        super.onDestroy();
    }

    private String[] getImageUrls(List<String> list) {
        String[] imageUrls = new String[list.size()];
        list.toArray(imageUrls);

        return imageUrls;
    }

    public static void start(Context context, int index, String[] imageArray) {
        Intent intent = new Intent();
        intent.setClass(context, CustomSplashActivity.class);
        intent.putExtra("index", index);
        intent.putExtra("images", imageArray);
        context.startActivity(intent);
    }

    protected void updateViewPaperLabel(int position) {
        mViewPaperLabel.setText(String.format("%d/%d", position + 1, mTotalCount));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mIndex = position;
        updateViewPaperLabel(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
