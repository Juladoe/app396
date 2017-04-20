package com.edusoho.kuozhi.clean.module.course;

import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.utils.CommonConstant;
import com.edusoho.kuozhi.clean.utils.TimeUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by JesseHuang on 2017/3/23.
 */

public class CourseProjectPresenter implements CourseProjectContract.Presenter {

    private static final String IS_JOIN_SUCCESS = "success";
    private static final float FREE_PRICE = 0f;
    private CourseProjectContract.View mView;
    private int mCourseProjectId;
    private CourseProject.Teacher mTeacher;
    private CourseLearningProgress mProgress;
    private CourseMember mMember;
    private CourseProject mCourseProject;

    public CourseProjectPresenter(int courseProjectId, CourseProjectContract.View view) {
        mCourseProjectId = courseProjectId;
        mView = view;
        mCourseProjectId = 33;
    }

    @Override
    public void consult() {
        //这里需要修改，应该要和RxJava结合使用
        mView.launchImChatWithTeacher(mTeacher);
    }

    @Override
    public void subscribe() {
        HttpUtils.getInstance()
                .createApi(CourseApi.class)
                .getCourseProject(mCourseProjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<CourseProject>() {
                    @Override
                    public void call(CourseProject courseProject) {
                        mCourseProject = courseProject;
                        mView.showCover(mCourseProject.courseSet.cover.middle);
                        if (courseProject.teachers.length > 0) {
                            mTeacher = courseProject.teachers[0];
                        }
                        if (EdusohoApp.app.loginUser != null) {
                            initLoginCourseMemberStatus(courseProject);
                        } else {
                            initLogoutCourseMemberStatus(courseProject);
                        }
                    }
                });
    }


    @Override
    public void showCourseProgressInfo() {
        mView.launchDialogProgress(mProgress, mMember);
    }

    @Override
    public void joinCourseProject(final int courseId) {
        if (mCourseProject.originPrice == FREE_PRICE) {
            joinFreeOrVipCourse(courseId, "free");
        } else if (EdusohoApp.app.loginUser.vip != null && EdusohoApp.app.loginUser.vip.levelId >= mCourseProject.vipLevelId) {
            joinFreeOrVipCourse(courseId, "vip");
        } else {
            mView.launchConfirmOrderActivity(mCourseProjectId, courseId);
        }
    }

    private void joinFreeOrVipCourse(final int courseId, String joinWay) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .joinFreeOrVipCourse(courseId, joinWay)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        if (jsonObject.get(IS_JOIN_SUCCESS).getAsBoolean()) {
                            mView.initJoinCourseLayout();
                            setCourseLearningProgress(courseId);
                        }
                    }
                });
    }

    private void initLoginCourseMemberStatus(final CourseProject courseProject) {
        HttpUtils.getInstance()
                .createApi(CourseApi.class)
                .getCourseMember(courseProject.id, EdusohoApp.app.loginUser.id)
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
                    public void onNext(CourseMember member) {
                        mMember = member;
                        if (member.user != null) {
                            if (isCourseExpired(courseProject.expiryEndDate)) {
                                //课程本身已过期，弹出退出框
                            } else if (isCourseMemberExpired(member.deadline)) {
                                //有效期过期，弹出退出框
                            }
                            // TODO: 2017/4/20 还需要处理vip过期问题
                        } else {
                            if (isCourseExpired(courseProject.expiryEndDate)) {
                                mView.setJoinButton(false);
                            }
                        }
                    }
                });
    }

    private void initLogoutCourseMemberStatus(final CourseProject courseProject) {
        if (isCourseExpired(courseProject.expiryEndDate)) {
            mView.setJoinButton(false);
        }
    }

    private void setCourseLearningProgress(int courseId) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getMyCourseLearningProgress(EdusohoApp.app.token, courseId)
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
                        mProgress = courseLearningProgress;
                        mView.setProgressBar(courseLearningProgress.progress);
                    }
                });
    }

    private List<CourseProjectEnum> initCourseModules(boolean isLearning) {
        List<CourseProjectEnum> list = new ArrayList<>();
        if (isLearning) {
            list.add(CourseProjectEnum.TASKS);
        } else {
            list.add(CourseProjectEnum.INFO);
            list.add(CourseProjectEnum.TASKS);
            list.add(CourseProjectEnum.RATE);
        }
        return list;
    }

    @Override
    public void unsubscribe() {

    }

    private boolean isCourseExpired(String expiryEndDate) {
        return TimeUtils.getUTCtoDate(expiryEndDate).compareTo(new Date()) < 1;
    }

    private boolean isCourseDoNotStarted(String expiryStartDate) {
        return TimeUtils.getUTCtoDate(expiryStartDate).compareTo(new Date()) > 0;
    }

    private boolean isCourseMemberExpired(String deadline) {
        return !CommonConstant.EXPIRED_MODE_FOREVER.equals(deadline) && TimeUtils.getUTCtoDate(deadline).compareTo(new Date()) < 0;
    }
}
