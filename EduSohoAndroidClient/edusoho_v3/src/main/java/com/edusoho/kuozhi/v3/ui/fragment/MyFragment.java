package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;

/**
 * Created by remilia on 2017/1/5.
 */
public class MyFragment extends BaseFragment {

    private LinearLayout mLayoutTab;
    private ImageView mIvAvatar;
    private TextView mTvName;
    private TextView mTvAvatarType;
    private View mLayoutMy;
    private ViewPager mVpContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_my);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mLayoutTab = (LinearLayout) view.findViewById(R.id.layout_tab);
        mIvAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        mTvName = (TextView) view.findViewById(R.id.tv_name);
        mTvAvatarType = (TextView) view.findViewById(R.id.tv_avatar_type);
        mLayoutMy = view.findViewById(R.id.layout_my);
        mVpContent = (ViewPager) view.findViewById(R.id.vp_content);
        initEvent();
        initData();
    }

    private void initEvent() {
        mVpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        int length = mLayoutTab.getChildCount();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                setTab(position);
                mVpContent.setCurrentItem(position);
            }
        };
        for (int i = 0; i < length; i++) {
            View view = mLayoutTab.getChildAt(i);
            view.setTag(i);
            view.setOnClickListener(onClickListener);
        }
        mLayoutMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initData() {

    }

    private void setTab(int position) {
        int length = mLayoutTab.getChildCount();
        for (int i = 0; i < length; i++) {
            View view = mLayoutTab.getChildAt(i);
            if (view != null && view instanceof ViewGroup
                    && ((ViewGroup) view).getChildCount() == 2) {
                if (i == position) {
                    ((ViewGroup) view).getChildAt(1).setVisibility(View.VISIBLE);
                } else {
                    ((ViewGroup) view).getChildAt(1).setVisibility(View.GONE);
                }
            }
        }
    }
}
