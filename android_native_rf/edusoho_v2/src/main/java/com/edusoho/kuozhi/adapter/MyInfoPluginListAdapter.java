package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.MyInfoPlugin;

import java.util.ArrayList;

public class MyInfoPluginListAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private ArrayList<MyInfoPlugin> mList;

    public MyInfoPluginListAdapter(
            Context context, ArrayList<MyInfoPlugin> list, int resource)
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
            holder.mTitle = (TextView) view.findViewById(R.id.myinfo_plugin_list_item_title);
            holder.mIcon = (ImageView) view.findViewById(R.id.myinfo_plugin_list_item_icon);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        MyInfoPlugin myInfoPlugin = mList.get(index);
        holder.mTitle.setText(myInfoPlugin.name);
        holder.mIcon.setImageBitmap(myInfoPlugin.icon);
        return view;
    }

    private class ViewHolder
    {
        public TextView mTitle;
        public ImageView mIcon;
    }

}
