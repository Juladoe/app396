package com.edusoho.test;

import android.test.UiThreadTest;

import com.edusoho.kuozhi.v3.ui.ChatActivity;

/**
 * Created by JesseHuang on 15/8/18.
 */
public class ChatActivityTest extends BaseActivityUnitTestCase<ChatActivity> {
    private ChatActivity mChatActivity;

    public ChatActivityTest() {
        super(ChatActivity.class);
    }

    @UiThreadTest
    public void testActivity() {
        mChatActivity = getActivity();
        assertNotNull(mChatActivity);
    }


}
