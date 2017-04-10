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
import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.TaskItem;
import com.edusoho.kuozhi.clean.module.course.CourseProjectFragmentListener;
import com.edusoho.kuozhi.clean.widget.ESIconView;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseTasksFragment extends Fragment implements
        CourseTasksContract.View, CourseProjectFragmentListener {

    private static final String COURSE_PROJECT_MODEL = "CourseProjectModel";
    private CourseTasksContract.Presenter mPresenter;
    private RecyclerView taskRecyclerView;

    @Override
    public String getBundleKey() {
        return COURSE_PROJECT_MODEL;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_tasks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        taskRecyclerView = (RecyclerView) view.findViewById(R.id.rv_content);
        Bundle bundle = getArguments();
        CourseProject courseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT_MODEL);
        mPresenter = new CourseTasksPresenter(this, courseProject);
        mPresenter.subscribe();
    }

    @Override
    public void showCourseTasks(List<TaskItem> taskItems) {
        CourseTaskAdapter adapter = new CourseTaskAdapter(getActivity(), taskItems);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        taskRecyclerView.setAdapter(adapter);
    }

    public static class CourseTaskViewHolder extends RecyclerView.ViewHolder {
        public ESIconView taskType;
        public TextView taskName;
        public TextView taskDuration;
        public TextView taskIsFree;

        public CourseTaskViewHolder(View view) {
            super(view);
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
