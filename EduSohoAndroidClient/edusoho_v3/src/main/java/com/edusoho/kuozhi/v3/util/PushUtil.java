package com.edusoho.kuozhi.v3.util;

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
        public static final String COURSE_ANNOUNCEMENT = "course.announcement";
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

    public static class CourseCode {
        public static final String TESTPAPER_REVIEWED = "试卷批阅完成";
        public static final String LESSON_PUBLISH = "课时更新";
        public static final String COURSE_ANNOUNCEMENT = "最新公告";
    }

    public static class ChatUserRole {
        public static final String TEACHER = "teacher";
        public static final String FRIEND = "friend";
    }

    public static class BulletinType {
        public static final String TYPE = "bulletin";
    }
}
