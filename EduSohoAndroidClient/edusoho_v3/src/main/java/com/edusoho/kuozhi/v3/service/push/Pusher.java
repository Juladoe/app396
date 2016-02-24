package com.edusoho.kuozhi.v3.service.push;

import android.os.Bundle;
import android.text.Html;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.service.EdusohoMainService;
import com.edusoho.kuozhi.v3.ui.BulletinActivity;
import com.edusoho.kuozhi.v3.ui.ChatActivity;
import com.edusoho.kuozhi.v3.ui.ClassroomDiscussActivity;
import com.edusoho.kuozhi.v3.ui.NewsCourseActivity;
import com.edusoho.kuozhi.v3.ui.ServiceProviderActivity;
import com.edusoho.kuozhi.v3.ui.ThreadDiscussActivity;
import com.edusoho.kuozhi.v3.ui.fragment.CourseStudyFragment;
import com.edusoho.kuozhi.v3.ui.fragment.FriendFragment;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.ui.fragment.article.ArticleFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;

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
        mBundle.putInt(Const.ADD_CHAT_MSG_DESTINATION, NewsFragment.HANDLE_RECEIVE_CHAT_MSG);
        boolean isForeground = EdusohoApp.app.isForeground(ChatActivity.class.getName());
        if (isForeground) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_MSG, mBundle, ChatActivity.class);
        }
        EdusohoApp.app.sendMsgToTarget(Const.ADD_MSG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_MSG, mWrapperMessage);
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
        mWrapperMessage.setContent(Html.fromHtml(PushUtil.replaceImgTag(mWrapperMessage.getContent())).toString().trim());
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
            EdusohoApp.app.sendMsgToTarget(Const.ADD_BULLETIN_MSG, mBundle, BulletinActivity.class);
        }
        EdusohoApp.app.sendMsgToTarget(Const.ADD_BULLETIN_MSG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_BULLETIN_MSG, mWrapperMessage);
    }

    public void pushTestpaperReviewed() {
        boolean isForeground = EdusohoApp.app.isForeground(NewsCourseActivity.class.getName());
        if (isForeground) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, CourseStudyFragment.class);
        }
        EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_COURSE_MSG, mWrapperMessage);
    }

    public void pushHomeworkReviewed() {
        boolean isForeground = EdusohoApp.app.isForeground(NewsCourseActivity.class.getName());
        if (isForeground) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, CourseStudyFragment.class);
        }
        EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_COURSE_MSG, mWrapperMessage);
    }

    public void pushQuestionAnswered() {
//        boolean isForeground = EdusohoApp.app.isForeground(NewsCourseActivity.class.getName());
//        if (isForeground) {
//            mWrapperMessage.isForeground = true;
//            EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, CourseStudyFragment.class);
//        }
//        EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, NewsFragment.class);
//        EdusohoMainService.getService().sendMessage(Const.ADD_COURSE_MSG, mWrapperMessage);

        if (EdusohoApp.app.isForeground(NewsCourseActivity.class.getName())) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, CourseStudyFragment.class);
            EdusohoMainService.getService().sendMessage(Const.ADD_COURSE_MSG, mWrapperMessage);
        } else if (EdusohoApp.app.isForeground(ThreadDiscussActivity.class.getName())) {
            mWrapperMessage.isForeground = true;
            //EdusohoApp.app.sendMsgToTarget(Const.ADD_THREAD_POST, mBundle, ThreadDiscussActivity.class);
            EdusohoMainService.getService().sendMessage(Const.QUESTION_ANSWERD, mWrapperMessage);
        } else {
            EdusohoMainService.getService().sendMessage(Const.QUESTION_ANSWERD, mWrapperMessage);
        }
        EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, NewsFragment.class);
    }

    public void pushQuestionCreated() {
        EdusohoMainService.getService().sendMessage(Const.QUESTION_CREATED, mWrapperMessage);
    }

    public void pushLessonFinished() {
        boolean isForeground = EdusohoApp.app.isForeground(NewsCourseActivity.class.getName());
        if (isForeground) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, CourseStudyFragment.class);
        }
        EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_COURSE_MSG, mWrapperMessage);
    }

    public void pushLessonStart() {
        boolean isForeground = EdusohoApp.app.isForeground(NewsCourseActivity.class.getName());
        if (isForeground) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, CourseStudyFragment.class);
        }
        EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_MSG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_COURSE_MSG, mWrapperMessage);
    }

    public void pushDiscountPass() {
        EdusohoMainService.getService().sendMessage(Const.ADD_DISCOUNT_PASS, mWrapperMessage);
    }

    public void pushLiveLessonStartNotify() {
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
        if (isForeground && ServiceProviderActivity.isRunWithFragmentByType(ServiceProviderActivity.ARTICLE)) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_ARTICLE_CREATE_MAG, mBundle, ArticleFragment.class);
        }

        EdusohoApp.app.sendMsgToTarget(Const.ADD_ARTICLE_CREATE_MAG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_ARTICLE_CREATE_MAG, mWrapperMessage);
    }

    public void pushClassroomMsg() {
        mBundle.putInt(Const.ADD_DISCUSS_MSG_DESTINATION, NewsFragment.HANDLE_RECEIVE_CLASSROOM_DISCUSS_MSG);
        boolean isForeground = EdusohoApp.app.isForeground(ClassroomDiscussActivity.class.getName());
        if (isForeground) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_CLASSROOM_MSG, mBundle, ClassroomDiscussActivity.class);
        }
        EdusohoApp.app.sendMsgToTarget(Const.ADD_CLASSROOM_MSG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_CLASSROOM_MSG, mWrapperMessage);
    }


    public void pushCourseDiscussMsg() {
        mBundle.putInt(Const.ADD_DISCUSS_MSG_DESTINATION, NewsFragment.HANDLE_RECEIVE_COURSE_DISCUSS_MSG);
        boolean isForeground = EdusohoApp.app.isForeground(NewsCourseActivity.class.getName());
        if (isForeground) {
            mWrapperMessage.isForeground = true;
            EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_DISCUSS_MSG, mBundle, NewsCourseActivity.class);
        }
        EdusohoApp.app.sendMsgToTarget(Const.ADD_COURSE_DISCUSS_MSG, mBundle, NewsFragment.class);
        EdusohoMainService.getService().sendMessage(Const.ADD_COURSE_DISCUSS_MSG, mWrapperMessage);
    }
}
