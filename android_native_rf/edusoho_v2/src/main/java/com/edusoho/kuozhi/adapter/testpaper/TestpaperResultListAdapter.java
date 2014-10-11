package com.edusoho.kuozhi.adapter.testpaper;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Testpaper.Accuracy;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.util.AppUtil;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-10-9.
 */
public class TestpaperResultListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private ArrayList<Accuracy> mList;
    private ArrayList<QuestionType> mTypeList;

    public TestpaperResultListAdapter(
            Context context,
            ArrayList<Accuracy> list,
            ArrayList<QuestionType> typeList,
            int resource
    )
    {
        mList = list;
        mTypeList = typeList;
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
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
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        View currentView;
        Accuracy accuracy = mList.get(index);
        QuestionType questionType = mTypeList.get(index);
        if (view == null) {
            currentView = inflater.inflate(mResouce, null);
            holder = new ViewHolder();
            holder.mType = (TextView) currentView.findViewById(R.id.testpaper_result_type);
            holder.mRight = (TextView) currentView.findViewById(R.id.testpaper_result_right);
            holder.mTotal = (TextView) currentView.findViewById(R.id.testpaper_result_total);
            currentView.setTag(holder);
        } else {
            currentView = view;
            holder = (ViewHolder) currentView.getTag();
        }

        holder.mType.setText(questionType.title());
        setRightText(holder.mRight, accuracy);
        holder.mTotal.setText(accuracy.totalScore + "");
        return currentView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private void setRightText(TextView rightText, Accuracy accuracy)
    {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(accuracy.all);
        stringBuffer.append("/");
        int start = stringBuffer.length();
        stringBuffer.append(accuracy.right);
        SpannableString spannableString = new SpannableString(stringBuffer);
        int color = mContext.getResources().getColor(R.color.action_bar_bg);
        spannableString.setSpan(
                new ForegroundColorSpan(color),
                start,
                start + AppUtil.getNumberLength(accuracy.right),
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        );
        rightText.setText(spannableString);
    }

    private class ViewHolder
    {
        public TextView mType;
        public TextView mRight;
        public TextView mTotal;
    }
}
