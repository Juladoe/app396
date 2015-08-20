package com.edusoho.test;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.test.ActivityUnitTestCase;
import android.view.ContextThemeWrapper;

import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;

/**
 * Created by howzhi on 15/8/17.
 */
public class BaseActivityUnitTestCase<T extends Activity> extends ActivityUnitTestCase<T> {

    protected Intent mLaunchIntent;
    protected Instrumentation mInstrumentation;

    public BaseActivityUnitTestCase(Class<T> activityClass)
    {
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
        initApplicationConfig();
        TestEduSohoApp app = (TestEduSohoApp) mInstrumentation.newApplication(
                getClass().getClassLoader(), TestEduSohoApp.class.getName(), context);
        mInstrumentation.callApplicationOnCreate(app);
        setApplication(app);
    }

    private void initApplicationConfig() throws Exception {
        Context context = mInstrumentation.getTargetContext();
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("showSplash", false);
        editor.putBoolean("registPublicDevice", false);
        editor.putBoolean("startWithSchool", true);
        editor.putInt("msgSound", 1);
        editor.putInt("msgVibrate", 1);
        editor.commit();
        editor.apply();

        sp = context.getSharedPreferences("defaultSchool", Context.MODE_APPEND);
        editor = sp.edit();
        editor.putString("name", "edusoho");
        editor.putString("url", "http://trymob.edusoho.cn/mapi_v2");
        editor.putString("host", "http://trymob.edusoho.cn");
        editor.putString("logo", "");
        editor.commit();
        editor.apply();
    }
}
