package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.CourseMenu;

import java.util.ArrayList;


public class CourseMenuItemAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private int mResouce;
    private Context mContext;
    private ArrayList<CourseMenu> mList;

    public CourseMenuItemAdapter(Context context, ArrayList<CourseMenu> list, int resource)
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
        if (view == null) {
            view = inflater.inflate(mResouce, null);
        }
        CourseMenu courseMenus = mList.get(index);
        TextView courseMenuItem = (TextView) view.findViewById(R.id.course_menu_item);
        switch (courseMenus.action) {
            case CourseMenu.ALL:
                courseMenuItem.setText("全部");
                break;
            case CourseMenu.BACK:
                courseMenuItem.setText("上一级");
                break;
            default:
                courseMenuItem.setText(courseMenus.name);
        }

        return view;
    }

}