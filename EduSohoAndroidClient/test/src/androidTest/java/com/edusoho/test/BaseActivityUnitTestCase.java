package com.edusoho.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;

/**
 * Created by howzhi on 15/8/17.
 */
public class BaseActivityUnitTestCase<T extends Activity> extends ActivityUnitTestCase<T> {

    protected Intent mLaunchIntent;
    protected Instrumentation mInstrumentation;

    public BaseActivityUnitTestCase(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    public T getActivity() {
        T mActivity = super.getActivity();
        if (mActivity == null) {
            mActivity = startActivity(mLaunchIntent, null, null);
        }

        return mActivity;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mInstrumentation = getInstrumentation();
        ContextThemeWrapper context = new ContextThemeWrapper(
                mInstrumentation.getTargetContext(), R.style.AppThemeNoActionBar);
        //Context targetContext = context.createPackageContext("com.edusoho.kuozhi", Context.CONTEXT_IGNORE_SECURITY);
        setActivityContext(context);

        TestEduSohoApp app = (TestEduSohoApp) mInstrumentation.newApplication(
                getClass().getClassLoader(), TestEduSohoApp.class.getName(), context);
        TestUtils.initApplication(app, mInstrumentation.getTargetContext());
        mInstrumentation.callApplicationOnCreate(app);
        setApplication(app);
    }
}
