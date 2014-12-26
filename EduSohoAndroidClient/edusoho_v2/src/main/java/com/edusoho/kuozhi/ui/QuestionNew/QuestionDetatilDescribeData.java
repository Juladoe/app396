package com.edusoho.kuozhi.ui.QuestionNew;

import android.graphics.drawable.Drawable;

/**
 * Created by onewoman on 2014/12/18.
 */
public class QuestionDetatilDescribeData {
    public String questionDetailTitle;
    public Drawable questionUserHeadImage;
    public String questionDetailUesrName;
    public String questionQuizTime;
    public String questionDescribe;
    public String questionDetailCourseTitle;
    public int questionDetailCheckCount;
    public int questionDetailAnswerCount;

    public QuestionDetatilDescribeData(String questionDetailTitle, Drawable questionUserHeadImage,
                                       String questionDetailUesrName, String questionQuizTime,
                                       String questionDescribe, String questionDetailCourseTitle,
                                       int questionDetailCheckCount, int questionDetailAnswerCount) {
        this.questionDetailTitle = questionDetailTitle;
        this.questionUserHeadImage = questionUserHeadImage;
        this.questionDetailUesrName = questionDetailUesrName;
        this.questionQuizTime = questionQuizTime;
        this.questionDescribe = questionDescribe;
        this.questionDetailCourseTitle = questionDetailCourseTitle;
        this.questionDetailCheckCount = questionDetailCheckCount;
        this.questionDetailAnswerCount = questionDetailAnswerCount;
    }
}
