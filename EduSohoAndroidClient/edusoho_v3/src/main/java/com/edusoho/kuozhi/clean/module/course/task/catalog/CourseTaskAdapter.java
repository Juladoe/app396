package com.edusoho.kuozhi.clean.module.course.task.catalog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSetting;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.TaskResultEnum;
import com.edusoho.kuozhi.clean.bean.innerbean.Result;
import com.edusoho.kuozhi.clean.utils.SharedPreferencesHelper;
import com.edusoho.kuozhi.clean.widget.ESIconView;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/28.
 */

public class CourseTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String CLICKED = "clicked";
    private List<CourseItem> mTaskItems;
    private CourseProject.LearnMode mLearnMode;
    private boolean mIsJoin;
    private Context mContext;
    private CourseTaskViewHolder mLastCourseTaskViewHolder;
    private CourseTask mCurrentCourseTask;

    public CourseTaskAdapter(Context context, List<CourseItem> taskItems, CourseProject.LearnMode mode, boolean isJoin) {
        this.mTaskItems = taskItems;
        this.mContext = context;
        this.mLearnMode = mode;
        this.mIsJoin = isJoin;
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
            String chapterName = SharedPreferencesHelper.getInstance(mContext)
                    .open(CourseSetting.COURSE_SETTING)
                    .getString(CourseSetting.CHAPTER_NAME_KEY);
            chapterHolder.chapterTitle.setText(String.format(mContext.getString(R.string.course_project_chapter)
                    , taskItem.number, chapterName, taskItem.title));
        } else if (holder instanceof CourseTaskUnitViewHolder) {
            CourseTaskUnitViewHolder unitHolder = (CourseTaskUnitViewHolder) holder;
            String partName = SharedPreferencesHelper.getInstance(mContext)
                    .open(CourseSetting.COURSE_SETTING)
                    .getString(CourseSetting.PART_NAME_KEY);
            unitHolder.unitTitle.setText(String.format(mContext.getString(R.string.course_project_unit), taskItem.number, partName, taskItem.title));
        } else {
            CourseTaskViewHolder taskHolder = (CourseTaskViewHolder) holder;
            if (mIsJoin) {
                setTaskStatusIcon(taskHolder, mLearnMode, taskItem);
            } else {
                taskHolder.taskStatus.setImageResource(
                        mLearnMode == CourseProject.LearnMode.FREEMODE ? R.drawable.lesson_status : R.drawable.lesson_status_lock);
            }
            taskHolder.taskName.setText(String.format(mContext.getString(R.string.course_project_task_item_name), taskItem.toTaskItemSequence(), taskItem.title));
            taskHolder.taskDuration.setText(taskItem.task.length);
            taskHolder.taskIsFree.setVisibility(taskItem.task.isFree == 1 ? View.VISIBLE : View.GONE);
            taskHolder.taskType.setText(getTaskIconResId(taskItem.task.type));
        }
    }

    private void setTaskStatusIcon(CourseTaskViewHolder holder, CourseProject.LearnMode mode, CourseItem taskItem) {
        if (mode == CourseProject.LearnMode.FREEMODE) {
            if (mCurrentCourseTask != null && taskItem.task.id == mCurrentCourseTask.id) {
                holder.taskType.setTextColor(mContext.getResources().getColor(R.color.primary_color));
                holder.taskName.setTextColor(mContext.getResources().getColor(R.color.primary_color));
                holder.taskDuration.setTextColor(mContext.getResources().getColor(R.color.primary_color));
            } else {
                holder.taskType.setTextColor(mContext.getResources().getColor(R.color.secondary2_font_color));
                holder.taskName.setTextColor(mContext.getResources().getColor(R.color.secondary_font_color));
                holder.taskDuration.setTextColor(mContext.getResources().getColor(R.color.secondary_font_color));
            }
            setTaskResult(holder, taskItem.task.result);
        } else {
            if (taskItem.task.lock) {
                holder.taskStatus.setImageResource(R.drawable.lesson_status_lock);
                holder.taskType.setTextColor(mContext.getResources().getColor(R.color.disabled_hint_color));
                holder.taskName.setTextColor(mContext.getResources().getColor(R.color.disabled_hint_color));
                holder.taskDuration.setTextColor(mContext.getResources().getColor(R.color.disabled_hint_color));
            } else {
                setTaskResult(holder, taskItem.task.result);
                holder.taskType.setTextColor(mContext.getResources().getColor(R.color.secondary2_font_color));
                holder.taskName.setTextColor(mContext.getResources().getColor(R.color.secondary_font_color));
                holder.taskDuration.setTextColor(mContext.getResources().getColor(R.color.secondary_font_color));
            }
        }
    }

    private void setTaskResult(CourseTaskViewHolder holder, Result result) {
        if (result == null) {
            holder.taskStatus.setImageResource(R.drawable.lesson_status);
        } else if (TaskResultEnum.FINISH.toString().equals(result.status)) {
            holder.taskStatus.setImageResource(R.drawable.lesson_status_finish);
        } else if (TaskResultEnum.START.toString().equals(result.status)) {
            holder.taskStatus.setImageResource(R.drawable.lesson_status_learning);
        }
    }

    void switchClickPosition(View currentClickView, CourseItem courseItem) {
        CourseTaskViewHolder taskViewHolder = new CourseTaskViewHolder(currentClickView);
        if (mLastCourseTaskViewHolder != null) {
            //taskViewHolder.taskLock.setTextColor(mContext.getResources().getColor(R.color.disabled_hint_color));
            mLastCourseTaskViewHolder.taskType.setTextColor(mContext.getResources().getColor(R.color.secondary2_font_color));
            mLastCourseTaskViewHolder.taskName.setTextColor(mContext.getResources().getColor(R.color.secondary_font_color));
            mLastCourseTaskViewHolder.taskDuration.setTextColor(mContext.getResources().getColor(R.color.secondary_font_color));
        }
        //taskViewHolder.taskLock.setTextColor(mContext.getResources().getColor(R.color.primary_color));
        taskViewHolder.taskType.setTextColor(mContext.getResources().getColor(R.color.primary_color));
        taskViewHolder.taskName.setTextColor(mContext.getResources().getColor(R.color.primary_color));
        taskViewHolder.taskDuration.setTextColor(mContext.getResources().getColor(R.color.primary_color));
        mLastCourseTaskViewHolder = taskViewHolder;
        mCurrentCourseTask = courseItem.task;
    }

    @Override
    public int getItemCount() {
        return mTaskItems.size();
    }

    private int getTaskIconResId(String type) {
        TaskTypeEnum icon = TaskTypeEnum.fromString(type);
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
                return R.string.task_discuss;
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

    public CourseItem getItem(int position) {
        return mTaskItems.get(position);
    }

    static class CourseTaskViewHolder extends RecyclerView.ViewHolder {
        //ESIconView taskLock;
        ESIconView taskType;
        TextView taskName;
        TextView taskDuration;
        TextView taskIsFree;
        ImageView taskStatus;

        CourseTaskViewHolder(View view) {
            super(view);
            //taskLock = (ESIconView) view.findViewById(R.id.ev_task_lock);
            taskType = (ESIconView) view.findViewById(R.id.ev_task_type);
            taskName = (TextView) view.findViewById(R.id.tv_task_name);
            taskDuration = (TextView) view.findViewById(R.id.tv_task_duration);
            taskIsFree = (TextView) view.findViewById(R.id.tv_task_is_free);
            taskStatus = (ImageView) view.findViewById(R.id.iv_task_status);
        }
    }

    static class CourseTaskUnitViewHolder extends RecyclerView.ViewHolder {
        TextView unitTitle;

        CourseTaskUnitViewHolder(View view) {
            super(view);
            unitTitle = (TextView) view.findViewById(R.id.tv_unit_title);
        }
    }

    static class CourseTaskChapterViewHolder extends RecyclerView.ViewHolder {
        TextView chapterTitle;

        CourseTaskChapterViewHolder(View view) {
            super(view);
            chapterTitle = (TextView) view.findViewById(R.id.tv_chapter_title);
        }
    }
}
