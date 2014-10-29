package com.edusoho.kuozhi.model.Note;

/**
 * Created by onewoman on 14-10-11.
 */
public class HttpDatas {
    public int id;
    public int userId;
    public int courseId;
    public int lessonId;
    public String content;
    public int length;
    public int status;
    public String createdTime;
    public String updatedTime;
    public String title;
    public String largePicture;
    public String lessonName;
    public String number;

    @Override
    public String toString() {
        return "HttpDatas{" +
                "id=" + id +
                ", userId=" + userId +
                ", courseId=" + courseId +
                ", lessonId=" + lessonId +
                ", content='" + content + '\'' +
                ", length=" + length +
                ", status=" + status +
                ", createdTime='" + createdTime + '\'' +
                ", updatedTime='" + updatedTime + '\'' +
                ", title='" + title + '\'' +
                ", largePicture='" + largePicture + '\'' +
                ", lessonName='" + lessonName + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
