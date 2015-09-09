package com.edusoho.test;

import android.test.UiThreadTest;
import android.widget.ListView;

import com.edusoho.kuozhi.v3.ui.BulletinActivity;
import com.edusoho.test.base.BaseActivityUnitTestCase;

/**
 * Created by JesseHuang on 15/8/25.
 */
public class BulletinActivityTest extends BaseActivityUnitTestCase<BulletinActivity> {
    public BulletinActivityTest() {
        super(BulletinActivity.class);
    }

    @UiThreadTest
    public void testBulletinActivity() {
        BulletinActivity mActivity = getActivity();
        assertNotNull(mActivity);
    }

    @UiThreadTest
    public void testBulletinActivityLayout() {
        BulletinActivity mActivity = getActivity();
        ListView mListView = (ListView) mActivity.findViewById(R.id.lv_bulletin);
        assertNotNull(mListView);
    }
}
