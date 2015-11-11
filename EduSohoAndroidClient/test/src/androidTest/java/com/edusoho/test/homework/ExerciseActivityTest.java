package com.edusoho.test.homework;

import android.content.Intent;
import android.test.UiThreadTest;

import com.edusoho.kuozhi.homework.ExerciseActivity;
import com.edusoho.test.base.BaseActivityUnitTestCase;

/**
 * Created by Melomelon on 2015/11/10.
 */
public class ExerciseActivityTest extends BaseActivityUnitTestCase<ExerciseActivity>{

    public ExerciseActivityTest() {
        super(ExerciseActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mLaunchIntent = new Intent(mInstrumentation.getTargetContext(),
                ExerciseActivity.class);
    }

    @UiThreadTest
    public void testGetActivity() {
        ExerciseActivity mActivity = getActivity();
        assertNotNull(mActivity);
    }
}
