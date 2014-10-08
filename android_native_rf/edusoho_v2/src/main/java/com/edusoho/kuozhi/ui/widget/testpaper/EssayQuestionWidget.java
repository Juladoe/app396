package com.edusoho.kuozhi.ui.widget.testpaper;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;

import java.util.ArrayList;


/**
 * Created by howzhi on 14-9-29.
 */
public class EssayQuestionWidget extends BaseQuestionWidget {

    protected TextView stemView;

    public EssayQuestionWidget(Context context) {
        super(context);
    }

    public EssayQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void invalidateData() {
        stemView = (TextView) this.findViewById(R.id.question_stem);
        stemView.setText(getQuestionStem());
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
