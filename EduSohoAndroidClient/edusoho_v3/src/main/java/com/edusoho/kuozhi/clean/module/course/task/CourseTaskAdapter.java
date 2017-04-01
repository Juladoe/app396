package com.edusoho.kuozhi.clean.module.course.task;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseTask;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/28.
 */

public class CourseTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int CHAPTER = 0;
    private static final int TASK = 1;
    private List<CourseTask> courseTasks;
    private Context mContext;

    public CourseTaskAdapter(Context context, List<CourseTask> courseTasks) {
        this.courseTasks = courseTasks;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if ("chapter".equals(courseTasks.get(position).itemType)) {
            return CHAPTER;
        }
        return TASK;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CHAPTER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_course_task_chapter, parent, false);
            return new CourseTasksFragment.CourseTaskChapterViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_course_task, parent, false);
            return new CourseTasksFragment.CourseTaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CourseTask task = courseTasks.get(position);
        if (holder instanceof CourseTasksFragment.CourseTaskChapterViewHolder) {
            CourseTasksFragment.CourseTaskChapterViewHolder chapterHolder = (CourseTasksFragment.CourseTaskChapterViewHolder) holder;
            chapterHolder.chapterNum.setText(task.chapterNum);
            chapterHolder.chapterName.setText(task.chapterTitle);
        } else if (holder instanceof CourseTasksFragment.CourseTaskViewHolder) {
            CourseTasksFragment.CourseTaskViewHolder taskHolder = (CourseTasksFragment.CourseTaskViewHolder) holder;
            taskHolder.isTaskFree.setVisibility(task.isTaskFree == "1" ? View.VISIBLE : View.INVISIBLE);
            taskHolder.taskName.setText(task.taskTitle);
            taskHolder.taskTime.setText(task.taskTime);
        }
    }

    @Override
    public int getItemCount() {
        return courseTasks.size();
    }
}
