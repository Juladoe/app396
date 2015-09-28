package com.edusoho.kuozhi.v3.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JesseHuang on 15/9/16.
 */
public class PushUtil {
    public static class CourseType {
        public static final String TYPE = "course";
        public static final String LESSON_PUBLISH = "lesson.publish";
        public static final String TESTPAPER_REVIEWED = "testpaper.reviewed";
        public static final String COURSE_OPEN = "course.open";
        public static final String COURSE_CLOSE = "course.close";
        public static final String COURSE_ANNOUNCEMENT = "announcement.create";
    }

    public static class LessonType {
        public static final String LIVE = "live";
        public static final String TEXT = "text";
        public static final String VIDEO = "video";
        public static final String AUDIO = "audio";
        public static final String TESTPAPER = "testpaper";
        public static final String PPT = "ppt";
        public static final String DOCUMENT = "document";
        public static final String FLASH = "flash";
    }

    public static class AnnouncementType {
        public static final String COURSE = "course";
        public static final String GLOBAL = "global";
    }

    public static class CourseCode {
        public static final String TESTPAPER_REVIEWED = "试卷批阅完成";
        public static final String LESSON_PUBLISH = "课时更新";
        public static final String COURSE_ANNOUNCEMENT = "课程公告";
    }

    public static class ChatUserType {
        public static final String USER = "user";
        public static final String TEACHER = "teacher";
        public static final String FRIEND = "friend";
    }

    /**
     * custom 中的 typeMsg
     */
    public static class ChatMsgType {
        public static final String TEXT = "text";
        public static final String AUDIO = "audio";
        public static final String IMAGE = "image";
    }

    public static class BulletinType {
        public static final String TYPE = "bulletin";
    }

    public static class ArticleType {
        public static final String TYPE = "news";
        public static final String NEWS_CREATE = "news.create";
    }

    public static class FriendVerified {
        public static final String TYPE = "verified";
    }

    public static class DiscountType {
        public static final String DISCOUNT = "discount";
        public static final String DISCOUNT_FREE = "discount.free";
        public static final String DISCOUNT_DISCOUNT = "discount.discount";
        public static final String DISCOUNT_GLOBAL = "discount.global";
    }

    public static String replaceImgTag(String source) {
        Pattern pattern = Pattern.compile("<img[^>]+>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(source);
        return matcher.replaceAll("[图片]");
    }
}
