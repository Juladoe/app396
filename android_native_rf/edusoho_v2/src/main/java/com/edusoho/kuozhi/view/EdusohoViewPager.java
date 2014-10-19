package com.edusoho.kuozhi.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.edusoho.kuozhi.R;
import com.edusoho.plugin.photo.HackyViewPager;

/**
 * Created by howzhi on 14-8-8.
 */
public class EdusohoViewPager extends RelativeLayout {

    private PointLayout mPointLayout;
    private Context mContext;
    private HackyViewPager mHackyViewPager;
    private int current;

    public EdusohoViewPager(Context context) {
        super(context);
        mContext = context;
    }

    public EdusohoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs)
    {
        mHackyViewPager = new HackyViewPager(mContext);
        mPointLayout = new PointLayout(mContext);

        addView(mHackyViewPager);
        addView(mPointLayout);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPointLayout.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mPointLayout.setLayoutParams(layoutParams);
    }

    public void setAdapter(PagerAdapter adapter)
    {
        mHackyViewPager.setAdapter(adapter);
        mPointLayout.addPointImages(adapter.getCount());
        mHackyViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPointLayout.refresh();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void setCurrentItem(int index)
    {
        current = index;
        mHackyViewPager.setCurrentItem(index);
    }

    class PointLayout extends LinearLayout
    {
        private int mPointNormalSrc = R.drawable.viewpager_point_normal;
        private int mPointSellSrc = R.drawable.viewpager_point_sel;

        private int mCount;
        private int mPadding = 5;

        public PointLayout(Context context) {
            super(context);
            setPadding(mPadding, mPadding, mPadding, mPadding);
        }

        public void addPointImages(int count)
        {
            mCount = count;
            for (int i=0; i < count; i++) {
                ImageView imageView = new ImageView(mContext);
                addView(imageView);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                layoutParams.bottomMargin = mPadding;
                layoutParams.topMargin = mPadding;
                layoutParams.leftMargin = mPadding;
                layoutParams.rightMargin = mPadding;
                imageView.setLayoutParams(layoutParams);
            }
            refresh();
        }

        public void refresh()
        {
            for (int i=0; i < mCount; i++) {
                ImageView imageView = (ImageView) getChildAt(i);
                if (i == mHackyViewPager.getCurrentItem()) {
                    imageView.setImageResource(mPointSellSrc);
                    continue;
                }
                imageView.setImageResource(mPointNormalSrc);
            }
        }
    }
}
