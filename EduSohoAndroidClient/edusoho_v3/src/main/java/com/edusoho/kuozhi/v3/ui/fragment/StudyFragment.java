package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tree on 2017/4/25.
 */

public class StudyFragment extends BaseFragment {

    private TabLayout tbTitles;
    private ViewPager vpContent;
    private String[] mTabTitles;
    private String[] mFragmentNames;

    private StudyPagerAdapter studyPagerAdapter;
    private List<RefreshFragment> mRefreshFragmentList = new ArrayList<>();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_study);
    }



    @Override
    protected void initView(View view) {
        tbTitles = (TabLayout) view.findViewById(R.id.tl_titles);
        vpContent = (ViewPager) view.findViewById(R.id.vp_content);
        vpContent.setOffscreenPageLimit(4);
        initViewPager();
    }

    private void initViewPager() {
        if (app.loginUser != null && app.loginUser.userRole2String().contains("教师")) {
            mTabTitles = new String[]{"教学", "学习", "缓存", "收藏", "问答"};
            mFragmentNames = new String[]{"MyTeachFragment", "MyStudyFragment", "MyVideoCacheFragment", "MyFavoriteFragment", "MyQuestionFragment"};
        } else {
            mTabTitles = new String[]{"学习", "缓存", "收藏", "问答"};
            mFragmentNames = new String[]{"MyStudyFragment", "MyVideoCacheFragment", "MyFavoriteFragment", "MyQuestionFragment"};
        }
        studyPagerAdapter = new StudyPagerAdapter(getFragmentManager(), mTabTitles, mFragmentNames);
        vpContent.setAdapter(studyPagerAdapter);
        tbTitles.setupWithViewPager(vpContent);
        vpContent.addOnPageChangeListener(getOnPageChangeListener());
    }

    private ViewPager.OnPageChangeListener getOnPageChangeListener(){
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (0 == position) {
                    MobclickAgent.onEvent(mActivity, "i_study");
                } else if (1 == position) {
                    MobclickAgent.onEvent(mActivity, "i_cache");
                } else if (2 == position) {
                    MobclickAgent.onEvent(mActivity, "i_collection");
                } else if (3 == position) {
                    MobclickAgent.onEvent(mActivity, "i_Q&A");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }



    private class StudyPagerAdapter extends FragmentStatePagerAdapter{
        private String[] tabTitles;
        private String[] fragmentTags;

        public StudyPagerAdapter(FragmentManager fm, String[] titles, String[] tags) {
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
                            fragmentTags[position], mActivity, null);
                    break;
                case 1:
                    fragment = app.mEngine.runPluginWithFragment(
                            fragmentTags[position], mActivity, null);
                    break;
                case 2:
                    fragment = app.mEngine.runPluginWithFragment(
                            fragmentTags[position], mActivity, null);
                    break;
                case 3:
                    fragment = app.mEngine.runPluginWithFragment(
                            fragmentTags[position], getActivity(), null);
                    break;
                case 4:
                    fragment = app.mEngine.runPluginWithFragment(
                            fragmentTags[position], mActivity, null);
                    break;
            }
            if (!mRefreshFragmentList.contains(fragment)) {
                mRefreshFragmentList.add((RefreshFragment) fragment);
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

    public interface RefreshFragment{
        void refreshData();

        void setSwipeEnabled(int i);
    }

    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[]{new MessageType(Const.LOGIN_SUCCESS), new MessageType(Const.THIRD_PARTY_LOGIN_SUCCESS)};
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (messageType.type.equals(Const.LOGIN_SUCCESS) || messageType.type.equals(Const.THIRD_PARTY_LOGIN_SUCCESS)) {
            initViewPager();

            for (RefreshFragment fragment : mRefreshFragmentList) {
                fragment.refreshData();
            }
        }
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View view) {
            super(view);
        }
    }
}
