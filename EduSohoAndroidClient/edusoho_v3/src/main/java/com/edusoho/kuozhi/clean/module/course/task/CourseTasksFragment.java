package com.edusoho.kuozhi.clean.module.course.task;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.course.CourseProjectFragmentListener;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseTasksFragment extends BaseFragment<CourseTasksContract.Presenter> implements
        CourseTasksContract.View, CourseProjectFragmentListener {

    private static final String COURSE_PROJECT_MODEL = "CourseProjectModel";
    private static final String COURSE_IS_JOIN = "CourseLearn";
    private CourseTasksContract.Presenter mPresenter;
    private RecyclerView taskRecyclerView;
    private FloatingActionButton mMenuButton;
    private TextView mMenuClose;
    private View mCourseMenuLayout;
    private CourseProject mCourseProject;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT_MODEL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_tasks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        taskRecyclerView = (RecyclerView) view.findViewById(R.id.rv_content);
        mMenuButton = (FloatingActionButton) view.findViewById(R.id.floating_button);
        mMenuClose = (TextView) view.findViewById(R.id.tv_close_menu);
        mCourseMenuLayout = view.findViewById(R.id.bottom_menu_layout);
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(mCourseMenuLayout);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomMenu(behavior);
            }
        });

        mMenuClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomMenu(behavior);
            }
        });

        mPresenter = new CourseTasksPresenter(this, mCourseProject, isJoin());
        mPresenter.subscribe();
    }

    private boolean isJoin() {
        if (getActivity() != null && getActivity() instanceof CourseProjectActivity) {
            return ((CourseProjectActivity) getActivity()).isJoin();
        }
        return false;
    }

    @Override
    public void showCourseMenuButton(boolean show) {
        mMenuButton.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showBottomMenu(BottomSheetBehavior behavior) {
        if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            mMenuButton.setVisibility(View.GONE);
        } else if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            mMenuButton.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMenuButton.setVisibility(View.VISIBLE);
                }
            }, 250);
        }
    }

    @Override
    public String getBundleKey() {
        return COURSE_PROJECT_MODEL;
    }

    @Override
    public void showCourseTasks(List<CourseItem> taskItems) {
        CourseTaskAdapter adapter = new CourseTaskAdapter(getActivity(), taskItems);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        taskRecyclerView.setAdapter(adapter);
    }
}
