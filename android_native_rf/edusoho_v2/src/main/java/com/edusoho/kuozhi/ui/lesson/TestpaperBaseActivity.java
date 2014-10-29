package com.edusoho.kuozhi.ui.lesson;

import com.edusoho.kuozhi.model.Question.Answer;
import com.edusoho.kuozhi.model.Testpaper.PaperResult;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.Testpaper;
import com.edusoho.kuozhi.ui.course.CourseDetailsTabActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by howzhi on 14-10-10.
 */
public class TestpaperBaseActivity extends CourseDetailsTabActivity {

    protected Testpaper mTestpaper;
    protected PaperResult mTestpaperResult;
    protected ArrayList<Integer> mFavorites;

    protected HashMap<QuestionType, ArrayList<Answer>> answerMap;

    protected HashMap<QuestionType, ArrayList<QuestionTypeSeq>> mQuestions;

    public ArrayList<QuestionTypeSeq> getQuesions(QuestionType type)
    {
        if (mQuestions == null) {
            return null;
        }
        return mQuestions.get(type);
    }

    public PaperResult getPaperResult()
    {
        return mTestpaperResult;
    }

    public ArrayList<Integer> getFavorites()
    {
        return mFavorites;
    }
}
