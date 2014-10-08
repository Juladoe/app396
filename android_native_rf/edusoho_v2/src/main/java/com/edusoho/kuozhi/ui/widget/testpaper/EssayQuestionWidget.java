package com.edusoho.kuozhi.ui.widget.testpaper;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
public class EssayQuestionWidget extends BaseQuestionWidget {

    protected TextView stemView;
    private EditText contentEdt;

    public EssayQuestionWidget(Context context) {
        super(context);
    }

    public EssayQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private TextWatcher onTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            Bundle bundle = new Bundle();
            bundle.putInt("index", mIndex - 1);
            if (mQuestionSeq instanceof MaterialQuestionTypeSeq) {
                bundle.putString("QuestionType", QuestionType.material.name());
            } else {
                bundle.putString("QuestionType", mQuestionSeq.question.type.name());
            }
            ArrayList<String> data = new ArrayList<String>();
            data.add(charSequence.toString());
            bundle.putStringArrayList("data", data);
            EdusohoApp.app.sendMsgToTarget(
                    TestpaperActivity.CHANGE_ANSWER, bundle, TestpaperActivity.class);
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };


    @Override
    protected void invalidateData() {
        stemView = (TextView) this.findViewById(R.id.question_stem);
        contentEdt = (EditText) this.findViewById(R.id.essay_content);

        stemView.setText(getQuestionStem());
        contentEdt.addTextChangedListener(onTextChangedListener);
    }

    @Override
    protected void initView(AttributeSet attrs) {
    }

    @Override
    public void setData(QuestionTypeSeq questionSeq, int index) {
        super.setData(questionSeq, index);
        invalidateData();
    }
}
