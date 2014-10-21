package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-10-20.
 */
public abstract class ListBaseAdapter<T> extends BaseAdapter {

    protected LayoutInflater inflater;
    protected int mRecourse;
    protected Context mContext;
    protected ArrayList<T> mList;

    public ListBaseAdapter(Context context, int resource)
    {
        mRecourse = resource;
        mContext = context;
        mList = new ArrayList<T>();
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    public void clear()
    {
        mList.clear();
    }

    public abstract void addItems(ArrayList<T> list);
}
