package com.edusoho.kuozhi.clean.module.course.task;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.widget.ESIconView;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/28.
 */

public class CourseTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CourseItem> mTaskItems;
    private CourseProject.LearnMode mLearnMode;
    private Context mContext;

    public CourseTaskAdapter(Context context, List<CourseItem> taskItems, CourseProject.LearnMode mode) {
        this.mTaskItems = taskItems;
        this.mContext = context;
        this.mLearnMode = mode;
    }

    @Override
    public int getItemViewType(int position) {
        if (CourseItemEnum.CHAPTER.toString().equals(mTaskItems.get(position).type)) {
            return CourseItemEnum.CHAPTER.getIndex();
        } else if (CourseItemEnum.UNIT.toString().equals(mTaskItems.get(position).type)) {
            return CourseItemEnum.UNIT.getIndex();
        } else {
            return CourseItemEnum.TASK.getIndex();
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
        CourseItem taskItem = mTaskItems.get(position);
        if (holder instanceof CourseTaskChapterViewHolder) {
            CourseTaskChapterViewHolder chapterHolder = (CourseTaskChapterViewHolder) holder;
            chapterHolder.chapterTitle.setText(String.format(mContext.getString(R.string.course_project_chapter), taskItem.number, taskItem.title));
        } else if (holder instanceof CourseTaskUnitViewHolder) {
            CourseTaskUnitViewHolder unitHolder = (CourseTaskUnitViewHolder) holder;
            unitHolder.unitTitle.setText(String.format(mContext.getString(R.string.course_project_unit), taskItem.number, taskItem.title));
        } else {
            CourseTaskViewHolder taskHolder = (CourseTaskViewHolder) holder;
            setTaskLockLayout(taskHolder, mLearnMode, taskItem);
            taskHolder.taskName.setText(String.format(mContext.getString(R.string.course_project_task_item_name), taskItem.toTaskItemSequence(), taskItem.title));
            taskHolder.taskDuration.setText(taskItem.task.length);
            taskHolder.taskIsFree.setVisibility(taskItem.task.isFree == 1 ? View.VISIBLE : View.GONE);
            taskHolder.taskType.setText(getTaskIconResId(taskItem.task.type));
        }
    }

    private void setTaskLockLayout(CourseTaskViewHolder holder, CourseProject.LearnMode mode, CourseItem taskItem) {
        if (mode == CourseProject.LearnMode.FREEMODE) {
            holder.taskLock.setVisibility(View.GONE);
        } else {
            holder.taskLock.setVisibility(View.VISIBLE);
            if (taskItem.task.lock) {
                holder.taskLock.setText(R.string.course_task_lock);
                holder.taskType.setTextColor(mContext.getResources().getColor(R.color.disabled_hint_color));
                holder.taskName.setTextColor(mContext.getResources().getColor(R.color.disabled_hint_color));
                holder.taskDuration.setTextColor(mContext.getResources().getColor(R.color.disabled_hint_color));
            } else {
                holder.taskLock.setText(R.string.course_task_unlock);
                holder.taskType.setTextColor(mContext.getResources().getColor(R.color.secondary2_font_color));
                holder.taskName.setTextColor(mContext.getResources().getColor(R.color.secondary_font_color));
                holder.taskDuration.setTextColor(mContext.getResources().getColor(R.color.secondary_font_color));
            }
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
            case DISCUSS:
                return R.string.discuss;
            case FLASH:
                return R.string.task_flash;
            case DOC:
                return R.string.task_doc;
            case PPT:
                return R.string.task_ppt;
            case TESTPAPER:
                return R.string.task_testpaper;
            case HOMEWORK:
                return R.string.task_homework;
            case EXERCISE:
                return R.string.task_exercise;
            case DOWNLOAD:
                return R.string.task_download;
            default:
                return R.string.task_download;
        }
    }

    public static class CourseTaskViewHolder extends RecyclerView.ViewHolder {
        public ESIconView taskLock;
        public ESIconView taskType;
        public TextView taskName;
        public TextView taskDuration;
        public TextView taskIsFree;

        public CourseTaskViewHolder(View view) {
            super(view);
            taskLock = (ESIconView) view.findViewById(R.id.ev_task_lock);
            taskType = (ESIconView) view.findViewById(R.id.ev_task_type);
            taskName = (TextView) view.findViewById(R.id.tv_task_name);
            taskDuration = (TextView) view.findViewById(R.id.tv_task_duration);
            taskIsFree = (TextView) view.findViewById(R.id.tv_task_is_free);
        }
    }

    public static class CourseTaskUnitViewHolder extends RecyclerView.ViewHolder {
        public TextView unitTitle;

        public CourseTaskUnitViewHolder(View view) {
            super(view);
            unitTitle = (TextView) view.findViewById(R.id.tv_unit_title);
        }
    }

    public static class CourseTaskChapterViewHolder extends RecyclerView.ViewHolder {
        public TextView chapterTitle;

        public CourseTaskChapterViewHolder(View view) {
            super(view);
            chapterTitle = (TextView) view.findViewById(R.id.tv_chapter_title);
        }
    }
}
