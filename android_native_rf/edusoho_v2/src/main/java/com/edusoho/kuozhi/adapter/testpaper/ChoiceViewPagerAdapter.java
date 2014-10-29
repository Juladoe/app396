package com.edusoho.kuozhi.adapter.testpaper;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;

import java.util.ArrayList;

public class ChoiceViewPagerAdapter extends QuestionViewPagerAdapter {

    public ChoiceViewPagerAdapter(
            Context context, ArrayList<QuestionTypeSeq> list, int resource)
    {
        super(context, list, resource);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(mResouce, null);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.quetion_choice_group);
        TextView stemView = (TextView) view.findViewById(R.id.question_stem);

        QuestionTypeSeq questionTypeSeq = mList.get(position);
        Question question = questionTypeSeq.question;
        stemView.setText(getQuestionStem(question, position + 1));
        ArrayList<String> metas = question.metas;
        if (metas != null) {
            int size = metas.size();
            for (int i=0; i < size; i++) {
                CheckBox checkBox = initCheckBox(metas.get(i), i + 1);
                radioGroup.addView(checkBox);
            }
        }
        container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return view;
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
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
