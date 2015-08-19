package com.edusoho.test;

import android.content.Intent;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.LinearLayout;

import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;

/**
 * Created by howzhi on 15/8/13.
 */

@MediumTest
public class DefaultPageActivityTest extends BaseActivityUnitTestCase<DefaultPageActivity> {

    private DefaultPageActivity mActivity;

    public DefaultPageActivityTest() {
        super(DefaultPageActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mLaunchIntent = new Intent(mInstrumentation.getTargetContext(),
                DefaultPageActivity.class);
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

        assertEquals(R.id.nav_tab_news, navLayout.getChildAt(0).getId());
        assertEquals(R.id.nav_tab_find, navLayout.getChildAt(1).getId());
        assertEquals(R.id.nav_tab_friends, navLayout.getChildAt(2).getId());
    }

    @UiThreadTest
    public void testActivityDestroy() {
        mActivity = getActivity();
        mInstrumentation.callActivityOnDestroy(mActivity);
    }
}
