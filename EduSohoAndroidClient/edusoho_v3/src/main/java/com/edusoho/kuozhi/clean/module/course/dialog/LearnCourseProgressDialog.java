package com.edusoho.kuozhi.clean.module.course.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.utils.TimeUtils;

/**
 * Created by JesseHuang on 2017/4/14.
 */

public class LearnCourseProgressDialog extends DialogFragment {

    private static final String PROGRESS_INFO = "progress_info";
    private static final String MEMBER_INFO = "member_info";
    private CourseLearningProgress mProgress;
    private CourseMember mMember;
    private TextView mFinishProgress;
    private TextView mPlanProgress;
    private TextView mPlanDeadline;
    private View mCloseDialog;

    public static LearnCourseProgressDialog newInstance(CourseLearningProgress progress, CourseMember member) {
        Bundle args = new Bundle();
        args.putSerializable(PROGRESS_INFO, progress);
        args.putSerializable(MEMBER_INFO, member);
        LearnCourseProgressDialog fragment = new LearnCourseProgressDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_progress_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFinishProgress = (TextView) view.findViewById(R.id.tv_finish_progress);
        mPlanProgress = (TextView) view.findViewById(R.id.tv_plan_progress);
        mPlanDeadline = (TextView) view.findViewById(R.id.tv_plan_deadline);
        mCloseDialog = view.findViewById(R.id.iv_close);
        mCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        init();
    }

    private void init() {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mProgress = (CourseLearningProgress) bundle.getSerializable(PROGRESS_INFO);
            mMember = (CourseMember) bundle.getSerializable(MEMBER_INFO);
        }
        mFinishProgress.setText(String.format(getString(R.string.course_finish_progress), mProgress.taskResultCount, mProgress.taskCount));
        mPlanProgress.setText(String.format(getString(R.string.course_plan_progress), mProgress.planStudyTaskCount, mProgress.taskCount));
        mPlanDeadline.setText(String.format(getString(R.string.course_progress_deadline), TimeUtils.getStringTime(mMember.deadline, "yyyy.MM.dd")));
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


}
