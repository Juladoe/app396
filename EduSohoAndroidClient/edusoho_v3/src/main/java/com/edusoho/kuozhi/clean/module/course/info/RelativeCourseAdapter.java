package com.edusoho.kuozhi.clean.module.course.info;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.v3.util.CommonUtil;

import java.util.List;

/**
 * Created by JesseHuang on 2017/4/3.
 */

public class RelativeCourseAdapter extends RecyclerView.Adapter<CourseProjectInfoFragment.ViewHolder> {

    private static final String FREE = "0.00";
    private Context mContext;
    private List<CourseProject> mCourseProjects;

    public RelativeCourseAdapter(Context context, List<CourseProject> courseProjects) {
        this.mContext = context;
        this.mCourseProjects = courseProjects;
    }

    @Override
    public CourseProjectInfoFragment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_relative_course_project, parent, false);
        return new CourseProjectInfoFragment.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseProjectInfoFragment.ViewHolder holder, int position) {
        final CourseProject courseProject = mCourseProjects.get(position);
        holder.courseTitle.setText(courseProject.title);
        if (FREE.equals(courseProject.price)) {
            holder.coursePrice.setText(R.string.free_course_project);
            holder.coursePrice.setTextColor(mContext.getResources().getColor(R.color.primary_color));
        } else {
            holder.coursePrice.setText(String.format("¥ %s", courseProject.price));
            holder.coursePrice.setTextColor(mContext.getResources().getColor(R.color.secondary_color));
        }
        holder.courseTasks.setText(String.format(mContext.getString(R.string.course_task_num), courseProject.publishedTaskNum));
        if (courseProject.services == null || courseProject.services.length == 0) {
            holder.promiseServiceLayout.setVisibility(View.GONE);
        } else {
            holder.promiseServiceLayout.setVisibility(View.VISIBLE);
            for (CourseProject.Service service : courseProject.services) {
                TextView serviceTextView = new TextView(mContext);
                serviceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                serviceTextView.setTextColor(mContext.getResources().getColor(R.color.primary_color));
                serviceTextView.setText(service.short_name);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.rightMargin = CommonUtil.dip2px(mContext, 10);

                serviceTextView.setLayoutParams(lp);
                serviceTextView.setBackgroundResource(R.drawable.course_project_services);
                holder.promiseServiceLayout.addView(serviceTextView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCourseProjects.size();
    }
}
