package com.edusoho.kuozhi.clean.module.course.task;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.widget.ESIconView;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseTasksFragment extends Fragment implements CourseTasksContract.View {

    private CourseTasksContract.Presenter mPresenter;
    private RecyclerView taskRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_tasks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        taskRecyclerView = (RecyclerView) view.findViewById(R.id.rv_content);
        mPresenter = new CourseTasksPresenter(this, "1");
        mPresenter.subscribe();
    }

    @Override
    public void showCourseTasks(List<CourseTask> courseTasks) {
        CourseTaskAdapter adapter = new CourseTaskAdapter(getActivity(), courseTasks);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        taskRecyclerView.setAdapter(adapter);
    }

    @Override
    public void setPresenter(CourseTasksContract.Presenter presenter) {

    }

    public static class CourseTaskViewHolder extends RecyclerView.ViewHolder {
        public ESIconView taskType;
        public TextView taskName;
        public TextView isTaskFree;
        public TextView taskTime;

        public CourseTaskViewHolder(View view) {
            super(view);
            taskType = (ESIconView) view.findViewById(R.id.ev_task_type);
            taskName = (TextView) view.findViewById(R.id.tv_task_name);
            isTaskFree = (TextView) view.findViewById(R.id.tv_free_task);
            taskTime = (TextView) view.findViewById(R.id.tv_task_time);
        }
    }

    public static class CourseTaskChapterViewHolder extends RecyclerView.ViewHolder {
        public TextView chapterNum;
        public TextView chapterName;

        public CourseTaskChapterViewHolder(View view) {
            super(view);
            chapterNum = (TextView) view.findViewById(R.id.tv_chapter_num);
            chapterName = (TextView) view.findViewById(R.id.tv_chapter_name);
        }
    }
}
