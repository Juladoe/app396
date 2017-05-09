package com.edusoho.kuozhi.clean.module.course.task.catalog;

import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
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
        HttpUtils.getInstance().addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .getCourseItems(mCourseProject.id, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<List<CourseItem>>() {
                    @Override
                    public void call(List<CourseItem> courseItems) {
                        mView.showCourseTasks(courseItems, mIsJoin);
                    }
                })
                .flatMap(new Func1<List<CourseItem>, Observable<CourseItem>>() {
                    @Override
                    public Observable<CourseItem> call(List<CourseItem> courseItems) {
                        for (CourseItem courseItem : courseItems) {
                            if (CourseItemEnum.TASK.toString().equals(courseItem.type)) {
                                return Observable.just(courseItem);
                            }
                        }
                        return null;
                    }
                })
                .subscribe(new Subscriber<CourseItem>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(final CourseItem courseItem) {
                        if (courseItem == null) {
                            return;
                        }
                        getCourseLearningProgress(mCourseProject.id)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<CourseLearningProgress>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(CourseLearningProgress courseLearningProgress) {
                                        mView.showLearnProgress(courseLearningProgress);
                                        mView.showNextTaskOnCover(courseLearningProgress.nextTask, courseItem.task.id == courseLearningProgress.nextTask.id);
                                    }
                                });
                    }
                });
    }

    private Observable<CourseLearningProgress> getCourseLearningProgress(int courseId) {
        return HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getMyCourseLearningProgress(courseId);
    }

    @Override
    public void initCourseProgress(CourseLearningProgress progress) {

    }

    @Override
    public void unsubscribe() {

    }
}
