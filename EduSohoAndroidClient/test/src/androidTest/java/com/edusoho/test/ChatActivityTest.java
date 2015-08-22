package com.edusoho.test;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.UiThreadTest;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.view.EduSohoIconView;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;

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

    @UiThreadTest
    public void testInitUI() {
        mChatActivity = getActivity();
        ListView listView = (ListView) mChatActivity.findViewById(R.id.lv_messages);
        assertNotNull(listView);

        PtrClassicFrameLayout ptrClassicFrameLayout = (PtrClassicFrameLayout) mChatActivity.findViewById(R.id.rotate_header_list_view_frame);
        assertNotNull(ptrClassicFrameLayout);

        View viewSpeakContainer = mChatActivity.findViewById(R.id.recording_container);
        assertNotNull(viewSpeakContainer);
        assertEquals(View.GONE, viewSpeakContainer.getVisibility());
        ImageView ivRecordImage = (ImageView) mChatActivity.findViewById(R.id.iv_voice_volume);
        assertNotNull(ivRecordImage);
        TextView tvSpeakHint = (TextView) mChatActivity.findViewById(R.id.tv_speak_hint);
        assertNotNull(tvSpeakHint);

        View viewChatDialog = mChatActivity.findViewById(R.id.ll_chat_dialog);
        assertNotNull(viewChatDialog);
        EduSohoIconView btnVoice = (EduSohoIconView) mChatActivity.findViewById(R.id.btn_voice);
        assertNotNull(btnVoice);
        assertEquals(mChatActivity.getResources().getString(R.string.font_chat_voice), btnVoice.getText());
        EduSohoIconView btnKeyBoard = (EduSohoIconView) mChatActivity.findViewById(R.id.btn_set_mode_keyboard);
        assertNotNull(btnKeyBoard);
        View viewPressToSpeak = mChatActivity.findViewById(R.id.rl_btn_press_to_speak);
        assertNotNull(viewPressToSpeak);
        assertEquals(View.GONE, viewPressToSpeak.getVisibility());
        TextView tvSpeak = (TextView) mChatActivity.findViewById(R.id.tv_speak);
        assertNotNull(tvSpeak);
        assertEquals(mChatActivity.getResources().getString(R.string.hand_press_and_speak), tvSpeak.getText());
        View viewMsgInput = mChatActivity.findViewById(R.id.rl_msg_input);
        assertNotNull(viewMsgInput);
        assertEquals(View.VISIBLE, viewMsgInput.getVisibility());
        EduSohoIconView ivAddMedia = (EduSohoIconView) mChatActivity.findViewById(R.id.iv_show_media_layout);
        assertNotNull(ivAddMedia);
        assertEquals(mChatActivity.getResources().getString(R.string.font_chat_add_media), ivAddMedia.getText());
        Button btnSend = (Button) mChatActivity.findViewById(R.id.tv_send);
        assertNotNull(btnSend);
        assertEquals(View.GONE, btnSend.getVisibility());
    }

}
