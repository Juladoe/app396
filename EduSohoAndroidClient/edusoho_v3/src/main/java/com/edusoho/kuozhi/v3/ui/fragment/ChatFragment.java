package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;

/**
 * Created by JesseHuang on 15/6/2.
 */
public class ChatFragment extends BaseFragment {

    public static final int COURSE_CHAT = 0x01;
    public static final String COURSE_ID = "course_id";
    private ChatHandler mHandler;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.activity_chat);
        mActivity.setTitle("用户名");
        mHandler = new ChatHandler();
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = mActivity.getIntent();
        if (intent != null) {
            String courseId = intent.getStringExtra(COURSE_ID);
            CommonUtil.longToast(mActivity, courseId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
    }

    @Override
    public void invoke(WidgetMessage message) {
        switch (message.type.code) {
            case Const.OPEN_COURSE_CHAT:
                Message msg = mHandler.obtainMessage();
                msg.what = COURSE_CHAT;
                msg.obj = message.data;
                mHandler.sendMessage(msg);
                break;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(Const.OPEN_COURSE_CHAT, source)
        };
        return messageTypes;
    }

    private class ChatHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COURSE_CHAT:
                    break;
            }
        }
    }

}
