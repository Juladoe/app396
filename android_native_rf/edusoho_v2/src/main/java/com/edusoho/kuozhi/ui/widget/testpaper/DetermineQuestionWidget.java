package com.edusoho.kuozhi.ui.widget.testpaper;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
public class DetermineQuestionWidget extends BaseQuestionWidget {

    protected TextView stemView;
    protected RadioGroup radioGroup;

    public DetermineQuestionWidget(Context context) {
        super(context);
    }

    public DetermineQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void invalidateData() {
        stemView = (TextView) this.findViewById(R.id.question_stem);
        radioGroup = (RadioGroup) findViewById(R.id.question_result_radio);

        stemView.setText(getQuestionStem());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int index) {
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
        });
    }

    @Override
    protected void initView(AttributeSet attrs) {
    }

    @Override
    public void setData(QuestionTypeSeq questionSeq, int index) {
        super.setData(questionSeq, index);
        invalidateData();
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {

        }
    };
}
