package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.util.AppUtil;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.File;
import java.util.ArrayList;

public class HorizontalCourseListAdapter extends EdusohoBaseAdapter {

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
        setMode(NORMAL);
    }

    /**
     *
     * @param courseItems
     */
    private void listAddItem(ArrayList<Course> courseItems)
    {
        mList.addAll(courseItems);
    }

    public void addItem(CourseResult courseResult)
    {
        setMode(NORMAL);
        listAddItem(courseResult.data);
        notifyDataSetChanged();
    }

    public void setItems(CourseResult courseResult)
    {
        mList.clear();
        addItem(courseResult);
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
            holder.course_title = (TextView) view.findViewById(R.id.course_title);
            holder.aq = new AQuery(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        switch (mMode) {
            case UPDATE:
                updateViewData(holder, index);
                break;
            case NORMAL:
                invaliViewData(holder, index);
        }
        return view;
    }

    private void updateViewData(ViewHolder holder, int index)
    {
        Course course =  mList.get(index);
        holder.course_title.setText(course.title);
        if (TextUtils.isEmpty(course.largePicture)) {
            holder.aq.id(R.id.course_pic).image(R.drawable.noram_course);
        } else {
            if (urlCacheExistsed(mContext, course.largePicture)) {
                return;
            }
            holder.aq.id(R.id.course_pic).image(
                    course.largePicture, false, true, 200, R.drawable.noram_course, null, AQuery.FADE_IN_NETWORK);
        }
    }

    private void invaliViewData(ViewHolder holder, int index)
    {
        Course course =  mList.get(index);
        holder.course_title.setText(course.title);
        if (TextUtils.isEmpty(course.largePicture)) {
            holder.aq.id(R.id.course_pic).image(R.drawable.noram_course);
        } else {
            holder.aq.id(R.id.course_pic).image(
                    course.largePicture, false, true, 200, R.drawable.noram_course, null, AQuery.FADE_IN_NETWORK);
        }
    }

    protected class ViewHolder {
        public AQuery aq;
        public TextView course_title;
        public TextView course_price;
    }

}
