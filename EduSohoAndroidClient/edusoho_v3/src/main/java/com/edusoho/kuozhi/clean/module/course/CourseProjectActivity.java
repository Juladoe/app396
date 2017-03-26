package com.edusoho.kuozhi.clean.module.course;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.edusoho.kuozhi.R;

/**
 * Created by JesseHuang on 2017/3/22.
 */

public class CourseProjectActivity extends AppCompatActivity implements CourseProjectContract.View {

    private CourseProjectContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
    }

    @Override
    public void showTasksCover() {

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setPresenter(CourseProjectContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
