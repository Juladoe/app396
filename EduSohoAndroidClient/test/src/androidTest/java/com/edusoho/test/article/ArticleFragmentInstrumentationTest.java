package com.edusoho.test.article;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.LargeTest;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.test.FragmentTestActivity;

/**
 * Created by howzhi on 15/9/9.
 */
@LargeTest
public class ArticleFragmentInstrumentationTest extends ActivityInstrumentationTestCase2<FragmentTestActivity> {

    public ArticleFragmentInstrumentationTest()
    {
        super(FragmentTestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.putExtra(FragmentPageActivity.FRAGMENT, "ArticleFragment");
        setActivityIntent(intent);
    }

    @LargeTest
    @UiThreadTest
    public void testInit() {
        FragmentTestActivity activity = getActivity();
        assertEquals("trymob.edusoho.cn", activity.app.domain);
        assertEquals("ddd", activity.app.token);
    }

}
