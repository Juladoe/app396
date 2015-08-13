package com.edusoho.test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * Created by howzhi on 15/8/13.
 */


public class DefaultPageActivityTest extends ActivityInstrumentationTestCase2<DefaultPageActivity> {

    private Instrumentation mInstrumentation;
    private DefaultPageActivity mActivity;

    public DefaultPageActivityTest() {
        super(DefaultPageActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
    }

    @Test
    public void testActivityRun() {
        assertNotNull(mActivity);
        assertThat(mActivity, notNullValue());
    }
}
