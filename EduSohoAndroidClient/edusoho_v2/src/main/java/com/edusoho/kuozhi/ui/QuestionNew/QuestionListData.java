package com.edusoho.kuozhi.ui.QuestionNew;

/**
 * Created by onewoman on 2014/12/22.
 */
public class QuestionListData {
    public String questiongTitle;
    public String questionAnswerCount;
    public String questionAnswerContent;
    public String questionCourseTitle;
    public String questionAnswerTime;

    public QuestionListData(String questiongTitle, String questionAnswerCount, String questionAnswerContent, String questionCourseTitle, String questionAnswerTime) {
        this.questionCourseTitle = questionCourseTitle;
        this.questiongTitle = questiongTitle;
        this.questionAnswerCount = questionAnswerCount;
        this.questionAnswerContent = questionAnswerContent;
        this.questionAnswerTime = questionAnswerTime;
    }
}
