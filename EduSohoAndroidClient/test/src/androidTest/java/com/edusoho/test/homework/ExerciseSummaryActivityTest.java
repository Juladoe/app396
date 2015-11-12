package com.edusoho.test.homework;

import android.content.Intent;
import android.test.UiThreadTest;

import com.edusoho.kuozhi.homework.ExerciseSummaryActivity;
import com.edusoho.test.base.BaseActivityUnitTestCase;

/**
 * Created by Melomelon on 2015/11/12.
 */
public class ExerciseSummaryActivityTest extends BaseActivityUnitTestCase<ExerciseSummaryActivity> {

    public ExerciseSummaryActivityTest() {
        super(ExerciseSummaryActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mLaunchIntent = new Intent(mInstrumentation.getTargetContext(), ExerciseSummaryActivity.class);
    }

    @UiThreadTest
    public void testGetActivity() {
        ExerciseSummaryActivity mActivity = getActivity();
        assertNotNull(mActivity);
    }
}
