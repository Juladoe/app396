package com.edusoho.kuozhi.ui.widget.testpaper;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Testpaper.MaterialQuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.ui.lesson.TestpaperActivity;

import java.util.ArrayList;


/**
 * Created by howzhi on 14-9-29.
 */
public class SingleChoiceQuestionWidget extends BaseQuestionWidget {

    protected RadioGroup radioGroup;
    protected TextView stemView;

    public SingleChoiceQuestionWidget(Context context) {
        super(context);
    }

    public SingleChoiceQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView(AttributeSet attrs) {
    }

    @Override
    protected void invalidateData() {
        super.invalidateData();
        radioGroup = (RadioGroup) this.findViewById(R.id.quetion_choice_group);
        stemView = (TextView) this.findViewById(R.id.question_stem);

        stemView.setText(getQuestionStem());

        Question mQuestion = mQuestionSeq.question;
        ArrayList<String> metas = mQuestion.metas;
        if (metas != null) {
            int size = metas.size();
            for (int i=0; i < size; i++) {
                RadioButton radioButton = initRadioButton(metas.get(i), i + 1);
                if (i == 0) {
                    radioGroup.check(radioButton.getId());
                }
                radioGroup.addView(radioButton);
            }
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int index) {
                sendMsgToTestpaper();
            }
        });

        if (mQuestion.testResult != null) {
            enable(radioGroup, false);
            mAnalysisVS = (ViewStub) this.findViewById(R.id.quetion_choice_analysis);
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
            View child = radioGroup.getChildAt(i);
            for (String answer : mQuestion.answer) {
                if (answer.equals(String.valueOf(i))) {
                    child.setSelected(true);
                    break;
                }
            }
        }
    }

    protected void sendMsgToTestpaper()
    {
        Bundle bundle = new Bundle();
        bundle.putInt("index", mIndex - 1);
        if (mQuestionSeq instanceof MaterialQuestionTypeSeq) {
            bundle.putString("QuestionType", QuestionType.material.name());
        } else {
            bundle.putString("QuestionType", mQuestionSeq.question.type.name());
        }
        int count = radioGroup.getChildCount();
        ArrayList<String> data = new ArrayList<String>();
        for (int i=0; i < count; i++) {
            CompoundButton radioButton = (CompoundButton) radioGroup.getChildAt(i);
            if (radioButton.isChecked()) {
                data.add(i + "");
            }
        }

        bundle.putStringArrayList("data", data);
        EdusohoApp.app.sendMsgToTarget(
                TestpaperActivity.CHANGE_ANSWER, bundle, TestpaperActivity.class);
    }

    private RadioButton initRadioButton(String text, int index)
    {
        RadioButton radioButton = new RadioButton(mContext);
        radioButton.setText(text);
        radioButton.setPadding(20, 20, 20, 20);
        Resources resources = mContext.getResources();
        radioButton.setTextColor(resources.getColorStateList(R.color.question_choice_btn_color));
        int id = resources.getIdentifier(
                "question_choice_" + index, "drawable", mContext.getPackageName());
        radioButton.setButtonDrawable(id);

        return radioButton;
    }

    @Override
    public void setData(QuestionTypeSeq questionSeq, int index) {
        super.setData(questionSeq, index);
        invalidateData();
    }
}
