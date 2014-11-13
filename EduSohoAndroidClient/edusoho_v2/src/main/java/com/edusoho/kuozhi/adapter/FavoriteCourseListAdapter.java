package com.edusoho.kuozhi.adapter;

import android.content.Context;
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

public class FavoriteCourseListAdapter extends BaseAdapter {

    protected LayoutInflater inflater;
    protected int mResouce;
    protected Context mContext;
    protected ArrayList<Course> mList;
    public int page = 0;
    public int count = 0;

    public FavoriteCourseListAdapter(Context context, CourseResult courseResult,
                                     int resource) {
        mList = new ArrayList<Course>();
        listAddItem(courseResult.data);
        mContext = context;
        mResouce = resource;
        inflater = LayoutInflater.from(context);
    }

    public void setItems(Course[] courseItems)
    {
        int equalsResult = -1;
        int size = mList.size();
        for (int i=0; i < size; i++) {
            Course course = mList.get(i);
            mList.remove(i);
            if ((equalsResult = equalsItem(course, courseItems)) > 0) {
                mList.add(i, courseItems[equalsResult]);
            }
        }
        notifyDataSetChanged();
    }

    public void refreshItem(int courseId, boolean isFavorite)
    {
        int size = mList.size();
        for (int i=0; i < size; i++) {
            Course course = mList.get(i);
            if (course.id == courseId && ! isFavorite) {
                mList.remove(i);
                notifyDataSetChanged();
                return;
            }
        }
    }

    private int equalsItem(Course src, Course[] targets)
    {
        int count = targets.length;
        for (int i=0; i < count; i++) {
            if (src.id == targets[i].id) {
                return i;
            }
        }
        return -1;
    }
    /**
     *
     * @param courseItems
     */
    private void listAddItem(ArrayList<Course> courseItems)
    {
        mList.addAll(courseItems);
    }

    public void addItem(CourseResult result)
    {
        listAddItem(result.data);
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
            holder.course_studentNum = (TextView) view.findViewById(R.id.course_studentNum);
            holder.course_price = (TextView) view.findViewById(R.id.course_price);
            holder.course_ratingbar = (RatingBar) view.findViewById(R.id.course_rating);
            holder.course_teacher_nickname = (TextView) view.findViewById(R.id.course_teacher_nickname);
            holder.aq = new AQuery(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.course_ratingbar.setRating((float) course.rating);
        holder.course_price.setText(course.price == 0 ? "免费" : course.price + "元");
        holder.course_title.setText(course.title);
        holder.course_studentNum.setText("学员数:" + course.studentNum);
        int width = EdusohoApp.app.screenW;
        holder.aq.id(R.id.course_pic).image(
                course.largePicture, true, true, (int)(width * 0.5f), R.drawable.noram_course);

        holder.aq.id(R.id.course_pic).height(AppUtil.getCourseListCoverHeight(width), false);
        if (course.teachers.length > 0) {
            Teacher user = course.teachers[0];
            holder.course_teacher_nickname.setText("教师:" + user.nickname);
            holder.aq.id(R.id.course_teacher_face).image(
                    user.avatar, false, true);
        }

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
