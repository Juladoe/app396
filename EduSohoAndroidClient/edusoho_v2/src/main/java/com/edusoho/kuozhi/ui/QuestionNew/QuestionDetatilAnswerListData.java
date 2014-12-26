package com.edusoho.kuozhi.ui.QuestionNew;

import android.graphics.drawable.Drawable;

/**
 * Created by onewoman on 2014/12/19.
 */
public class QuestionDetatilAnswerListData {
    public Drawable questionDetatilListHeadImage;
    public String questionDetatilListUserName;
    public String questionDetatilListTime;
    public String questionDetatilListAnswer;

    public QuestionDetatilAnswerListData(Drawable questionDetatilListHeadImage, String questionDetatilListUserName, String questionDetatilListTime, String questionDetatilListAnswer) {
        this.questionDetatilListHeadImage = questionDetatilListHeadImage;
        this.questionDetatilListUserName = questionDetatilListUserName;
        this.questionDetatilListTime = questionDetatilListTime;
        this.questionDetatilListAnswer = questionDetatilListAnswer;
    }
}
