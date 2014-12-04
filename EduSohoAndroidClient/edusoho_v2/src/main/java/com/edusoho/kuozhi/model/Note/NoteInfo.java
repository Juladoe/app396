package com.edusoho.kuozhi.model.Note;

/**
 * Created by howzhi on 14-10-31.
 */
public class NoteInfo {

    public int coursesId;
    public String courseTitle;
    public String noteLastUpdateTime;

    public int lessonId;
    public String lessonTitle;
    public String learnStatus;

    public String content;
    public String createdTime;
    public int noteNum;
    public String largePicture;



    @Override
    public String toString() {
        return "NoteInfo{" +
                "coursesId=" + coursesId +
                ", courseTitle='" + courseTitle + '\'' +
                ", noteLastUpdateTime=" + noteLastUpdateTime +
                ", noteNum=" + noteNum +
                ", largePicture='" + largePicture + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
