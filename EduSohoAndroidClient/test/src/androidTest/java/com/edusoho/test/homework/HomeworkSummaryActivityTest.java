package com.edusoho.test.homework;

import android.content.Intent;
import android.test.UiThreadTest;

import com.edusoho.kuozhi.homework.HomeworkSummaryActivity;
import com.edusoho.test.base.BaseActivityUnitTestCase;

/**
 * Created by Melomelon on 2015/11/12.
 */
public class HomeworkSummaryActivityTest extends BaseActivityUnitTestCase<HomeworkSummaryActivity> {

    public HomeworkSummaryActivityTest() {
        super(HomeworkSummaryActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mLaunchIntent = new Intent(mInstrumentation.getTargetContext(),
                HomeworkSummaryActivity.class);
    }

    @UiThreadTest
    public void testGetActivity() {
        HomeworkSummaryActivity mActivity = getActivity();
        assertNotNull(mActivity);
    }
}
