package com.edusoho.kuozhi.clean.utils.biz;

import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum;
import com.edusoho.kuozhi.v3.EdusohoApp;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by JesseHuang on 2017/5/11.
 */

public class TaskFinishHelper {

    private static final int INTERVAL_TIME = 60 * 1000;
    public static final String DEFAULT = null;
    public static final String END = "end";
    public static final String TIME = "time";

    public static final String DOING = "doing";
    public static final String FINISH = "finish";

    private String mFinishType;
    private int mCourseId;
    private int mTaskId;
    private TaskTypeEnum mTaskType;
    private ActionListener mActionListener;

    public TaskFinishHelper(Builder builder) {
        this.mFinishType = builder.mFinishType;
        this.mCourseId = builder.mCourseId;
        this.mTaskId = builder.mTaskId;
        this.mTaskType = builder.mTaskType;
        this.mActionListener = builder.mActionListener;
    }

    public void finish() {
        onRecord(FINISH);
    }

    public void doing() {
        onRecord(DOING);
    }

    private void onRecord(String status) {
        if (mCourseId == 0) {
            throw new RuntimeException("CourseId is 0!");
        }
        if (mTaskId == 0) {
            throw new RuntimeException("TaskId is 0!");
        }
        if (mActionListener == null) {
            throw new RuntimeException("actionListener cannot be null!");
        }
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .setCourseTaskStatus(mCourseId, mTaskId, status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TaskEvent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TaskEvent taskEvent) {
                        if (FINISH.equals(taskEvent.result.status)) {
                            mActionListener.doAction(taskEvent);
                        }
                    }
                });
    }

    public void invoke() {

    }


    public static class Builder {
        private String mFinishType;
        private int mCourseId;
        private int mTaskId;
        private TaskTypeEnum mTaskType;
        private int mFinishLimitTime;
        private ActionListener mActionListener;

        public Builder setCourseId(int courseId) {
            mCourseId = courseId;
            return this;
        }

        public Builder setTaskId(int taskId) {
            mTaskId = taskId;
            return this;
        }

        public Builder setFinishType(String finishType) {
            mFinishType = finishType;
            return this;
        }

        public Builder setTaskType(TaskTypeEnum taskType) {
            mTaskType = taskType;
            return this;
        }

        public Builder setFinishLimitTime(int finishLimitTime) {
            mFinishLimitTime = finishLimitTime;
            return this;
        }

        public Builder actionListener(ActionListener actionListener) {
            mActionListener = actionListener;
            return this;
        }

        public TaskFinishHelper build() {
            return new TaskFinishHelper(this);
        }
    }

    public interface ActionListener {
        void doAction(TaskEvent taskEvent);
    }
}
