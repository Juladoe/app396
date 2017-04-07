package com.edusoho.kuozhi.clean.module.course.task;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private List<TaskItem> mTaskItems;
    private Context mContext;

    public CourseTaskAdapter(Context context, List<TaskItem> taskItems) {
        this.mTaskItems = taskItems;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (CourseItemEnum.CHAPTER.toString().equals(mTaskItems.get(position).type)) {
            return CourseItemEnum.CHAPTER.getIndex();
        } else if (CourseItemEnum.UNIT.toString().equals(mTaskItems.get(position).type)) {
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
        TaskItem taskItem = mTaskItems.get(position);
        if (holder instanceof CourseTaskChapterViewHolder) {
            CourseTaskChapterViewHolder chapterHolder = (CourseTaskChapterViewHolder) holder;
            chapterHolder.chapterTitle.setText(String.format(mContext.getString(R.string.course_project_chapter), taskItem.number, taskItem.title));
        } else if (holder instanceof CourseTaskUnitViewHolder) {
            CourseTaskUnitViewHolder unitHolder = (CourseTaskUnitViewHolder) holder;
            unitHolder.unitTitle.setText(String.format(mContext.getString(R.string.course_project_unit), taskItem.number, taskItem.title));
        } else if (holder instanceof CourseTaskViewHolder) {
            CourseTaskViewHolder taskHolder = (CourseTaskViewHolder) holder;
            taskHolder.taskName.setText(String.format(mContext.getString(R.string.course_project_task_item_name), taskItem.toTaskSequence(), taskItem.title));
            taskHolder.taskDuration.setText(taskItem.length);
            taskHolder.taskIsFree.setVisibility(taskItem.isFree == 1 ? View.VISIBLE : View.GONE);
            Log.d("onBindViewHolder", "onBindViewHolder: " + taskItem.type);
            taskHolder.taskType.setText(getTaskIconResId(taskItem.type));
        }
    }

    @Override
    public int getItemCount() {
        return mTaskItems.size();
    }

    private int getTaskIconResId(String type) {
        TaskIconEnum icon = TaskIconEnum.fromString(type);
        switch (icon) {
            case TEXT:
                return R.string.task_text;
            case VIDEO:
                return R.string.task_video;
            case AUDIO:
                return R.string.task_audio;
            case LIVE:
                return R.string.task_live;
            case FLASH:
                return R.string.task_flash;
            case DOC:
                return R.string.task_doc;
            case TESTPAPER:
                return R.string.task_testpaper;
            case HOMEWORK:
                return R.string.task_homework;
            case EXERCISE:
                return R.string.task_exercise;
            default:
                return R.string.task_download;
        }
    }
}
