package com.edusoho.kuozhi.clean.module.course.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.widget.ESProgressBar;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by JesseHuang on 2017/5/2.
 */

public class TaskFinishDialog extends DialogFragment {

    public static final String TASK_EVENT = "task_event";

    private View mClose;
    private TextView mCourseTitle;
    private ESProgressBar mProgressBar;
    private TextView mNextTask;
    private TextView mShareCourse;

    private TaskEvent mTaskEvent;

    public static TaskFinishDialog newInstance(TaskEvent taskEvent) {
        Bundle args = new Bundle();
        args.putSerializable(TASK_EVENT, taskEvent);
        TaskFinishDialog fragment = new TaskFinishDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_task_finish, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mClose = view.findViewById(R.id.iv_close);
        mProgressBar = (ESProgressBar) view.findViewById(R.id.pb_progress);
        mCourseTitle = (TextView) view.findViewById(R.id.tv_course_title);
        mShareCourse = (TextView) view.findViewById(R.id.icon_share);
        mNextTask = (TextView) view.findViewById(R.id.tv_next_task);
        init();
    }

    private void init() {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mTaskEvent = (TaskEvent) bundle.getSerializable(TASK_EVENT);
        }
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        HttpUtils.getInstance()
                .createApi(CourseApi.class)
                .getCourseTask(mTaskEvent.result.courseId, mTaskEvent.result.courseTaskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseTask>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CourseTask courseTask) {
                        mCourseTitle.setText(String.format("%d-%d：%s", courseTask.number, courseTask.seq, courseTask.title));
                    }
                });

        HttpUtils.getInstance()
                .createApi(UserApi.class)
                .getMyCourseLearningProgress(mTaskEvent.result.courseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseLearningProgress>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(final CourseLearningProgress courseLearningProgress) {
                        mProgressBar.setProgress(courseLearningProgress.progress);
                        mNextTask.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (courseLearningProgress.nextTask != null) {
                                    Log.d("TaskFinishDialog", "onClick: " + courseLearningProgress.nextTask.title);
                                }
                            }
                        });
                    }
                });
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
