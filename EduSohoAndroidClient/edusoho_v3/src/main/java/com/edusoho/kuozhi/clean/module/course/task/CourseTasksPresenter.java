package com.edusoho.kuozhi.clean.module.course.task;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseTask;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by JesseHuang on 2017/3/26.
 * 任务列表
 */

public class CourseTasksPresenter implements CourseTasksContract.Presenter {

    private String mCourseProjectId;
    private CourseTasksContract.View mView;

    public CourseTasksPresenter(CourseTasksContract.View view, String courseProjectId) {
        mView = view;
        mCourseProjectId = courseProjectId;
    }

    @Override
    public Observable<List<CourseTask>> getCourseTasks(String courseProjectId) {
        return RetrofitService.getTasks(courseProjectId);
    }

    @Override
    public void subscribe() {
        getCourseTasks(mCourseProjectId).subscribe(new Subscriber<List<CourseTask>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<CourseTask> courseTasks) {
                mView.showCourseTasks(courseTasks);
            }
        });
    }

    @Override
    public void unsubscribe() {

    }
}
