package com.edusoho.kuozhi.adapter.testpaper;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.ui.widget.testpaper.QuestionWidget;

import java.util.ArrayList;

public abstract class QuestionViewPagerAdapter extends PagerAdapter {

    protected LayoutInflater inflater;
    protected int mResouce;
    protected Context mContext;
    protected ArrayList<QuestionTypeSeq> mList;

    public QuestionViewPagerAdapter(
            Context context, ArrayList<QuestionTypeSeq> list, int resource)
    {
        mList = list;
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * 获取题干
     * @param question
     * @param index
     * @return
     */
    protected String getQuestionStem(Question question, int index)
    {
        switch (question.type) {
            case choice:
            case uncertain_choice:
            case single_choice:
                return String.format("%d, (%s) %s %s", index, question.type.title(), question.stem, "( )");
            case essay:
                return index + question.type.title() + question.stem;
            case material:
                return index + question.type.title() + question.stem;
            case determine:
                return index + ", " + question.stem;
            case fill:
                return index + question.type.title() + question.stem;
        }

        return "";
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    protected View switchQuestionWidget(QuestionTypeSeq questionSeq, int index)
    {
        QuestionWidget widget = new QuestionWidget(mContext, questionSeq, index);
        return widget.getView();
    }
}
