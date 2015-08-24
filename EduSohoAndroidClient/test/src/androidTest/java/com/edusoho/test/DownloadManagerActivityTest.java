package com.edusoho.test;

import android.support.v4.view.ViewPager;
import android.test.UiThreadTest;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kuozhi.v3.ui.DownloadManagerActivity;

import extensions.PagerSlidingTabStrip;

/**
 * Created by JesseHuang on 15/8/24.
 */
public class DownloadManagerActivityTest extends BaseActivityUnitTestCase<DownloadManagerActivity> {
    public DownloadManagerActivityTest() {
        super(DownloadManagerActivity.class);
    }

    @UiThreadTest
    public void testDownloadManagerActivity() {
        DownloadManagerActivity mActivity = getActivity();
        assertNotNull(mActivity);
    }


    @UiThreadTest
    public void testLayout() {
        DownloadManagerActivity mActivity = getActivity();
        PagerSlidingTabStrip mPagerTab = (PagerSlidingTabStrip) mActivity.findViewById(R.id.tab_download);
        assertNotNull(mPagerTab);

        ViewPager mViewPagers = (ViewPager) mActivity.findViewById(R.id.viewpager_download);
        assertNotNull(mViewPagers);
        assertEquals(2, mViewPagers.getAdapter().getCount());

        int initCurrentItem = mViewPagers.getCurrentItem();
        DownloadManagerActivity.MyPagerAdapter viewPagerAdapter = (DownloadManagerActivity.MyPagerAdapter) mViewPagers.getAdapter();
        assertEquals(0, initCurrentItem);
        assertEquals(DownloadManagerActivity.DOWNLOAD_TITLES[initCurrentItem], viewPagerAdapter.getPageTitle(initCurrentItem));

        mViewPagers.setCurrentItem(1);

        int currentItem = mViewPagers.getCurrentItem();
        assertEquals(1, currentItem);
        assertEquals(DownloadManagerActivity.DOWNLOAD_TITLES[currentItem], viewPagerAdapter.getPageTitle(currentItem));

        TextView mDeviceSpaceInfo = (TextView) mActivity.findViewById(R.id.download_device_info);
        assertNotNull(mDeviceSpaceInfo);
        ProgressBar pbDownloadDeviceInfo = (ProgressBar) mActivity.findViewById(R.id.pb_download_device_info);
        assertNotNull(pbDownloadDeviceInfo);
    }

}
