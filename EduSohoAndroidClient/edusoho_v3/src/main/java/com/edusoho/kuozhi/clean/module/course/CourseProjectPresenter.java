package com.edusoho.kuozhi.clean.module.course;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by JesseHuang on 2017/3/23.
 */

public class CourseProjectPresenter implements CourseProjectContract.Presenter {

    private CourseProjectContract.View mView;
    private String mCourseProjectId;

    public CourseProjectPresenter(String courseProjectId, CourseProjectContract.View view) {
        mCourseProjectId = courseProjectId;
        mView = view;
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
        getCourseProject(mCourseProjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CourseProject>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(CourseProject courseProject) {
                        mView.showFragments(initCourseModules(), courseProject);
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }

    private CourseProjectEnum[] initCourseModules() {
        return new CourseProjectEnum[]{CourseProjectEnum.INFO, CourseProjectEnum.TASKS, CourseProjectEnum.RATE};
    }

    private Observable<CourseProject> getCourseProject(String id) {
        return RetrofitService.getCourseProject(id);
    }

    private Observable<CourseSet> getCourseSet(String id) {
        return RetrofitService.getCourseSet(id);
    }
}
