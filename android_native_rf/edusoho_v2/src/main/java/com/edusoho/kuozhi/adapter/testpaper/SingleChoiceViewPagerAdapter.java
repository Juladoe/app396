package com.edusoho.kuozhi.adapter.testpaper;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;

import java.util.ArrayList;

public class SingleChoiceViewPagerAdapter extends QuestionViewPagerAdapter {

    public SingleChoiceViewPagerAdapter(
            Context context, ArrayList<QuestionTypeSeq> list, int resource)
    {
        super(context, list, resource);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(mResouce, null);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.quetion_choice_group);
        TextView stemView = (TextView) view.findViewById(R.id.question_stem);

        QuestionTypeSeq questionTypeSeq = mList.get(position);
        Question question = questionTypeSeq.question;
        stemView.setText(getQuestionStem(question, position + 1));
        ArrayList<String> metas =  question.metas;
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
        container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return view;
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
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
