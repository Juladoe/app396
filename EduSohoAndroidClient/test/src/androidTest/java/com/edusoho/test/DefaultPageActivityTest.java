package com.edusoho.test;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityUnitTestCase;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.ContextThemeWrapper;
import android.widget.LinearLayout;
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;
import com.edusoho.kuozhi.v3.view.EduSohoTextBtn;
import com.edusoho.kuozhi.v3.view.EduToolBar;

import org.junit.Test;
/**
 * Created by howzhi on 15/8/13.
 */

@MediumTest
public class DefaultPageActivityTest extends ActivityUnitTestCase<DefaultPageActivity> {

    private Intent mLaunchIntent;
    private Instrumentation mInstrumentation;
    private DefaultPageActivity mActivity;

    public DefaultPageActivityTest() {
        super(DefaultPageActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mInstrumentation = getInstrumentation();
        ContextThemeWrapper context = new ContextThemeWrapper(
                mInstrumentation.getTargetContext(), R.style.AppThemeNoActionBar);
        Context targetContext = context.createPackageContext("com.edusoho.kuozhi", Context.CONTEXT_IGNORE_SECURITY);
        setActivityContext(context);
        initApplicationConfig();
        TestEduSohoApp app = (TestEduSohoApp) mInstrumentation.newApplication(
                getClass().getClassLoader(), TestEduSohoApp.class.getName(), targetContext);
        mInstrumentation.callApplicationOnCreate(app);
        setApplication(app);

        mLaunchIntent = new Intent(mInstrumentation.getTargetContext(),
                DefaultPageActivity.class);
    }

    private void initApplicationConfig() {
        Context context = mInstrumentation.getContext();
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("showSplash", false);
        editor.putBoolean("registPublicDevice", false);
        editor.putBoolean("startWithSchool", true);
        editor.putInt("msgSound", 1);
        editor.putInt("msgVibrate", 1);
        editor.commit();

        sp = context.getSharedPreferences("defaultSchool", Context.MODE_APPEND);
        editor = sp.edit();
        editor.putString("name", "edusoho");
        editor.putString("url", "http://trymob.edusoho.cn/mapi_v2");
        editor.putString("host", "http://trymob.edusoho.cn");
        editor.putString("logo", "");
        editor.commit();
    }

    @Override
    public DefaultPageActivity getActivity() {
        mActivity = super.getActivity();
        if (mActivity == null) {
            mActivity = startActivity(mLaunchIntent, null, null);
        }

        return mActivity;
    }

    @UiThreadTest
    public void testActivity() {
        mActivity = getActivity();
        assertNotNull(mActivity);
    }

    @UiThreadTest
    public void testActivityBtnLayout() {
        mActivity = getActivity();
        LinearLayout navLayout = (LinearLayout) mActivity.findViewById(com.edusoho.kuozhi.R.id.nav_bottom_layout);
        assertNotNull(navLayout);
        assertEquals(3, navLayout.getChildCount());

        /*EduSohoTextBtn mDownTabNews = (EduSohoTextBtn) mActivity.findViewById(com.edusoho.kuozhi.R.id.nav_tab_news);
        EduSohoTextBtn mDownTabFind = (EduSohoTextBtn) mActivity.findViewById(com.edusoho.kuozhi.R.id.nav_tab_find);
        EduSohoTextBtn mDownTabFriends = (EduSohoTextBtn) mActivity.findViewById(com.edusoho.kuozhi.R.id.nav_tab_friends);
        EduToolBar mToolBar = (EduToolBar) mActivity.findViewById(com.edusoho.kuozhi.R.id.toolbar);
        DrawerLayout mDrawerLayout = (DrawerLayout) mActivity.findViewById(com.edusoho.kuozhi.R.id.drawer_layout);
*/
    }
}
