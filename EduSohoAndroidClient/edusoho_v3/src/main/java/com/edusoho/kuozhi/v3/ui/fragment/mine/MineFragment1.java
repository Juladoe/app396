package com.edusoho.kuozhi.v3.ui.fragment.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.ui.fragment.MyTabFragment;
import com.edusoho.kuozhi.v3.view.circleImageView.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by JesseHuang on 2017/2/6.
 */

public class MineFragment1 extends BaseFragment {

    private TextView tvName;
    private CircleImageView ivAvatar;
    private TextView tvUserType;
    private TabLayout tbTitles;
    private ViewPager vpContent;
    private View rlayoutFilterName;
    private String[] mTabTitles = {"学习", "缓存", "收藏", "问答"};
    private String[] mFragmentNames = {"MyStudyFragment", "MyStudyFragment", "MyFavoriteFragment", "MyStudyFragment"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_mine1);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUserInfo();
        initViewPager();
    }

    @Override
    protected void initView(View view) {
        tvName = (TextView) view.findViewById(R.id.tv_name);
        ivAvatar = (CircleImageView) view.findViewById(R.id.iv_avatar);
        tvUserType = (TextView) view.findViewById(R.id.tv_avatar_type);
        tbTitles = (TabLayout) view.findViewById(R.id.tl_titles);
        vpContent = (ViewPager) view.findViewById(R.id.vp_content);
        vpContent.setOffscreenPageLimit(3);
        vpContent.addOnPageChangeListener(getTabLayoutPageChangeListener());

        rlayoutFilterName = view.findViewById(R.id.rlayout_filter_name);
    }

    private void initUserInfo() {
        tvName.setText(app.loginUser.nickname);
        tvUserType.setText(app.loginUser.userRole2String());
        ImageLoader.getInstance().displayImage(app.loginUser.getMediumAvatar(), ivAvatar, app.mAvatarOptions);
    }

    private void initViewPager() {
        MinePagerAdapter minePagerAdapter = new MinePagerAdapter(getFragmentManager(), mTabTitles, mFragmentNames);
        vpContent.setAdapter(minePagerAdapter);
        tbTitles.setupWithViewPager(vpContent);
    }

    private ViewPager.OnPageChangeListener getTabLayoutPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0 || position == 3) {
                    setFilterLayoutVisible(true);
                } else {
                    setFilterLayoutVisible(false);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

    private void setFilterLayoutVisible(boolean visible) {
        rlayoutFilterName.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private class MinePagerAdapter extends FragmentPagerAdapter {

        private String[] tabTitles;
        private String[] fragmentTags;

        public MinePagerAdapter(FragmentManager fm, String[] titles, String[] tags) {
            super(fm);
            tabTitles = titles;
            fragmentTags = tags;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = app.mEngine.runPluginWithFragment(
                            fragmentTags[position], mActivity, new PluginFragmentCallback() {
                                @Override
                                public void setArguments(Bundle bundle) {
                                    bundle.putInt(MyTabFragment.TYPE, MyTabFragment.TYPE_STUDY);
                                }
                            });
                    break;
                case 1:
                    fragment = app.mEngine.runPluginWithFragment(
                            fragmentTags[position], mActivity, new PluginFragmentCallback() {
                                @Override
                                public void setArguments(Bundle bundle) {
                                    bundle.putInt(MyTabFragment.TYPE, MyTabFragment.TYPE_CACHE);
                                }
                            });
                    break;
                case 2:
                    fragment = app.mEngine.runPluginWithFragment(
                            fragmentTags[position], mActivity, new PluginFragmentCallback() {
                                @Override
                                public void setArguments(Bundle bundle) {
                                    bundle.putInt(MyTabFragment.TYPE, MyTabFragment.TYPE_COLLECT);
                                }
                            });
                    break;
                case 3:
                    fragment = app.mEngine.runPluginWithFragment(
                            fragmentTags[position], getActivity(), new PluginFragmentCallback() {
                                @Override
                                public void setArguments(Bundle bundle) {
                                    bundle.putInt(MyTabFragment.TYPE, MyTabFragment.TYPE_ASK);
                                }
                            });
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
