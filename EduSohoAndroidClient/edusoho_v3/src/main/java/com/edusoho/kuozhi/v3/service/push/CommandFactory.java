package com.edusoho.kuozhi.v3.service.push;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.util.PushUtil;

/**
 * Created by JesseHuang on 15/9/11.
 */
public class CommandFactory {

    /**
     * 老格式处理
     */
    public static PushCommand Make(String command, Pusher pusher) {
        PushCommand pushCommand = null;
        switch (command) {
            case "friend":
            case "teacher":
                pushCommand = new PushMsgCommand(pusher);
                break;
            case "bulletin":
                pushCommand = new PushGlobalAnnouncementCommand(pusher);
                break;
            case "verified":
                pushCommand = new PushVerifiedCommand(pusher);
                break;
        }
        return pushCommand;
    }

    /**
     * 3.1版本格式处理
     */
    public static PushCommand V2Make(Pusher pusher) {
        PushCommand pushCommand = null;
        V2CustomContent v2CustomContent = pusher.getV2CustomContent();
        String bodyType = v2CustomContent.getTo().getType();
        switch (bodyType) {
            case PushUtil.CourseType.LESSON_PUBLISH:
                pushCommand = new PushLessonPublishCommand(pusher);
                break;
            case PushUtil.CourseType.TESTPAPER_REVIEWED:
                pushCommand = new PushTestpaperReviewedCommand(pusher);
                break;
            case PushUtil.CourseType.COURSE_ANNOUNCEMENT:
                if (PushUtil.AnnouncementType.COURSE.equals(v2CustomContent.getFrom().getType())) {
                    pushCommand = new PushCourseAnnouncementCommand(pusher);
                } else {
                    pushCommand = new PushGlobalAnnouncementCommand(pusher);
                }
                break;
            case PushUtil.ArticleType.NEWS_CREATE:
                pushCommand = new PushArticleCreateCommand(pusher);
                break;
            case PushUtil.FriendVerified.TYPE:
            case PushUtil.ChatMsgType.AUDIO:
            case PushUtil.ChatMsgType.IMAGE:
            case PushUtil.ChatMsgType.TEXT:
            case PushUtil.ChatMsgType.MULTI:
                if (PushUtil.ChatUserType.CLASSROOM.equals(v2CustomContent.getTo().getType())) {
                    if (v2CustomContent.getFrom().getId() != EdusohoApp.app.loginUser.id) {
                        pushCommand = new PushClassRoomMsgCommand(pusher);
                    }
                } else if (PushUtil.ChatUserType.FRIEND.equals(bodyType) ||
                        PushUtil.ChatUserType.TEACHER.equals(bodyType)) {
                    pusher.convertWrapperMessage2V2();
                    pushCommand = new PushMsgCommand(pusher);
                }
                break;
            case PushUtil.DiscountType.DISCOUNT_GLOBAL:
            case PushUtil.DiscountType.DISCOUNT_DISCOUNT:
            case PushUtil.DiscountType.DISCOUNT_FREE:
                pushCommand = new PushDiscountPassCommand(pusher);
                break;
        }
        return pushCommand;
    }
}
