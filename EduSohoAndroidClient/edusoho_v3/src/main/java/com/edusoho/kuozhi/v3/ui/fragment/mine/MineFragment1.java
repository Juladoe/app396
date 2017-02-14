package com.edusoho.kuozhi.v3.ui.fragment.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.ui.fragment.MyTabFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.circleImageView.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 2017/2/6.
 */

public class MineFragment1 extends BaseFragment {

    private TextView tvName;
    private CircleImageView ivAvatar;
    private TextView tvUserType;
    private TabLayout tbTitles;
    private ViewPager vpContent;
    private String[] mTabTitles = {"学习", "缓存", "收藏", "问答"};
    private String[] mFragmentNames = {"MyStudyFragment", "MyVideoCacheFragment", "MyFavoriteFragment", "MyQuestionFragment"};

    private MinePagerAdapter minePagerAdapter;

    private List<RefreshFragment> mRefreshFragmentList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_mine1);
    }

    @Override
    protected void initView(View view) {
        tvName = (TextView) view.findViewById(R.id.tv_name);
        ivAvatar = (CircleImageView) view.findViewById(R.id.iv_avatar);
        tvUserType = (TextView) view.findViewById(R.id.tv_avatar_type);
        tbTitles = (TabLayout) view.findViewById(R.id.tl_titles);
        vpContent = (ViewPager) view.findViewById(R.id.vp_content);
        vpContent.setOffscreenPageLimit(3);
        initUserInfo();
        initViewPager();
    }

    private void initUserInfo() {
        if (app.loginUser != null) {
            tvName.setText(app.loginUser.nickname);
            tvUserType.setText(app.loginUser.userRole2String());
            ImageLoader.getInstance().displayImage(app.loginUser.getMediumAvatar(), ivAvatar, app.mAvatarOptions);
        }
    }

    private void initViewPager() {
        minePagerAdapter = new MinePagerAdapter(getFragmentManager(), mTabTitles, mFragmentNames);
        vpContent.setAdapter(minePagerAdapter);
        tbTitles.setupWithViewPager(vpContent);
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

    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[]{new MessageType(Const.LOGIN_SUCCESS)};
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (messageType.type.equals(Const.LOGIN_SUCCESS)) {
            initUserInfo();
            initViewPager();

            for (RefreshFragment fragment : mRefreshFragmentList) {
                fragment.refreshData();
            }
        }
        Log.d("develop", "refreshData: " + this.getClass().getSimpleName());
    }

    public interface RefreshFragment {
        void refreshData();
    }
}
