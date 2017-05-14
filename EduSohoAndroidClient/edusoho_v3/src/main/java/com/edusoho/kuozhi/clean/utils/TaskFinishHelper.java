package com.edusoho.kuozhi.clean.utils;

import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.bean.TaskFinishType;
import com.edusoho.kuozhi.clean.bean.TaskResultEnum;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by JesseHuang on 2017/5/2.
 */

public class TaskFinishHelper {
    public static final int CYCLE_TIME = 1000 * 60;
    private static TaskFinishHelper instance;
    private int mCourseId;
    private CourseTask mCourseTask;
    private TaskFinishType mFinishType = TaskFinishType.TIME;
    public TaskResultEnum mTaskStatus = TaskResultEnum.START;
    private Timer mTimer = new Timer();

    public static TaskFinishHelper getInstance() {
        if (instance == null) {
            synchronized (TaskFinishHelper.class) {
                if (instance == null) {
                    instance = new TaskFinishHelper();
                }
            }
        }
        return instance;
    }

    public TaskFinishHelper setCourseId(int courseId) {
        this.mCourseId = courseId;
        return this;
    }

    public TaskFinishHelper addTask(CourseTask task) {
        mCourseTask = task;
        return instance;
    }

    public TaskFinishHelper addFinishType(TaskFinishType finishType) {
        mFinishType = finishType;
        return instance;
    }

    public void start() {
        if (mCourseTask != null && mFinishType == TaskFinishType.TIME) {
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    submitTaskStatus();
                }
            }, CYCLE_TIME);
        }
    }

    public void finishTask() {
        mTaskStatus = TaskResultEnum.FINISH;
        submitTaskStatus();
    }

    public void stop() {
        mTimer.cancel();
    }

    private void submitTaskStatus() {
        HttpUtils.getInstance()
                .createApi(CourseApi.class)
                .setCourseTaskFinish(mCourseId, mCourseTask.id)
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
                        if (TaskResultEnum.FINISH.toString().equals(taskEvent.result.status)) {
                            mTimer.cancel();
                        }
                    }
                });
    }

    public static class Builder {
        private Timer mTimer;
        private CourseTask mCourseTask;
        private TaskFinishType mFinishType = TaskFinishType.TIME;

        public Builder() {
            mTimer = new Timer();
        }

        public void addTask(CourseTask courseTask) {
            this.mCourseTask = courseTask;
        }

        public void addTaskFinishType(TaskFinishType finishType) {
            mFinishType = finishType;
        }

        public void Build() {

        }

    }
}
