package com.edusoho.kuozhi.adapter.testpaper;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.TestpaperItem;
import com.edusoho.kuozhi.ui.widget.MuiltTextView;

import java.util.ArrayList;

public class QuestionViewPagerAdapter extends PagerAdapter {

    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private ArrayList<QuestionTypeSeq> mList;

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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(0, null);
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
