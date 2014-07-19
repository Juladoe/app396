package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.CourseMenu;


public class CourseMenuItemAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private CourseMenu[] mList;

    public CourseMenuItemAdapter(Context context, CourseMenu[] list, int resource)
    {
        mList = list;
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.length;
    }

    @Override
    public Object getItem(int index) {
        return mList[index];
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

        TextView courseMenuItem = (TextView) view.findViewById(R.id.course_menu_item);
        courseMenuItem.setText(mList[index].name);
        return view;
    }

}

