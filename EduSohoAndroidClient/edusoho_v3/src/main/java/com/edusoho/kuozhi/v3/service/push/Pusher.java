package com.edusoho.kuozhi.v3.service.push;

import android.os.Bundle;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.service.EdusohoMainService;
import com.edusoho.kuozhi.v3.ui.BulletinActivity;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.fragment.FriendFragment;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.util.Const;

/**
 * Created by JesseHuang on 15/9/11.
 */
public class Pusher {
    private Bundle mBundle;
    private WrapperXGPushTextMessage mWrapperMessage;

    public Pusher(Bundle bundle, WrapperXGPushTextMessage wrapperMessage) {
        mBundle = bundle;
        mWrapperMessage = wrapperMessage;
    }

    public void pushMsg() {
        //普通消息
        mBundle.putInt(Const.ADD_CHAT_MSG_TYPE, NewsFragment.HANDLE_RECEIVE_MSG);
        boolean isForeground = EdusohoApp.app.isForeground(ChatActivity.class.getName());
        if (isForeground) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_CHAT_MSG, mBundle, ChatActivity.class);
        }
        EdusohoApp.app.sendMsgToTarget(Const.ADD_CHAT_MSG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_CHAT_MSG, mWrapperMessage);
    }

    public void pushBulletin() {
        //公告
        boolean isForeground = EdusohoApp.app.isForeground(BulletinActivity.class.getName());
        if (isForeground) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_BULLETIT_MSG, mBundle, BulletinActivity.class);
        }
        EdusohoApp.app.sendMsgToTarget(Const.ADD_BULLETIT_MSG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_BULLETIT_MSG, mWrapperMessage);
    }

    public void pushVerifield() {
        //验证
        EdusohoMainService.getService().setNewNotification();
        EdusohoApp.app.sendMsgToTarget(Const.NEW_FANS, mBundle, FriendFragment.class);
    }
}
