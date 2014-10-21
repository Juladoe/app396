package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.model.Notify;


import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-16.
 */
public class MessageListAdapter extends ListBaseAdapter<Notify>
{

    public MessageListAdapter(
            Context context,  int resource)
    {
        super(context, resource);
    }

    @Override
    public void addItems(ArrayList<Notify> list) {
        mList.addAll(list);
        notifyDataSetChanged();
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