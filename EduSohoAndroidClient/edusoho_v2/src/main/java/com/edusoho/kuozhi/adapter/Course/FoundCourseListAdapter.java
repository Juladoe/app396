package com.edusoho.kuozhi.adapter.Course;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.util.Const;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import ch.boye.httpclientandroidlib.util.TextUtils;

/**
 * Created by howzhi on 14/11/20.
 */
public class FoundCourseListAdapter extends ListBaseAdapter<Course> {

    private DisplayImageOptions mOptions;

    public FoundCourseListAdapter(Context context, int resource) {
        super(context, resource);
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    @Override
    public void addItem(Course item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    @Override
    public void addItems(ArrayList<Course> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(mResource, null);
            holder = new ViewHolder();
            holder.mCourseTitle = (TextView) view.findViewById(R.id.found_list_course_title);
            holder.mCourseTeacher = (TextView) view.findViewById(R.id.found_list_course_teacher);
            holder.mCourseStudentNum = (TextView) view.findViewById(R.id.found_list_course_studentnum);
            holder.mCoursePic = (ImageView) view.findViewById(R.id.found_list_course_pic);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Course course = mList.get(i);
        holder.mCourseTitle.setText(course.title);
        if (Const.SHOW_STUDENT_NUM.equals(course.showStudentNumType)) {
            holder.mCourseStudentNum.setVisibility(View.VISIBLE);
            holder.mCourseStudentNum.setText(String.valueOf(course.studentNum));
        } else {
            holder.mCourseStudentNum.setVisibility(View.GONE);
        }

        Teacher[] teachers = course.teachers;
        if (teachers != null && teachers.length > 0) {
            holder.mCourseTeacher.setText(String.valueOf(teachers[0].nickname));
        }

        if (!TextUtils.isEmpty(course.largePicture)) {
            ImageLoader.getInstance().displayImage(course.largePicture, holder.mCoursePic, mOptions);
        }
        return view;
    }

    protected class ViewHolder {
        public TextView mCourseTitle;
        public TextView mCourseTeacher;
        public TextView mCourseStudentNum;
        public ImageView mCoursePic;
    }
}
