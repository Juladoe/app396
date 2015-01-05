package com.edusoho.kuozhi.adapter.Course;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.util.Const;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howzhi on 14/11/19.
 */
public class WeekCourseAdapter
        extends RecyclerViewListBaseAdapter<Course, WeekCourseAdapter.ViewHolder> {

    private DisplayImageOptions mOptions;

    public WeekCourseAdapter(Context context, int resource)

    {
        super(context, resource);
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    @Override
    public void addItems(List<Course> list) {
        mList.addAll(list);
        notifyItemRangeInserted(mList.size() - 1 - list.size(), mList.size() - 1);
    }

    @Override
    public void addItem(Course item) {
        mList.add(item);
        notifyItemInserted(mList.size() - 1);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        Course course = mList.get(i);
        String teacher = "暂无教师";
        Teacher[] teachers = course.teachers;
        if (teachers != null && teachers.length > 0) {
            teacher = teachers[0].nickname;
        }

        viewHolder.mCourseTeacherName.setText(teacher);
        if (Const.SHOW_STUDENT_NUM.equals(course.showStudentNumType)) {
            viewHolder.mCourseStudentNum.setVisibility(View.VISIBLE);
            viewHolder.mCourseStudentNum.setText(course.studentNum + "");
        } else {
            viewHolder.mCourseStudentNum.setVisibility(View.GONE);
        }

        viewHolder.mCourseTitle.setText(course.title);
        ImageLoader.getInstance().displayImage(course.largePicture, viewHolder.mCoursePic, mOptions);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(mResource, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mCourseTitle;
        public TextView mCourseStudentNum;
        public TextView mCourseTeacherName;
        public TextView mCoursePrice;
        public ImageView mCoursePic;

        public ViewHolder(View view)
        {
            super(view);
            mCourseTitle = (TextView) view.findViewById(R.id.found_list_course_title);
            mCourseStudentNum = (TextView) view.findViewById(R.id.found_list_course_studentnum);
            mCourseTeacherName = (TextView) view.findViewById(R.id.found_list_course_teacher);
            //mCoursePrice = (TextView) view.findViewById(R.id.found_list_course_pic);
            mCoursePic = (ImageView) view.findViewById(R.id.found_list_course_pic);
        }
    }

}
