package com.edusoho.kuozhi.clean.module.course.task.menu.info;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.course.info.CourseProjectInfoFragment;
import com.edusoho.kuozhi.clean.utils.TimeUtils;

/**
 * Created by JesseHuang on 2017/4/24.
 */

public class CourseMenuInfoFragment extends CourseProjectInfoFragment implements CourseMenuInfoContract.MenuCourseInfoView {

    public static final String COURSE_PROJECT_MODEL = "CourseProjectModel";
    public static final String COURSE_PROGRESS = "CourseProgress";
    public static final String MEMBER_INFO = "member_info";
    private CourseMenuInfoContract.MenuCourseInfoPresenter mPresenter;
    private View mBack;
    private View mShare;
    private TextView mCourseTitle;
    private TextView mMyCourseProgress;
    private TextView mCourseProgress;
    private TextView mDeadline;
    private ProgressBar mMyCourseProgressRate;
    private ProgressBar mCourseProgressRate;

    private CourseProject mCourseProject;
    private CourseLearningProgress mCourseLearningProgress;
    private CourseMember mCourseMember;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT_MODEL);
        mCourseLearningProgress = (CourseLearningProgress) bundle.getSerializable(COURSE_PROGRESS);
        mCourseMember = (CourseMember) bundle.getSerializable(MEMBER_INFO);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        FrameLayout mHeader = (FrameLayout) view.findViewById(R.id.course_progress_layout);
        View header = inflater.inflate(R.layout.menu_course_info_layout, null);
        mHeader.addView(header);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mBack = view.findViewById(R.id.iv_back);
        mShare = view.findViewById(R.id.icon_share);
        mCourseTitle = (TextView) view.findViewById(R.id.tv_course_title);
        mDeadline = (TextView) view.findViewById(R.id.tv_deadline);
        mMyCourseProgress = (TextView) view.findViewById(R.id.tv_my_course_progress);
        mCourseProgress = (TextView) view.findViewById(R.id.tv_course_progress);
        mMyCourseProgressRate = (ProgressBar) view.findViewById(R.id.my_course_progress_rate);
        mCourseProgressRate = (ProgressBar) view.findViewById(R.id.course_progress_rate);

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();

            }
        });

        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mCourseTitle.setText(mCourseProject.title);
        showCourseProgress(mCourseLearningProgress, mCourseMember);
        mPresenter = new CourseMenuInfoPresenter(this, mCourseProject, mCourseLearningProgress, mCourseMember);
        mPresenter.subscribe();
    }

    private void showCourseProgress(CourseLearningProgress progress, CourseMember courseMember) {
        mMyCourseProgress.setText(String.format(getString(R.string.course_finish_progress), progress.taskResultCount, progress.taskCount));
        mMyCourseProgressRate.setProgress(progress.taskResultCount * 100 / progress.taskCount);
        mCourseProgress.setText(String.format(getString(R.string.course_plan_progress), progress.planStudyTaskCount, progress.taskCount));
        mCourseProgressRate.setProgress(progress.planStudyTaskCount * 100 / progress.taskCount);
        mDeadline.setText(String.format(getString(R.string.course_progress_deadline),
                "0".equals(courseMember.deadline) ? getString(R.string.permnent_expired) : TimeUtils.getStringTime(courseMember.deadline, "yyyy.MM.dd")));
    }
}
