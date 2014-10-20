package com.edusoho.kuozhi.adapter.lesson;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.model.Course;

/**
 * Created by howzhi on 14-9-1.
 */
public class ScrollListAdapter<T> extends AbstractCourseListAdapter {

    public ScrollListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ViewHolder holder = (ViewHolder) view.getTag();

        Course course = (Course) mInfos.get(position);
        initViewData(holder, course);

        return view;
    }
}
