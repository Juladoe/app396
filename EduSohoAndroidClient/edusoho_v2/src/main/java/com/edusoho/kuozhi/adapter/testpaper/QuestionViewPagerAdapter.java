package com.edusoho.kuozhi.adapter.testpaper;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.model.Testpaper.MaterialQuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.ui.widget.testpaper.QuestionWidget;
import com.edusoho.kuozhi.util.html.EduImageGetterHandler;
import com.edusoho.kuozhi.util.html.EduTagHandler;

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

    protected Spanned getQuestionStem(
            MaterialQuestionTypeSeq questionTypeSeq, int mIndex, TextView textView){
        return null;
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
