package com.edusoho.kuozhi.clean.module.course;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.utils.CommonConstant;
import com.edusoho.kuozhi.clean.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by JesseHuang on 2017/3/23.
 */

public class CourseProjectPresenter implements CourseProjectContract.Presenter {

    private CourseProjectContract.View mView;
    private String mCourseProjectId;
    private CourseProject.Teacher mTeacher;

    public CourseProjectPresenter(String courseProjectId, CourseProjectContract.View view) {
        mCourseProjectId = courseProjectId;
        mView = view;
    }

    @Override
    public void consult() {
        //这里需要修改，应该要和RxJava结合使用
        //mView.launchImChatWithTeacher(mTeacher);
        mView.initLearnedLayout();
    }

    @Override
    public void subscribe() {
        getCourseProject(mCourseProjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<CourseProject>() {
                    @Override
                    public void call(CourseProject courseProject) {
                        if (courseProject.teachers.length > 0) {
                            mTeacher = courseProject.teachers[0];
                        }
                        mView.showFragments(initCourseModules(), courseProject);
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<CourseProject, Observable<CourseSet>>() {
                    @Override
                    public Observable<CourseSet> call(CourseProject courseProject) {
                        return getCourseSet(courseProject.courseSetId);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseSet>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CourseSet courseSet) {
                        mView.showCover(courseSet.cover.large);
                    }
                });

        getCourseMember(mCourseProjectId, "4")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseMember>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CourseMember courseMember) {
                        boolean isLearned = courseMember.user != null && !isExpired(courseMember.deadline);
                        mView.showBottomLayout(!isLearned);
                        mView.showCacheButton(isLearned);
                        mView.showShareButton(!isLearned);
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }

    private boolean isExpired(String utcTime) {
        return !CommonConstant.EXPIRED_MODE_FOREVER.equals(utcTime) && TimeUtils.getUTCtoDate(utcTime).compareTo(new Date()) < 0;
    }

    private List<CourseProjectEnum> initCourseModules() {
        List<CourseProjectEnum> list = new ArrayList<>();
        list.add(CourseProjectEnum.INFO);
        list.add(CourseProjectEnum.TASKS);
        list.add(CourseProjectEnum.RATE);
        return list;
    }

    private Observable<CourseProject> getCourseProject(String id) {
        return RetrofitService.getCourseProject(id);
    }

    private Observable<CourseMember> getCourseMember(String courseId, String userId) {
        return RetrofitService.getCourseMember(courseId, userId);
    }

    private Observable<CourseSet> getCourseSet(String id) {
        return RetrofitService.getCourseSet(id);
    }
}
