package com.edusohoapp.app.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edusohoapp.app.R;
import com.edusohoapp.app.entity.NotificationItem;
import com.edusohoapp.app.entity.RecommendSchoolItem;
import com.edusohoapp.app.model.Notify;
import com.edusohoapp.app.util.AppUtil;

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
        String message = item.content.message;
        if (item.content.message != null) {
            holder.notification_message.setText(message.replaceAll("<[^>]+>", ""));
        } else {
            Notify.NotifyEnum ne = Notify.NotifyEnum.cover(item.content.threadType);
            switch (ne) {
                case QUESTION:
                    StringBuffer buffer = new StringBuffer(item.content.threadUserNickname);
                    buffer.append("  在课程  ")
                          .append(item.content.courseTitle)
                          .append("  发表了问题  ")
                          .append(item.content.threadTitle);
                    holder.notification_message.setText(buffer.toString());
                    break;
                case EMPTY:
                    break;
            }
        }
        holder.notification_time.setText(AppUtil.coverTime(item.createdTime));
        return view;
    }

    private class ViewHolder
    {
        public TextView notification_message;
        public TextView notification_time;
    }

}
