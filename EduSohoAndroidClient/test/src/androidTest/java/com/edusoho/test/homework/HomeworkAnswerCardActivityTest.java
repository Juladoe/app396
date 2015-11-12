package com.edusoho.test.homework;

import android.content.Intent;
import android.test.UiThreadTest;

import com.edusoho.kuozhi.homework.HomeworkAnswerCardActivity;
import com.edusoho.test.base.BaseActivityUnitTestCase;

/**
 * Created by Melomelon on 2015/11/12.
 */
public class HomeworkAnswerCardActivityTest extends BaseActivityUnitTestCase<HomeworkAnswerCardActivity> {

    public HomeworkAnswerCardActivityTest() {
        super(HomeworkAnswerCardActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mLaunchIntent = new Intent(mInstrumentation.getTargetContext(),
                HomeworkAnswerCardActivity.class);
    }

    @UiThreadTest
    public void testGetActivity() {
        HomeworkAnswerCardActivity mActivity = getActivity();
        assertNotNull(mActivity);
    }
}
