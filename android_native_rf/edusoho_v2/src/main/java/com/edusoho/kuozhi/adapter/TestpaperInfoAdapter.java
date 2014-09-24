package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Category;
import com.edusoho.kuozhi.model.Testpaper.TestpaperItem;
import com.edusoho.kuozhi.ui.widget.MuiltTextView;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorInflater;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TestpaperInfoAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private ArrayList<TestpaperItem> mList;

    public TestpaperInfoAdapter(
            Context context, ArrayList<TestpaperItem> list, int resource)
    {
        mList = list;
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
        TestpaperItem item = mList.get(index);
        if (view == null) {
            currentView = inflater.inflate(mResouce, null);
            holder = new ViewHolder();
            holder.mTitle = (MuiltTextView) currentView.findViewById(R.id.testpaper_item);
            currentView.setTag(holder);
        } else {
            currentView = view;
            holder = (ViewHolder) currentView.getTag();
        }

        holder.mTitle.setContent(item);
        return currentView;
    }

    private class ViewHolder
    {
        public MuiltTextView mTitle;
    }

}
