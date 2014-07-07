package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Notify;
import com.edusoho.kuozhi.util.AppUtil;

import java.util.ArrayList;

public class NotificationListAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private ArrayList<Notify> mList;

    public NotificationListAdapter(
            Context context, ArrayList<Notify> list, int resource)
    {
        mList = list;
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
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
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(mResouce, null);
            holder = new ViewHolder();
            holder.notification_time = (TextView) view.findViewById(R.id.notification_time);
            holder.notification_message = (TextView) view.findViewById(R.id.notification_message);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Notify item = mList.get(index);
        String message = item.message;
        holder.notification_message.setText(message.replaceAll("<[^>]+>", ""));
        holder.notification_time.setText(AppUtil.coverTime(item.createdTime));
        return view;
    }

    private class ViewHolder
    {
        public TextView notification_message;
        public TextView notification_time;
    }

}
