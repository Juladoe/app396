package com.edusoho.kuozhi.ui.widget.testpaper;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
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
public class ChoiceQuestionWidget extends BaseQuestionWidget {

    protected RadioGroup radioGroup;
    protected TextView stemView;

    public ChoiceQuestionWidget(Context context) {
        super(context);
    }

    public ChoiceQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void invalidateData() {
        radioGroup = (RadioGroup) this.findViewById(R.id.quetion_choice_group);
        stemView = (TextView) this.findViewById(R.id.question_stem);

        stemView.setText(getQuestionStem());
        Question mQuestion = mQuestionSeq.question;
        ArrayList<String> metas = mQuestion.metas;
        if (metas != null) {
            int size = metas.size();
            for (int i=0; i < size; i++) {
                CheckBox checkBox = initCheckBox(metas.get(i), i + 1);
                radioGroup.addView(checkBox);
            }
        }
    }

    @Override
    protected void initView(AttributeSet attrs) {
    }

    private CheckBox initCheckBox(String text, int index)
    {
        CheckBox checkBox = new CheckBox(mContext);
        checkBox.setText(text);
        checkBox.setPadding(20, 20, 20, 20);
        Resources resources = mContext.getResources();
        checkBox.setTextColor(resources.getColorStateList(R.color.question_choice_btn_color));
        int id = resources.getIdentifier(
                "question_choice_" + index, "drawable", mContext.getPackageName());
        checkBox.setButtonDrawable(id);

        return checkBox;
    }

    @Override
    public void setData(QuestionTypeSeq questionSeq, int index) {
        super.setData(questionSeq, index);
        invalidateData();
    }
}
