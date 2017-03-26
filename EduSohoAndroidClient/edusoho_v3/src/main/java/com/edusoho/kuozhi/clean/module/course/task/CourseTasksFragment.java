package com.edusoho.kuozhi.clean.module.course.task;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseTasksFragment extends Fragment implements CourseTasksContract.View {


    @Override
    public void showLearningProgress() {

    }

    @Override
    public void showCourseTasks() {

    }

    @Override
    public void setPresenter(CourseTasksContract.Presenter presenter) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_tasks, container, false);
    }
}
