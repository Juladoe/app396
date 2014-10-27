package com.edusoho.kuozhi.ui.note;

/**
 * Created by onewoman on 14-10-9.
 */
public class LessonList {
    public String courseNum;
    public String courseTitle;
    public String courseContent;

    public LessonList(String courseNum, String courseTitle, String courseContent) {
        this.courseNum = courseNum;
        this.courseTitle = courseTitle;
        this.courseContent = courseContent;
    }

    @Override
    public String toString() {
        return "LessonList{" +
                "course='" + courseNum + '\'' +
                ", coursetitle='" + courseTitle + '\'' +
                ", coursecontent='" + courseContent + '\'' +
                '}';
    }
}
