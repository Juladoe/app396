package com.edusoho.test;

import android.support.v4.app.Fragment;
import android.test.UiThreadTest;

import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.view.swipemenulistview.SwipeMenuListView;

import junit.framework.Assert;

/**
 * Created by JesseHuang on 15/8/19.
 */
public class NewsFragmentTestCase extends BaseFragmentTestCase<NewsFragment> {

    public NewsFragmentTestCase() {
        super(NewsFragment.class);
    }

    @UiThreadTest
    public void testGetFragment() {
        Fragment mFragment = getFragment();
        assertNotNull(mFragment);
    }

    @UiThreadTest
    public void testInitWidget() {
        SwipeMenuListView swipeMenuListView = (SwipeMenuListView) getFragment().getView().findViewById(R.id.lv_news_list);
        Assert.assertNotNull(swipeMenuListView);
    }

}
