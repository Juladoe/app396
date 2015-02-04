package com.edusoho.kuozhi.model;

import com.edusoho.kuozhi.model.Lesson.UploadFile;

/**
 * Created by howzhi on 14-5-24.
 */
public class LessonItem<T> {
    public static final int FREE = 1;

    public int id;
    public int courseId;
    public int chapterId;
    public int number;
    public int seq;
    public int free;
    public String status;
    public String title;
    public String summary;
    public String tag;
    public String type;
    public T content;
    private int giveCredit;
    private int requireCredit;
    public int mediaId;
    public String mediaSource;
    public String mediaName;
    public String mediaUri;
    public String headUrl;
    public String length;
    public int materialNum;
    public int quizNum;
    public int learnedNum;
    public int viewedNum;
    public int userId;
    public String createdTime;
    public String itemType;
    public String startTime;
    public String endTime;

    public UploadFile uploadFile;

    public static enum ItemType{
        LESSON, CHAPTER, UNIT, EMPTY;

        public static ItemType cover(String name)
        {
            ItemType type = EMPTY;
            try {
                type = valueOf(name.toUpperCase());
            } catch (Exception e) {
                return EMPTY;
            }
            return type;
        }
    }

    public static enum MediaSourceType{
        YOUKU, SELF, TUDOU, EMPTY, QQVIDEO, FALLBACK, NETEASEOPENCOURSE;

        public static MediaSourceType cover(String name)
        {
            MediaSourceType type = EMPTY;
            try {
                type = valueOf(name.toUpperCase());
            } catch (Exception e) {
                return EMPTY;
            }
            return type;
        }
    }
}
