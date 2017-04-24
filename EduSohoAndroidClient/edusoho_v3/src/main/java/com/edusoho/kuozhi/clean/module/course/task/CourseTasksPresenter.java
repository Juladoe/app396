package com.edusoho.kuozhi.clean.module.course.task;

import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.http.HttpUtils;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by JesseHuang on 2017/3/26.
 * 任务列表
 */

public class CourseTasksPresenter implements CourseTasksContract.Presenter {

    private CourseTasksContract.View mView;
    private CourseProject mCourseProject;
    private boolean mIsJoin;

    public CourseTasksPresenter(CourseTasksContract.View view, CourseProject courseProject, boolean isJoin) {
        mView = view;
        mCourseProject = courseProject;
        mIsJoin = isJoin;
    }

    @Override
    public void subscribe() {
        mView.showCourseMenuButton(mIsJoin);
        HttpUtils.getInstance().createApi(CourseApi.class)
                .getCourseItems(mCourseProject.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CourseItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<CourseItem> taskItems) {
                        mView.showCourseTasks(taskItems);
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }
}
