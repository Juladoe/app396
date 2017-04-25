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
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.course.CourseProjectFragmentListener;
import com.edusoho.kuozhi.clean.module.course.dialog.LearnCourseProgressDialog;
import com.edusoho.kuozhi.clean.module.course.task.menu.info.CourseMenuInfoFragment;
import com.edusoho.kuozhi.clean.widget.CourseMenuButton;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.ESProgressBar;
import com.edusoho.kuozhi.clean.widget.FragmentPageActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseTasksFragment extends BaseFragment<CourseTasksContract.Presenter> implements
        CourseTasksContract.View, CourseProjectFragmentListener {

    private static final String COURSE_PROJECT_MODEL = "CourseProjectModel";
    private CourseTasksContract.Presenter mPresenter;
    private RecyclerView taskRecyclerView;
    private FloatingActionButton mMenuButton;
    private TextView mMenuClose;
    private View mCourseMenuLayout;
    private CourseMenuButton mCourseInfo;
    private ESProgressBar mLearnProgressRate;
    private ESIconView mCourseProgressInfo;
    private CourseLearningProgress mCourseLearningProgress;
    private CourseMember mCourseMember;

    private CourseProject mCourseProject;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT_MODEL);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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
        mCourseInfo = (CourseMenuButton) view.findViewById(R.id.btn_course_menu_info);
        mLearnProgressRate = (ESProgressBar) view.findViewById(R.id.pb_learn_progress);
        mCourseProgressInfo = (ESIconView) view.findViewById(R.id.icon_progress_info);

        mCourseProgressInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LearnCourseProgressDialog.newInstance(mCourseLearningProgress,
                        mCourseMember).show(getActivity().getSupportFragmentManager(), "LearnCourseProgressDialog");
            }
        });
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

        mCourseInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(CourseMenuInfoFragment.COURSE_PROJECT_MODEL, mCourseProject);
                bundle.putSerializable(CourseMenuInfoFragment.COURSE_PROGRESS, mCourseLearningProgress);
                bundle.putSerializable(CourseMenuInfoFragment.MEMBER_INFO, mCourseMember);
                FragmentPageActivity.launchFragmentPageActivity(getActivity(), CourseMenuInfoFragment.class.getName(), bundle);
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

    @Subscribe
    public void onReceiveJoinMessage(List<MessageEvent> list) {
        mCourseLearningProgress = (CourseLearningProgress) list.get(0).getMessageBody();
        mCourseMember = (CourseMember) list.get(1).getMessageBody();
        mLearnProgressRate.setProgress(mCourseLearningProgress.progress);
    }
}
