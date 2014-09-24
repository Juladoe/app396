package com.edusoho.kuozhi.ui.fragment.testpaper;

import android.os.Bundle;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-24.
 */
public class FillFragment extends QuestionTypeBaseFragment{

    private QuestionType type = QuestionType.fill;

    @Override
    public String getTitle() {
        return "填空题";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.choice_fragment_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        refreshViewData();
    }

    @Override
    protected void refreshViewData() {
        ArrayList<QuestionTypeSeq> questionTypeSeqs = getQuestion(type);
        if (questionTypeSeqs == null) {
            return;
        }
        mQuestionType.setText(type.title());
        mQuestionNumber.setText(String.format("%d/%d", mCurrentIndex, questionTypeSeqs.size()));
    }
}
