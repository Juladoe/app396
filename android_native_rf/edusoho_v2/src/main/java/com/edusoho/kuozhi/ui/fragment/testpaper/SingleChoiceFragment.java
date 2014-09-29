package com.edusoho.kuozhi.ui.fragment.testpaper;

import android.os.Bundle;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.testpaper.ChoiceViewPagerAdapter;
import com.edusoho.kuozhi.adapter.testpaper.QuestionAdapter;
import com.edusoho.kuozhi.adapter.testpaper.QuestionViewPagerAdapter;
import com.edusoho.kuozhi.adapter.testpaper.SingleChoiceViewPagerAdapter;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.ui.widget.testpaper.ChoiceQuestionWidget;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-24.
 */
public class SingleChoiceFragment extends SelectQuestionFragment{

    private QuestionType type = QuestionType.single_choice;

    @Override
    public String getTitle() {
        return "单选题";
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

        mQuestionCount = questionTypeSeqs.size();
        mQuestionType.setText(type.title());
        mQuestionNumber.setText(String.format("%d/%d", mCurrentIndex, mQuestionCount));

        QuestionAdapter adapter = new QuestionAdapter(
                mContext, questionTypeSeqs
        );

        mQuestionPager.setAdapter(adapter);
    }
}
