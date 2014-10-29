package com.edusoho.kuozhi.model.Note;

import java.util.ArrayList;

/**
 * Created by onewoman on 14-10-9.
 */
public class LessonList {
    public String courseNum;
    public String courseTitle;
    public String courseContent;
    public Integer lessonId;

    public LessonList(String courseNum, String courseTitle, String courseContent, Integer lessonId) {
        this.courseNum = courseNum;
        this.courseTitle = courseTitle;
        this.courseContent = courseContent;
        this.lessonId = lessonId;
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
