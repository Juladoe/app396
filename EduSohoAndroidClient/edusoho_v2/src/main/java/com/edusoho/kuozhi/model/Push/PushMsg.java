package com.edusoho.kuozhi.model.Push;

import android.content.ContextWrapper;
import android.content.Intent;

import com.edusoho.kuozhi.ui.message.MessageLetterListActivity;
import com.edusoho.kuozhi.ui.question.QuestionDetailActivity;
import com.edusoho.kuozhi.util.Const;

/**
 * Created by JesseHuang on 14/12/18.
 * 用于处理推送的信息
 */
public class PushMsg {
    private String mTypeId;

    /**
     * 发送私信对方的昵称，或者回答title名字
     */
    private String mObjectName;

    /**
     * 发送私信对方的Id，或者回答titleId
     */
    private String mObjectId;

    private String mOtherId;

    private String notificationTitle;

    private String notificationContent;

    private Intent intent;

    public String getTypeId() {
        return mTypeId;
    }

    public void setTypeId(String typeId) {
        this.mTypeId = typeId;
    }

    public String getObjectName() {
        return mObjectName;
    }

    public void setObjectName(String objectName) {
        this.mObjectName = objectName;
    }

    public String getObjectId() {
        return mObjectId;
    }

    public void setObjectId(String objectId) {
        this.mObjectId = objectId;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public String getOtherId() {
        return mOtherId;
    }

    public Intent getIntent() {
        return intent;
    }

    public PushMsg(String[] msg, ContextWrapper activity) {
        mTypeId = msg[0];
        mObjectId = msg[1];
        mObjectName = msg[2];
        mOtherId = msg[3];

        switch (Integer.parseInt(mTypeId)) {
            //私信处理
            case 0:
                notificationTitle = "一条私信";
                notificationContent = mObjectName + "给你回复了一条私信";
                intent = new Intent(activity, MessageLetterListActivity.class);
                intent.putExtra(MessageLetterListActivity.CONVERSATION_FROM_ID, Integer.parseInt(mObjectId));
                intent.putExtra(MessageLetterListActivity.CONVERSATION_FROM_NAME, mObjectName);
                intent.putExtra(MessageLetterListActivity.CONVERSATION_ID, Integer.parseInt(mOtherId));
                break;
            case 1:
                notificationTitle = "一条回答";
                notificationContent = "你的提问《" + mObjectName + "》收到了1条新评论";
                intent = new Intent(activity, QuestionDetailActivity.class);
                intent.putExtra(Const.COURSE_ID, Integer.parseInt(mObjectId));
                intent.putExtra(Const.THREAD_ID, Integer.parseInt(mOtherId));

                break;
        }
    }
}
