package com.edusoho.kuozhi.v3.service.push;

import android.os.Bundle;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.push.CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.service.EdusohoMainService;
import com.edusoho.kuozhi.v3.ui.BulletinActivity;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.NewsCourseActivity;
import com.edusoho.kuozhi.v3.ui.ServiceProviderActivity;
import com.edusoho.kuozhi.v3.ui.fragment.FriendFragment;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.ui.fragment.article.ArticleFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.Gson;

/**
 * Created by JesseHuang on 15/9/11.
 */
public class Pusher {
    private Bundle mBundle;
    private WrapperXGPushTextMessage mWrapperMessage;
    private V2CustomContent mV2CustomContent;

    public Pusher(Bundle bundle, WrapperXGPushTextMessage wrapperMessage) {
        mBundle = bundle;
        mWrapperMessage = wrapperMessage;
    }

    public V2CustomContent getV2CustomContent() {
        return mV2CustomContent;
    }

    public void setV2CustomContent(V2CustomContent mV2CustomContent) {
        this.mV2CustomContent = mV2CustomContent;
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

    public void pushVerified() {
        EdusohoMainService.getService().setNewNotification();
        EdusohoApp.app.sendMsgToTarget(Const.NEW_FANS, mBundle, FriendFragment.class);
    }

    public void pushLessonPublish() {
        boolean isForeground = EdusohoApp.app.isForeground(NewsCourseActivity.class.getName());
        if (isForeground) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, NewsCourseActivity.class);
        }
        EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_COURSE_MSG, mWrapperMessage);
    }

    public void pushCourseAnnouncement() {
        boolean isForeground = EdusohoApp.app.isForeground(NewsCourseActivity.class.getName());
        if (isForeground) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, NewsCourseActivity.class);
        }
        EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_COURSE_MSG, mWrapperMessage);
    }

    public void pushGlobalAnnouncement() {
        boolean isForeground = EdusohoApp.app.isForeground(BulletinActivity.class.getName());
        if (isForeground) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_BULLETIT_MSG, mBundle, BulletinActivity.class);
        }
        EdusohoApp.app.sendMsgToTarget(Const.ADD_BULLETIT_MSG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_BULLETIT_MSG, mWrapperMessage);
    }

    public void pushTestpaperReviewed() {
        boolean isForeground = EdusohoApp.app.isForeground(NewsCourseActivity.class.getName());
        if (isForeground) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, NewsCourseActivity.class);
        }
        EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_COURSE_MSG, mWrapperMessage);
    }


    public void pushArticleCreate() {
        boolean isForeground = EdusohoApp.app.isForeground(ServiceProviderActivity.class.getName());
        if (isForeground && ServiceProviderActivity.ARTICLE.equals(ServiceProviderActivity.SERVICE_NAME)) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_ARTICLE_CREATE_MAG, mBundle, ArticleFragment.class);
        }

        EdusohoApp.app.sendMsgToTarget(Const.ADD_ARTICLE_CREATE_MAG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_ARTICLE_CREATE_MAG, mWrapperMessage);
    }

    public void convertWrapperMessage2V2() {
        CustomContent v1CustomContent = new CustomContent();
        v1CustomContent.setId(mV2CustomContent.getMsgId());
        v1CustomContent.setTypeMsg(mV2CustomContent.getBody().getType());
        v1CustomContent.setTypeBusiness("friend");
        v1CustomContent.setNickname(mV2CustomContent.getFrom().getNickname());
        v1CustomContent.setImgUrl(mV2CustomContent.getFrom().getImage());
        v1CustomContent.setFromId(mV2CustomContent.getFrom().getId());
        v1CustomContent.setCreatedTime(mV2CustomContent.getCreatedTime());
        Gson gson = new Gson();
        mWrapperMessage.setCustomContentJson(gson.toJson(v1CustomContent));
    }

    public void convertArticleMessageV2() {
        CustomContent v1CustomContent = new CustomContent();
        v1CustomContent.setId(mV2CustomContent.getMsgId());
        v1CustomContent.setTypeMsg(mV2CustomContent.getBody().getType());
        v1CustomContent.setTypeBusiness(mV2CustomContent.getBody().getType());
        v1CustomContent.setNickname(EdusohoApp.app.domain);
        v1CustomContent.setImgUrl(mV2CustomContent.getFrom().getImage());
        v1CustomContent.setFromId(mV2CustomContent.getFrom().getId());
        v1CustomContent.setCreatedTime(mV2CustomContent.getCreatedTime());
        Gson gson = new Gson();
        mWrapperMessage.setContent(gson.toJson(mV2CustomContent.getBody()));
        mWrapperMessage.setCustomContentJson(gson.toJson(v1CustomContent));
    }
}
