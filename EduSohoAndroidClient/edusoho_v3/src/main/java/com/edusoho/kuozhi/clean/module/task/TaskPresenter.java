package com.edusoho.kuozhi.clean.module.task;

import android.support.annotation.Nullable;

/**
 * Created by JesseHuang on 2017/3/23.
 */

public class TaskPresenter implements TaskContract.Presenter {

    @Nullable
    private String mTaskId;

    private TaskContract.View mView;

    public TaskPresenter(String taskId, TaskContract.View view) {
        mTaskId = taskId;
        mView = view;
    }

    @Override
    public void getTaskInfo() {

    }

    @Override
    public void getTasks(int taskId) {
        //RetrofitService.getTasks(taskId)
    }

    @Override
    public void learnTask(int taskId) {

    }

    @Override
    public void favorite(int taskId) {

    }

    @Override
    public void consult() {

    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
