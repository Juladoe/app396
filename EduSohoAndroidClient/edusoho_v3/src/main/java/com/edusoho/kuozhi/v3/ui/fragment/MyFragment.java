package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.test.FragmentViewPagerAdapter;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.HeadStopScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangruoyi on 2017/1/5.
 */
public class MyFragment extends BaseFragment {

    private LinearLayout mLayoutTab;
    private ImageView mIvAvatar;
    private TextView mTvName;
    private TextView mTvAvatarType;
    private HeadStopScrollView mParent;
    private View mLayoutMy;
    private ViewPager mVpContent;
    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentViewPagerAdapter mAdapter;

    private int mScrollHeadHeight = 121;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_my);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mParent = (HeadStopScrollView) view.findViewById(R.id.parent);
        mLayoutTab = (LinearLayout) view.findViewById(R.id.layout_tab);
        mIvAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        mTvName = (TextView) view.findViewById(R.id.tv_name);
        mTvAvatarType = (TextView) view.findViewById(R.id.tv_avatar_type);
        mLayoutMy = view.findViewById(R.id.layout_my);
        mVpContent = (ViewPager) view.findViewById(R.id.vp_content);
        mAdapter = new FragmentViewPagerAdapter(getChildFragmentManager(), mFragments);
        mVpContent.setAdapter(mAdapter);
        ImageLoader.getInstance().displayImage(app.loginUser.avatar, mIvAvatar, app.mAvatarOptions);
        mTvName.setText(app.loginUser.nickname);
        mTvAvatarType.setText(app.loginUser.type);
        mParent.setFirstViewHeight(AppUtil.dp2px(getActivity(), mScrollHeadHeight));
        RelativeLayout.LayoutParams vpParams = (RelativeLayout.LayoutParams) mVpContent.getLayoutParams();
        if (vpParams != null) {
            int bottom = AppUtil.dp2px(getActivity(), 44 + 50 + 45);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                bottom += AppUtil.dp2px(getActivity(), 25);
            }
            vpParams.height = AppUtil.getHeightPx(getActivity()) - bottom;
            mVpContent.setLayoutParams(vpParams);
        }
        setTab(0);
        initFragment();
        initEvent();
    }

    private void initFragment() {
        Fragment studyFragment = app.mEngine.runPluginWithFragment(
                "MyTabFragment", getActivity(), new PluginFragmentCallback() {
                    @Override
                    public void setArguments(Bundle bundle) {
                        bundle.putInt(MyTabFragment.TYPE, MyTabFragment.TYPE_STUDY);
                    }
                });
        mFragments.add(studyFragment);
        Fragment cacheFragment = app.mEngine.runPluginWithFragment(
                "MyTabFragment", getActivity(), new PluginFragmentCallback() {
                    @Override
                    public void setArguments(Bundle bundle) {
                        bundle.putInt(MyTabFragment.TYPE, MyTabFragment.TYPE_CACHE);
                    }
                });
        mFragments.add(cacheFragment);
        Fragment collectFragment = app.mEngine.runPluginWithFragment(
                "MyTabFragment", getActivity(), new PluginFragmentCallback() {
                    @Override
                    public void setArguments(Bundle bundle) {
                        bundle.putInt(MyTabFragment.TYPE, MyTabFragment.TYPE_COLLECT);
                    }
                });
        mFragments.add(collectFragment);
        Fragment askFragment = app.mEngine.runPluginWithFragment(
                "MyTabFragment", getActivity(), new PluginFragmentCallback() {
                    @Override
                    public void setArguments(Bundle bundle) {
                        bundle.putInt(MyTabFragment.TYPE, MyTabFragment.TYPE_ASK);
                    }
                });
        mFragments.add(askFragment);
        mAdapter.notifyDataSetChanged();
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
                Fragment fragment = mFragments.get(position);
                if(fragment instanceof MyTabFragment){
                    ((MyTabFragment) fragment).refresh();
                }
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
                final String url = String.format(
                        Const.MOBILE_APP_URL,
                        EdusohoApp.app.schoolHost,
                        String.format("main#/userinfo/%s",
                                app.loginUser.id)
                );
                CoreEngine.create(mContext).runNormalPlugin("WebViewActivity"
                        , getActivity(), new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.WEB_URL, url);
                            }
                        });
            }
        });
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
