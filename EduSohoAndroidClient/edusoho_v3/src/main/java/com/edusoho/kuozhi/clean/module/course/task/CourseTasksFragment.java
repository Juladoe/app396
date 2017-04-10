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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        CourseProject courseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT_MODEL);
        mPresenter = new CourseTasksPresenter(this, courseProject);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_tasks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        taskRecyclerView = (RecyclerView) view.findViewById(R.id.rv_content);
        mPresenter.subscribe();
    }

    @Override
    public String getBundleKey() {
        return COURSE_PROJECT_MODEL;
    }

    @Override
    public void showCourseTasks(List<TaskItem> taskItems) {
        CourseTaskAdapter adapter = new CourseTaskAdapter(getActivity(), taskItems);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        taskRecyclerView.setAdapter(adapter);
    }
}
