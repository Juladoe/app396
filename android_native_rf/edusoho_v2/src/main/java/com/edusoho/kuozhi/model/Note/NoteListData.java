package com.edusoho.kuozhi.model.Note;

import java.util.ArrayList;

/**
 * Created by onewoman on 14-10-9.
 */
public class NoteListData {
    public ArrayList<String> courseNum;
    public ArrayList<String> courseTitle;
    public ArrayList<String> courseContent;
    public ArrayList<Integer> lessonId;

    public NoteListData(ArrayList<String> courseNum, ArrayList<String> courseTitle, ArrayList<String> courseContent, ArrayList<Integer> lessonId) {
        this.courseNum = courseNum;
        this.courseContent = courseContent;
        this.courseTitle = courseTitle;
        this.lessonId = lessonId;
    }
}
