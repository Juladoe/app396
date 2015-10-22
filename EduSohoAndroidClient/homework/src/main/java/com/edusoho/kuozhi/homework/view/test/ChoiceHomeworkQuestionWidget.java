package com.edusoho.kuozhi.homework.view.test;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.edusoho.kuozhi.homework.HomeworkActivity;
import com.edusoho.kuozhi.homework.R;
import com.edusoho.kuozhi.homework.model.HomeWorkQuestion;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionType;
import com.edusoho.kuozhi.v3.ui.test.TestpaperActivity;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by howzhi on 14-9-29.
 */
public class ChoiceHomeworkQuestionWidget extends BaseHomeworkQuestionWidget {

    protected RadioGroup radioGroup;

    public ChoiceHomeworkQuestionWidget(Context context) {
        super(context);
    }

    public ChoiceHomeworkQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private CompoundButton.OnCheckedChangeListener checkedChangeListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    sendMsgToHomework();
                }
    };

    protected void sendMsgToHomework() {
        Bundle bundle = new Bundle();
        bundle.putInt("index", mIndex - 1);
        bundle.putString("QuestionType", QuestionType.material.name());

        int count = radioGroup.getChildCount();
        ArrayList<String> data = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            CompoundButton radioButton = (CompoundButton) radioGroup.getChildAt(i);
            if (radioButton.isChecked()) {
                data.add(i + "");
            }
        }

        bundle.putStringArrayList("data", data);
        MessageEngine.getInstance().sendMsgToTaget(
                HomeworkActivity.CHANGE_ANSWER, bundle, HomeworkActivity.class);
    }

    @Override
    protected void invalidateData() {
        super.invalidateData();
        radioGroup = (RadioGroup) this.findViewById(R.id.homework_quetion_choice_group);

        List<String> metas = mQuestion.getMetas();
        if (metas != null) {
            int size = metas.size();
            for (int i = 0; i < size; i++) {
                CheckBox checkBox = initCheckBox(metas.get(i), i + 1);
                checkBox.setOnCheckedChangeListener(checkedChangeListener);

                RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.bottomMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.homework_question_choice_padding);;
                radioGroup.addView(checkBox, layoutParams);
            }
        }

        if (mQuestion.getResult() != null) {
            enable(radioGroup, false);
            mAnalysisVS = (ViewStub) this.findViewById(R.id.hw_quetion_choice_analysis);
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

    private void initQuestionResult() {
        int count = radioGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = radioGroup.getChildAt(i);
            for (String answer : mQuestion.getAnswer()) {
                if (answer.equals(String.valueOf(i))) {
                    child.setSelected(true);
                    break;
                }
            }
        }
    }

    @Override
    protected void initView(AttributeSet attrs) {
    }

    private CheckBox initCheckBox(String text, int index) {
        CheckBox checkBox = (CheckBox) LayoutInflater.from(mContext).inflate(
                R.layout.question_checkbox, null);
        checkBox.setText(text);
        Resources resources = mContext.getResources();
        checkBox.setTextColor(resources.getColorStateList(R.color.question_choice_btn_color));
        int id = resources.getIdentifier(
                "question_choice_" + index, "drawable", mContext.getPackageName());
        checkBox.setButtonDrawable(id);

        return checkBox;
    }

    @Override
    public void setData(HomeWorkQuestion questionSeq, int index) {
        super.setData(questionSeq, index);
        invalidateData();
    }
}
