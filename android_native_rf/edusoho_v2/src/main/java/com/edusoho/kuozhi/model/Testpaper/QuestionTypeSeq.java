package com.edusoho.kuozhi.model.Testpaper;

/**
 * Created by howzhi on 14-9-23.
 */
public class QuestionTypeSeq {

    public int id;
    public int testId;
    public int seq;
    public int questionId;
    public QuestionType questionType;
    public int parentId;
    public double score;
    public double missScore;

    public Question question;
}
