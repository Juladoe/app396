package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.util.AppUtil;

import java.util.ArrayList;

public class HorizontalCourseListAdapter extends BaseAdapter {

    protected LayoutInflater inflater;
    protected int mResouce;
    protected Context mContext;
    protected ArrayList<Course> mList;
    public int page = 0;
    public int count = 0;

    public HorizontalCourseListAdapter(Context context, CourseResult courseResult,
                                       int resource) {
        mList = new ArrayList<Course>();
        listAddItem(courseResult.data);
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
    }

    /**
     *
     * @param courseItems
     */
    private void listAddItem(Course[] courseItems)
    {
        for (Course item : courseItems) {
            mList.add(item);
        }
    }

    public void addItem(CourseResult courseResult)
    {
        listAddItem(courseResult.data);
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
        Course course =  mList.get(index);
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(mResouce, null);
            holder = new ViewHolder();
            holder.course_title = (TextView) view.findViewById(R.id.course_title);
            holder.aq = new AQuery(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.course_title.setText(course.title);
        if (TextUtils.isEmpty(course.largePicture)) {
            holder.aq.id(R.id.course_pic).image(R.drawable.noram_course);
        } else {
            holder.aq.id(R.id.course_pic).image(
                    course.largePicture, false, true, 285, R.drawable.noram_course);
        }

        return view;
    }

    protected class ViewHolder {
        public AQuery aq;
        public TextView course_title;
        public TextView course_price;
    }

}
