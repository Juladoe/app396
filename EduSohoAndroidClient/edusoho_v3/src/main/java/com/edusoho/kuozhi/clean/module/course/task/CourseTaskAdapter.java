package com.edusoho.kuozhi.clean.module.course.task;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.TaskItem;

import java.util.List;

import static com.edusoho.kuozhi.clean.module.course.task.CourseTasksFragment.CourseTaskChapterViewHolder;
import static com.edusoho.kuozhi.clean.module.course.task.CourseTasksFragment.CourseTaskUnitViewHolder;
import static com.edusoho.kuozhi.clean.module.course.task.CourseTasksFragment.CourseTaskViewHolder;

/**
 * Created by JesseHuang on 2017/3/28.
 */

public class CourseTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TaskItem> mTaskItem;
    private Context mContext;

    public CourseTaskAdapter(Context context, List<TaskItem> taskItems) {
        this.mTaskItem = taskItems;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (CourseItemEnum.CHAPTER.toString().equals(mTaskItem.get(position).type)) {
            return CourseItemEnum.CHAPTER.getIndex();
        } else if (CourseItemEnum.UNIT.toString().equals(mTaskItem.get(position).type)) {
            return CourseItemEnum.UNIT.getIndex();
        } else {
            return CourseItemEnum.LESSON.getIndex();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CourseItemEnum.CHAPTER.getIndex()) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_course_task_chapter, parent, false);
            return new CourseTaskChapterViewHolder(view);
        } else if (viewType == CourseItemEnum.UNIT.getIndex()) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_course_task_unit, parent, false);
            return new CourseTaskUnitViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_course_task, parent, false);
            return new CourseTaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TaskItem taskItem = mTaskItem.get(position);
        if (holder instanceof CourseTaskChapterViewHolder) {
            CourseTaskChapterViewHolder chapterHolder = (CourseTaskChapterViewHolder) holder;
            chapterHolder.chapterTitle.setText(String.format(mContext.getString(R.string.course_project_chapter), taskItem.number, taskItem.title));
        } else if (holder instanceof CourseTaskUnitViewHolder) {
            CourseTaskUnitViewHolder unitHolder = (CourseTaskUnitViewHolder) holder;
            unitHolder.unitTitle.setText(String.format(mContext.getString(R.string.course_project_unit), taskItem.number, taskItem.title));
        } else if (holder instanceof CourseTaskViewHolder) {
            CourseTaskViewHolder taskHolder = (CourseTaskViewHolder) holder;
            taskHolder.taskName.setText(taskItem.toTaskSequence() + " " + taskItem.title);
            taskHolder.taskTime.setText(taskItem.length);
            //taskHolder.taskType
        }
    }

    @Override
    public int getItemCount() {
        return mTaskItem.size();
    }
}
