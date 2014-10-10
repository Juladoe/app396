package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.Notify;


import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-16.
 */
public class MessageListAdapter extends BaseAdapter
{
    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private ArrayList<Notify> mList;

    public MessageListAdapter(
            Context context, ArrayList<Notify> list, int resource)
    {
        mList = list;
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
    }

    private void listAddItem(ArrayList<Notify> notifies)
    {
        mList.addAll(notifies);
    }

    public void addItem(ArrayList<Notify> notifies)
    {
        listAddItem(notifies);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<Notify> notifies){
        mList.clear();
        addItem(notifies);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int index) {
        return mList.get(index);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int index, View view, ViewGroup vg) {
        if (view == null) {
            view = inflater.inflate(mResouce, null);
        }

        TextView textView = (TextView) view;
        Notify notify = mList.get(index);
        textView.setText(Html.fromHtml(notify.message));
        return view;
    }

}