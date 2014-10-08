package com.edusoho.kuozhi.adapter.testpaper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Question.Answer;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;

import java.util.ArrayList;

public class TestpaperCardAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private ArrayList<QuestionTypeSeq> mList;
    private ArrayList<Answer> mAnswers;

    public TestpaperCardAdapter(
            Context context,
            ArrayList<QuestionTypeSeq> list,
            ArrayList<Answer> answers,
            int resource)
    {
        mList = list;
        mContext = context;
        mAnswers = answers;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(mResouce, null);
        }

        TextView mText = (TextView) view;
        Answer answer = mAnswers.get(i);
        mText.setEnabled(!answer.isAnswer);
        mText.setText((i + 1) + "");
        return view;
    }

}
