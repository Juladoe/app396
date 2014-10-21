package com.edusoho.kuozhi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.util.AppUtil;

public class CourseListAdapter<T> extends ListBaseAdapter<T> {

    public CourseListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void addItems(ArrayList<T> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int index, View view, ViewGroup vg) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(mRecourse, null);
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

        invaliViewData(holder, index);
        return view;
    }

    public void invaliViewData(ViewHolder holder, int index)
    {
        Course course =  (Course) mList.get(index);
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
