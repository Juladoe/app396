package com.edusoho.kuozhi.clean.module.task;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.edusoho.kuozhi.R;

/**
 * Created by JesseHuang on 2017/3/22.
 */

public class TaskActivity extends AppCompatActivity implements TaskContract.View {

    private TaskContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
    }

    @Override
    public void showTaskInfo() {

    }

    @Override
    public void showTasks() {

    }

    @Override
    public void showRates() {

    }

    @Override
    public void showTasksCover() {

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setPresenter(TaskContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
