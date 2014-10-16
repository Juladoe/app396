package com.edusoho.kuozhi.adapter.testpaper;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
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

    protected Spanned getQuestionStem(Question mQuestion, int mIndex)
    {
        String stem = "";
        switch (mQuestion.type) {
            case choice:
            case uncertain_choice:
            case single_choice:
                stem = String.format("%d, (%s) %s %s", mIndex, mQuestion.type.title(), mQuestion.stem, "( )");
                break;
            case essay:
                stem = mIndex + mQuestion.type.title() + mQuestion.stem;
                break;
            case material:
                stem = mIndex + mQuestion.type.title() + mQuestion.stem;
                break;
            case determine:
                stem = mIndex + ", " + mQuestion.stem;
                break;
            case fill:
                stem = mIndex + mQuestion.type.title() + mQuestion.stem;
        }

        return Html.fromHtml(stem);
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
