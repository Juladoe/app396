package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.MyPostedThreadFragment;
import com.edusoho.kuozhi.v3.ui.fragment.MyRepliedThreadFragment;
import com.edusoho.kuozhi.v3.view.EduSohoViewPager;

import java.util.ArrayList;
import java.util.List;

import extensions.PagerSlidingTabStrip;

/**
 * Created by melomelon on 16/2/26.
 */
public class MyThreadActivity extends ActionBarBaseActivity {

    private PagerSlidingTabStrip mTab;
    private EduSohoViewPager mViewPager;

    private List<Fragment> mFragmentList;
    private String[] mFragmentTitles = {"我发起的","我回复的"};

    private MyPostedThreadFragment myPostedThreadFragment;
    private MyRepliedThreadFragment myRepliedThreadFragment;

    private MyThreadPageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_thtead_activity_layout);
        setBackMode(BACK,"我的问答");
        initData();
    }

    public void initData(){
        mFragmentList = new ArrayList<Fragment>();

        mTab = (PagerSlidingTabStrip) findViewById(R.id.my_thread_tab);
        mTab.setIndicatorColor(R.color.material_green);


        mViewPager = (EduSohoViewPager) findViewById(R.id.my_thread_viewpager);
        mViewPager.setIsPagingEnable(false);

        myPostedThreadFragment = new MyPostedThreadFragment();
        myRepliedThreadFragment = new MyRepliedThreadFragment();
        mFragmentList.add(myPostedThreadFragment);
        mFragmentList.add(myRepliedThreadFragment);

        mAdapter = new MyThreadPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTab.setViewPager(mViewPager);

    }

    public class MyThreadPageAdapter extends FragmentPagerAdapter{

        public MyThreadPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles[position];
        }
    }
}
