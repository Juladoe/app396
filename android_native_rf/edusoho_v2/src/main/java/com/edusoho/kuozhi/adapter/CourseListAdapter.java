package com.edusoho.kuozhi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
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

public class CourseListAdapter extends EdusohoBaseAdapter {

    protected LayoutInflater inflater;
    protected int mResouce;
    protected Context mContext;
    protected ArrayList<Course> mList;
    public int page = 0;
    public int count = 0;

    public CourseListAdapter(Context context, CourseResult courseResult,
                             int resource) {
        mList = new ArrayList<Course>();
        listAddItem(courseResult.data);
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
        setMode(NORMAL);
    }

    private void listAddItem(ArrayList<Course> courseItems)
    {
        mList.addAll(courseItems);
    }

    public void addItem(CourseResult courseResult)
    {
        setMode(UPDATE);
        listAddItem(courseResult.data);
        notifyDataSetChanged();
    }

    public void setItems(CourseResult courseResult){
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
            holder.course_studentNum = (TextView) view.findViewById(R.id.course_studentNum);
            holder.course_price = (TextView) view.findViewById(R.id.course_price);
            holder.course_ratingbar = (RatingBar) view.findViewById(R.id.course_rating);

            holder.course_teacher_nickname = (TextView) view.findViewById(R.id.course_teacher_nickname);
            holder.aq = new AQuery(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        switch (mMode){
            case UPDATE:
                updateViewData(holder, index);
                break;
            case NORMAL:
                invaliViewData(holder, index);
        }
        return view;
    }

    public void updateViewData(ViewHolder holder, int index)
    {
        Course course =  mList.get(index);
        Teacher user = null;
        holder.course_title.setText(course.title);
        if ("opened".equals(course.showStudentNumType)) {
            holder.course_studentNum.setText(course.studentNum + " 学员");
        }

        holder.course_ratingbar.setRating((float) course.rating);
        if (course.teachers.length > 0) {
            user = course.teachers[0];
            holder.course_teacher_nickname.setText("教师: " + user.nickname);
            holder.aq.id(R.id.course_teacher_face).image(
                    user.avatar, false, true);
        }

        holder.course_price.setText(course.price == 0 ? "免费": course.price + "元");

        int width = (int)(EdusohoApp.app.screenW * 0.5f);
        if (TextUtils.isEmpty(course.largePicture)) {
            holder.aq.id(R.id.course_pic).image(R.drawable.noram_course);
        } else {
            if (urlCacheExistsed(mContext, course.largePicture)) {
                return;
            }

            holder.aq.id(R.id.course_pic).image(
                    course.largePicture, false, true, width, R.drawable.noram_course);
            holder.aq.id(R.id.course_pic)
                    .width(width)
                    .height(AppUtil.getCourseListCoverHeight(width), false);
        }
    }

    public void invaliViewData(ViewHolder holder, int index)
    {
        Course course =  mList.get(index);
        Teacher user = null;
        holder.course_title.setText(course.title);
        if ("opened".equals(course.showStudentNumType)) {
            holder.course_studentNum.setText(course.studentNum + " 学员");
        }

        int width = (int)(EdusohoApp.app.screenW * 0.5f);
        if (TextUtils.isEmpty(course.largePicture)) {
            holder.aq.id(R.id.course_pic).image(R.drawable.noram_course);
        } else {
            holder.aq.id(R.id.course_pic).image(
                    course.largePicture, false, true, width, R.drawable.noram_course);
            Log.d(null, "height->" + AppUtil.getCourseListCoverHeight(width));
            holder.aq.id(R.id.course_pic)
                    .width(width, false)
                    .height(AppUtil.getCourseListCoverHeight(width), false);
        }

        holder.aq.id(R.id.course_pic).height(AppUtil.getCourseListCoverHeight(width), false);

        holder.course_ratingbar.setRating((float) course.rating);
        if (course.teachers.length > 0) {
            user = course.teachers[0];
            holder.course_teacher_nickname.setText("教师: " + user.nickname);
            holder.aq.id(R.id.course_teacher_face).image(
                    user.avatar, false, true);
        }
        holder.course_price.setText(course.price == 0 ? "免费": course.price + "元");
    }

    protected class ViewHolder {
        public AQuery aq;
        public RatingBar course_ratingbar;
        public TextView course_title;
        public TextView course_studentNum;
        public TextView course_teacher_nickname;
        public TextView course_price;
    }

}
