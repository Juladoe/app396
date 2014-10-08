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

public class EssayViewPagerAdapter extends QuestionViewPagerAdapter {

    public EssayViewPagerAdapter(
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
        TextView stemView = (TextView) view.findViewById(R.id.question_stem);

        QuestionTypeSeq questionTypeSeq = mList.get(position);
        Question question = questionTypeSeq.question;
        stemView.setText(getQuestionStem(question, position + 1));
        container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return view;
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
