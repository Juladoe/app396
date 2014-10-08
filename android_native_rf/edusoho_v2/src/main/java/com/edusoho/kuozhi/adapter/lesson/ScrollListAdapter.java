package com.edusoho.kuozhi.adapter.lesson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;

import java.util.LinkedList;
import java.util.List;

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
