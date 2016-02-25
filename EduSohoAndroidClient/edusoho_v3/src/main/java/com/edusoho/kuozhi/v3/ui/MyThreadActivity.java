package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.MyPostedThreadFragment;
import com.edusoho.kuozhi.v3.ui.fragment.MyRepliedThreadFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by melomelon on 16/2/25.
 */
public class MyThreadActivity extends ActionBarBaseActivity {

    private  TabLayout mTabLayout;
    private ViewPager mViewpager;

    private MyPostedThreadFragment myPostedThreadFragment;
    private MyRepliedThreadFragment myRepliedThreadFragment;

    private List<Fragment> mFragmentList;
    private String[] tabTitleList = {"我发起的","我回复的"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_thread_activity_layout);

        mTabLayout = (TabLayout) findViewById(R.id.my_thread_tablayout);
        mViewpager = (ViewPager) findViewById(R.id.my_thread_viewpager);
        initData();
    }

    public void initData(){

        mFragmentList = new ArrayList<>();
        for (int i = 0;i<tabTitleList.length;i++){
            mTabLayout.addTab(mTabLayout.newTab().setText(tabTitleList[i]));
        }

        myPostedThreadFragment = new MyPostedThreadFragment();
        myRepliedThreadFragment = new MyRepliedThreadFragment();

        mFragmentList.add(myPostedThreadFragment);
        mFragmentList.add(myRepliedThreadFragment);

    }


    class ThreadPageAdapter extends PagerAdapter{

        public ThreadPageAdapter() {

        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;  //官方推荐写法
        }
    }

}
