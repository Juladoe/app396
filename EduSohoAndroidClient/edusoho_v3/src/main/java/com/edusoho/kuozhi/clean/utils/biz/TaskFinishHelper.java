package com.edusoho.kuozhi.clean.utils.biz;

import android.content.Context;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.module.course.task.catalog.TaskTypeEnum;
import com.edusoho.kuozhi.v3.EdusohoApp;

import cn.trinea.android.common.util.ToastUtils;
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

    private int mEnableFinish;
    private int mCourseId;
    private CourseTask mCourseTask;
    private ActionListener mActionListener;
    private Context mContext;

    public TaskFinishHelper(Builder builder, Context context) {
        this.mEnableFinish = builder.mEnableFinish;
        this.mCourseId = builder.mCourseId;
        this.mCourseTask = builder.mCourseTask;
        this.mContext = context;
    }

    public TaskFinishHelper setActionListener(ActionListener actionListener) {
        this.mActionListener = actionListener;
        return this;
    }

    public void finish() {
        if (mEnableFinish == 1) {
            onRecord(FINISH);
        } else {
            TaskTypeEnum taskType = TaskTypeEnum.fromString(mCourseTask.type);
            switch (taskType) {
                case VIDEO:
                    String videoLimit = TaskFinishHelper.TIME.equals(mCourseTask.activity.finishType)
                            ? mContext.getString(R.string.task_finish_limit, mCourseTask.activity.finishDetail)
                            : mContext.getString(R.string.video_task_finish_limit);
                    ToastUtils.show(mContext, videoLimit);
                    break;
                case AUDIO:
                    ToastUtils.show(mContext, R.string.audio_task_finish_limit);
                    break;
                case TEXT:
                case DOC:
                    ToastUtils.show(mContext, mContext.getString(R.string.task_finish_limit, mCourseTask.activity.finishDetail));
                    break;
                case PPT:
                    String pptLimit = TaskFinishHelper.TIME.equals(mCourseTask.activity.finishType)
                            ? mContext.getString(R.string.task_finish_limit, mCourseTask.activity.finishDetail)
                            : mContext.getString(R.string.ppt_task_finish_limit);
                    ToastUtils.show(mContext, pptLimit);
                    break;
            }
        }
    }

    public void doing() {
        onRecord(DOING);
    }

    private void onRecord(String status) {
        if (mCourseTask == null) {
            throw new RuntimeException("CourseTask is null");
        }
        if (mActionListener == null) {
            throw new RuntimeException("actionListener cannot be null!");
        }
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .setCourseTaskStatus(mCourseId, mCourseTask.id, status)
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
                            mActionListener.onFinish(taskEvent);
                        }
                    }
                });
    }

    public void invoke() {
        if (mEnableFinish == 0 && TIME.equals(mCourseTask.activity.finishType)) {
            ToastUtils.show(mContext, "doing invoke");
        }
    }

    public static class Builder {
        private int mEnableFinish;
        private int mCourseId;
        private CourseTask mCourseTask;

        public Builder setCourseId(int courseId) {
            this.mCourseId = courseId;
            return this;
        }

        public Builder setCourseTask(CourseTask courseTask) {
            this.mCourseTask = courseTask;
            return this;
        }

        public Builder setEnableFinish(int enableFinish) {
            mEnableFinish = enableFinish;
            return this;
        }

        public TaskFinishHelper build(Context context) {
            return new TaskFinishHelper(this, context);
        }
    }

    public interface ActionListener {
        void onFinish(TaskEvent taskEvent);

        void onError(Throwable e);
    }
}
