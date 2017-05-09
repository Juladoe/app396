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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.course.info.CourseProjectInfoFragment;
import com.edusoho.kuozhi.clean.utils.AppUtils;
import com.edusoho.kuozhi.clean.utils.TimeUtils;
import com.edusoho.kuozhi.v3.util.AppUtil;

/**
 * Created by JesseHuang on 2017/4/24.
 */

public class CourseMenuInfoFragment extends CourseProjectInfoFragment implements CourseMenuInfoContract.MenuCourseInfoView {

    public static final String COURSE_PROJECT_MODEL = "CourseProjectModel";
    public static final String COURSE_PROGRESS = "CourseProgress";
    private CourseMenuInfoContract.MenuCourseInfoPresenter mPresenter;
    private View mBack;
    private View mShare;
    private TextView mCourseTitle;
    private TextView mMyCourseProgress;
    private TextView mCourseProgress;
    private TextView mDeadline;
    private ProgressBar mMyCourseProgressRate;
    private ProgressBar mCourseProgressRate;
    private ImageView mCourseScheduleBackground;

    private CourseProject mCourseProject;
    private CourseLearningProgress mCourseLearningProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT_MODEL);
        mCourseLearningProgress = (CourseLearningProgress) bundle.getSerializable(COURSE_PROGRESS);
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
        mCourseScheduleBackground = (ImageView) view.findViewById(R.id.tv_course_schedule_bg);

        if (CourseProject.ExpiryMode.DATE.toString().equals(mCourseProject.learningExpiryDate.expiryMode)) {
            mCourseProgress.setVisibility(View.VISIBLE);
            mCourseProgressRate.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , AppUtil.dp2px(getActivity(), 260));
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mCourseScheduleBackground.setLayoutParams(lp);
        }

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
        showCourseProgress(mCourseLearningProgress);
        mPresenter = new CourseMenuInfoPresenter(this, mCourseProject, mCourseLearningProgress);
        mPresenter.subscribe();
    }

    private void showCourseProgress(CourseLearningProgress progress) {
        try {
            mMyCourseProgress.setText(String.format(getString(R.string.task_finish_progress), progress.taskResultCount, progress.taskCount));
            mMyCourseProgressRate.setProgress(progress.taskCount == 0 ? 0 : progress.taskResultCount * 100 / progress.taskCount);
            mCourseProgress.setText(String.format(getString(R.string.course_plan_progress), progress.planStudyTaskCount, progress.taskCount));
            mCourseProgressRate.setProgress(progress.taskCount == 0 ? 0 : progress.planStudyTaskCount * 100 / progress.taskCount);
            mDeadline.setText(String.format(getString(R.string.course_progress_deadline),
                    "0".equals(progress.member.deadline) ? getString(R.string.permnent_expired) : TimeUtils.getStringTime(progress.member.deadline, "yyyy.MM.dd")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
