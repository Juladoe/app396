package com.edusoho.test;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.test.UiThreadTest;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.edusoho.kuozhi.v3.ui.DownloadManagerActivity;
import com.edusoho.kuozhi.v3.ui.fragment.DownloadedFragment;
import com.edusoho.test.base.BaseActivityUnitTestCase;

/**
 * Created by JesseHuang on 15/8/25.
 */
public class DownloadedFragmentTest extends BaseActivityUnitTestCase<DownloadManagerActivity> {

    public DownloadedFragmentTest() {
        super(DownloadManagerActivity.class);
    }

    public Fragment getFragment() {
        Fragment fragment = null;
        try {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragment = Fragment.instantiate(getActivity(), DownloadedFragment.class.getName());
            fragment.setArguments(mLaunchIntent.getExtras());
            fragmentTransaction.replace(android.R.id.content, fragment);
            fragmentTransaction.commit();
            mInstrumentation.callActivityOnStart(getActivity());
            mInstrumentation.callActivityOnResume(getActivity());
        } catch (Exception ex) {
            Log.d("getFragment", ex.toString());
        }
        return fragment;
    }

//    public K getFragment() {
//        if (mFragment == null) {
//            FragmentTestActivity activity = super.getActivity();
//            mFragment = (K) activity.loadFragment(mFragmentClass.getName(), mLaunchIntent.getExtras());
//
//            mInstrumentation.callActivityOnStart(getActivity());
//            mInstrumentation.callActivityOnResume(getActivity());
//        }
//        return mFragment;
//    }


    @UiThreadTest
    public void testDownloadedFragment() {
//        Fragment mFragment = getFragment();
//        assertNotNull(mFragment);
        DownloadedFragment mFragment = (DownloadedFragment) getFragment();
        assertNotNull(mFragment);
    }

    @UiThreadTest
    public void testDownloadedFragmentLayout() {
        DownloadManagerActivity mActivity = getActivity();
        DownloadedFragment mFragment = (DownloadedFragment) getFragment();
        ActionBar actionBar = mActivity.getSupportActionBar();
        assertNotNull(actionBar);

        ViewPager mViewPagers = (ViewPager) mActivity.findViewById(R.id.viewpager_download);

        //DownloadManagerActivity.MyPagerAdapter myPagerAdapter = (DownloadManagerActivity.MyPagerAdapter) mViewPagers.getAdapter();
        View view = mFragment.getView();

        View mToolsLayout = view.findViewById(R.id.download_tools_layout);
        assertNotNull(mToolsLayout);
        assertEquals(0, mToolsLayout.getHeight());
        TextView mSelectAllBtn = (TextView) view.findViewById(R.id.tv_select_all);
        assertNotNull(mSelectAllBtn);
        TextView mDelBtn = (TextView) view.findViewById(R.id.tv_delete);
        assertNotNull(mDelBtn);
        ExpandableListView mListView = (ExpandableListView) view.findViewById(R.id.el_downloaded);
        assertNotNull(mListView);
    }


}
