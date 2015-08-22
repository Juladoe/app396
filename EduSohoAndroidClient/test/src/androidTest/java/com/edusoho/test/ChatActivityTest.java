package com.edusoho.test;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.UiThreadTest;
import android.view.ContextThemeWrapper;

import com.edusoho.kuozhi.v3.ui.ChatActivity;

/**
 * Created by JesseHuang on 15/8/18.
 */
public class ChatActivityTest extends ActivityUnitTestCase<ChatActivity> {
    private ChatActivity mChatActivity;
    protected Instrumentation mInstrumentation;
    protected Intent mLaunchIntent;

    public ChatActivityTest() {
        super(ChatActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mInstrumentation = getInstrumentation();
        ContextThemeWrapper context = new ContextThemeWrapper(
                mInstrumentation.getTargetContext(), R.style.Theme_AppCompat);
        setActivityContext(context);

        TestEduSohoApp app = (TestEduSohoApp) mInstrumentation.newApplication(
                getClass().getClassLoader(), TestEduSohoApp.class.getName(), context);
        TestUtils.initApplication(app, mInstrumentation.getTargetContext());
        mInstrumentation.callApplicationOnCreate(app);
        setApplication(app);
        mLaunchIntent = new Intent(mInstrumentation.getTargetContext(),
                ChatActivity.class);
    }

    @Override
    public ChatActivity getActivity() {
        mChatActivity = super.getActivity();
        if (mChatActivity == null) {
            mChatActivity = startActivity(mLaunchIntent, null, null);
        }

        return mChatActivity;
    }

    @UiThreadTest
    public void testActivity() {
        mChatActivity = getActivity();
        assertNotNull(mChatActivity);
    }


}
