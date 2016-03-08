package com.edusoho.tcmmook.v3.service.push;

import com.edusoho.kuozhi.v3.service.push.CommandFactory;
import com.edusoho.kuozhi.v3.service.push.PushArticleCreateCommand;
import com.edusoho.kuozhi.v3.service.push.PushCourseAnnouncementCommand;
import com.edusoho.kuozhi.v3.service.push.PushGlobalAnnouncementCommand;
import com.edusoho.kuozhi.v3.service.push.PushTestpaperReviewedCommand;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.service.push.Pusher;
import com.edusoho.kuozhi.v3.service.push.PushCommand;
import com.edusoho.kuozhi.v3.service.push.PushMsgCommand;
import com.edusoho.kuozhi.v3.service.push.PushDiscountPassCommand;

/**
 * Created by JesseHuang on 15/9/11.
 */
public class CustomCommandFactory extends CommandFactory {



    /**
     * 3.1版本格式处理
     */
    public static PushCommand V2Make(Pusher pusher) {
        PushCommand pushCommand = null;
        switch (pusher.getV2CustomContent().getBody().getType()) {
            case PushUtil.CourseType.LESSON_PUBLISH:
                //pushCommand = new PushLessonPublishCommand(pusher);
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
            case PushUtil.ArticleType.NEWS_CREATE:
                pushCommand = new PushArticleCreateCommand(pusher);
                break;
            case PushUtil.FriendVerified.TYPE:
            case PushUtil.ChatMsgType.AUDIO:
            case PushUtil.ChatMsgType.IMAGE:
            case PushUtil.ChatMsgType.TEXT:
            case PushUtil.ChatMsgType.MULTI:
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
}
