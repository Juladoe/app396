package com.edusoho.kuozhi.adapter;

import java.util.ArrayList;

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

public class CourseListAdapter extends BaseAdapter {

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
        Teacher user = null;
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

        holder.course_title.setText(course.title);
        if ("opened".equals(course.showStudentNumType)) {
            holder.course_studentNum.setText(course.studentNum + " 学员");
        }

        if (TextUtils.isEmpty(course.largePicture)) {
            holder.aq.id(R.id.course_pic).image(R.drawable.noram_course);
        } else {
            holder.aq.id(R.id.course_pic).image(
                    course.largePicture, false, true);
        }

        int width = EdusohoApp.app.screenW;
        holder.aq.id(R.id.course_pic).height(AppUtil.getCourseListCoverHeight(width), false);

        holder.course_ratingbar.setRating((float) course.rating);
        if (course.teachers.length > 0) {
            user = course.teachers[0];
            holder.course_teacher_nickname.setText("教师: " + user.nickname);
            holder.aq.id(R.id.course_teacher_face).image(
                    user.avatar, false, true);
        }
        holder.course_price.setText(course.price == 0 ? "免费": course.price + "元");
        return view;
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
