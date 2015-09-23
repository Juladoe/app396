package com.edusoho.kuozhi.v3.service.push;

import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
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
        switch (pusher.getV2CustomContent().getBody().getType()) {
            case PushUtil.CourseType.LESSON_PUBLISH:
                pushCommand = new PushLessonPublishCommand(pusher);
                break;
            case PushUtil.CourseType.TESTPAPER_REVIEWED:
                pushCommand = new PushTestpaperReviewedCommand(pusher);
                break;
            case PushUtil.CourseType.COURSE_ANNOUNCEMENT:
                if (PushUtil.AnnouncementType.COURSE.equals(pusher.getV2CustomContent().getFrom().getType())) {
                    pushCommand = new PushCourseAnnouncementCommand(pusher);
                } else {
                    pushCommand = new PushGlobalAnnouncementCommand(pusher);
                }
                break;
            case PushUtil.FriendVerified.TYPE:
                break;
            case PushUtil.ChatMsgType.AUDIO:
            case PushUtil.ChatMsgType.IMAGE:
            case PushUtil.ChatMsgType.TEXT:
                pusher.convertWrapperMessage2V2();
                pushCommand = new PushMsgCommand(pusher);
                break;
            case PushUtil.DiscountType.DISCOUNT_GLOBAL:
            case PushUtil.DiscountType.DISCOUNT_DISCOUNT:
            case PushUtil.DiscountType.DISCOUNT_FREE:
                pushCommand = new PushDiscountPassCommand(pusher);
                break;
        }
        return pushCommand;
    }

    public static String MakeNotificationTitle(WrapperXGPushTextMessage xgMessage) {
        V2CustomContent v2CustomContent = xgMessage.getV2CustomContent();
        String notifyTitle = "";
        if (PushUtil.CourseType.TYPE.equals(v2CustomContent.getFrom().getType())) {

        } else {
            switch (v2CustomContent.getBody().getType()) {
                case PushUtil.DiscountType.DISCOUNT_GLOBAL:
                    notifyTitle = String.format("[%s]%s", PushUtil.DiscountCode.DISCOUNT_GLOBAL, xgMessage.getContent());
                    break;
                case PushUtil.DiscountType.DISCOUNT_DISCOUNT:
                    notifyTitle = String.format("[%s]%s", PushUtil.DiscountCode.DISCOUNT_DISCOUNT, xgMessage.getContent());
                    break;
                case PushUtil.DiscountType.DISCOUNT_FREE:
                    notifyTitle = String.format("[%s]%s", PushUtil.DiscountCode.DISCOUNT_FREE, xgMessage.getContent());
                    break;
            }
        }
        return notifyTitle;
    }
}
