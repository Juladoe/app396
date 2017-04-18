package com.edusoho.kuozhi.clean.module.course.task;

import android.util.Log;

import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.TaskItem;
import com.edusoho.kuozhi.clean.http.HttpUtils;

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

    private CourseTasksContract.View mView;
    private CourseProject mCourseProject;

    public CourseTasksPresenter(CourseTasksContract.View view, CourseProject courseProject) {
        mView = view;
        mCourseProject = courseProject;
    }

    @Override
    public void subscribe() {
        HttpUtils.getInstance().createApi(CourseApi.class)
                .getCourseItems(mCourseProject.id)
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
                        mView.showCourseTasks(taskItems);
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }
}
