package com.edusoho.kuozhi.homework.view.test;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import com.edusoho.kuozhi.homework.HomeworkActivity;
import com.edusoho.kuozhi.homework.R;
import com.edusoho.kuozhi.homework.model.HomeWorkQuestion;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionType;
import java.util.ArrayList;


/**
 * Created by howzhi on 14-9-29.
 */
public class DetermineHomeworkQuestionWidget extends BaseHomeworkQuestionWidget {

    protected RadioGroup radioGroup;

    public DetermineHomeworkQuestionWidget(Context context) {
        super(context);
    }

    public DetermineHomeworkQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void invalidateData() {
        super.invalidateData();
        radioGroup = (RadioGroup) findViewById(R.id.hw_question_result_radio);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int index) {
                Bundle bundle = new Bundle();
                bundle.putInt("index", mIndex - 1);
                bundle.putString("QuestionType", QuestionType.material.name());
                int count = radioGroup.getChildCount();
                ArrayList<String> data = new ArrayList<String>();
                for (int i=0; i < count; i++) {
                    CompoundButton radioButton = (CompoundButton) radioGroup.getChildAt(i);
                    if (radioButton.isChecked()) {
                        data.add(i + "");
                    }
                }

                bundle.putStringArrayList("data", data);
                MessageEngine.getInstance().sendMsgToTaget(
                        HomeworkActivity.CHANGE_ANSWER, bundle, HomeworkActivity.class);
            }
        });

        parseQuestionAnswer();
    }

    @Override
    protected void parseQuestionAnswer() {
        if (mQuestion.getResult() != null) {
            enable(radioGroup, false);
            mAnalysisVS = (ViewStub) this.findViewById(R.id.hw_quetion_analysis);
            mAnalysisVS.setOnInflateListener(new ViewStub.OnInflateListener() {
                @Override
                public void onInflate(ViewStub viewStub, View view) {
                    initResultAnalysis(view);
                    initQuestionResult();
                }
            });
            mAnalysisVS.inflate();
        }
    }

    private void initQuestionResult()
    {
        int count = radioGroup.getChildCount();
        for (int i=0; i < count; i++) {
            CompoundButton child = (CompoundButton) radioGroup.getChildAt(i);
            for (String answer : mQuestion.getAnswer()) {
                if (answer.equals(String.valueOf(i))) {
                    child.setChecked(true);
                    break;
                }
            }
        }
    }

    @Override
    protected void initView(AttributeSet attrs) {
    }

    @Override
    public void setData(HomeWorkQuestion questionSeq, int index) {
        super.setData(questionSeq, index);
        invalidateData();
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
        }
    };
}
