package com.edusoho.kuozhi.clean.module.course.task;

import android.util.Log;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.TaskItem;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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

    public Observable<List<CourseItem>> getCourseItems(String courseId) {
        return RetrofitService.getTasks(courseId);
    }

    @Override
    public void subscribe() {
        getCourseItems(mCourseProjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<List<CourseItem>, Observable<CourseItem>>() {
                    @Override
                    public Observable<CourseItem> call(List<CourseItem> courseItems) {
                        return Observable.from(courseItems);
                    }
                })
                .flatMap(new Func1<CourseItem, Observable<TaskItem>>() {
                    @Override
                    public Observable<TaskItem> call(CourseItem courseItem) {
                        return Observable.from(courseItem.toTaskItems());
                    }
                })
                .toList()
                .subscribe(new Subscriber<List<TaskItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<TaskItem> taskItems) {
                        Log.d("getCourseI1tems", "onNext: " + taskItems.toString());
                        mView.showCourseTasks(taskItems);
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }
}
