package com.edusoho.kuozhi.clean.module.course;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
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
import rx.schedulers.Schedulers;


/**
 * Created by JesseHuang on 2017/3/23.
 */

public class CourseProjectPresenter implements CourseProjectContract.Presenter {

    private static final String IS_JOIN_SUCCESS = "success";
    private static final float FREE_PRICE = 0f;
    private CourseProjectContract.View mView;
    private int mCourseProjectId;
    private Teacher mTeacher;
    private CourseLearningProgress mProgress;
    private CourseMember mMember;
    private CourseProject mCourseProject;

    public CourseProjectPresenter(int courseProjectId, CourseProjectContract.View view) {
        mCourseProjectId = courseProjectId;
        mView = view;
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
                .subscribe(new Subscriber<CourseProject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CourseProject courseProject) {
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
        initTrialFirstTask(mCourseProjectId);
    }

    private void initTrialFirstTask(final int courseId) {
        HttpUtils.getInstance()
                .createApi(CourseApi.class)
                .getTrialFirstTask(courseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseTask>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CourseTask trialTask) {
                        if (trialTask != null && trialTask.id != 0) {
                            mView.setTrialTaskVisible(true);
                            mView.initTrailTask(trialTask);
                        } else {
                            mView.setTrialTaskVisible(false);
                        }
                    }
                });
    }

    @Override
    public void joinCourseProject(final int courseId) {
        if (mCourseProject.originPrice == FREE_PRICE) {
            joinFreeOrVipCourse(courseId, "free");
        } else if (EdusohoApp.app.loginUser.vip != null && EdusohoApp.app.loginUser.vip.levelId >= mCourseProject.vipLevelId) {
            joinFreeOrVipCourse(courseId, "vip");
        } else {
            mView.launchConfirmOrderActivity(mCourseProject.courseSet.id, courseId);
        }
    }

    @Override
    public void exitCourse() {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .exitCourse(mCourseProjectId)
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
                            mView.showToast(R.string.exit_course_success);
                            mView.exitCourseLayout();
                        } else {
                            mView.showToast(R.string.exit_course_failure);
                        }
                    }
                });
    }

    @Override
    public void showCourseProgressInfo() {
        mView.launchDialogProgress(mProgress, mMember);
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
                        boolean isLearning = member.user != null;
                        if (isLearning) {
                            mView.showFragments(initCourseModules(true), courseProject);
                            mView.initLearnLayout();
                            setCourseLearningProgress(courseProject.id);
                            if (courseProject.learningExpiryDate.expired) {
                                mView.showExitDialog(CourseProjectActivity.DialogType.COURSE_EXPIRED);
                            } else if (isCourseMemberExpired(member.deadline)) {
                                mView.showExitDialog(CourseProjectActivity.DialogType.COURSE_MEMBER_EXPIRED);
                            }
                            // TODO: 2017/4/20 还需要处理vip过期问题
                        } else {
                            mView.showFragments(initCourseModules(false), courseProject);
                            if (courseProject.learningExpiryDate.expired) {
                                mView.setJoinButton(false);
                            }
                        }
                    }
                });
    }

    private void initLogoutCourseMemberStatus(final CourseProject courseProject) {
        mView.showFragments(initCourseModules(false), courseProject);
        if (courseProject.learningExpiryDate.expired) {
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
                        mView.setTrialTaskVisible(true);
                        mView.initNextTask(courseLearningProgress.nextTask);
                    }
                });
    }

    private void joinFreeOrVipCourse(final int courseId, String joinWay) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .joinFreeOrVipCourse(courseId)
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
                        if (courseMember != null) {
                            mView.showToast(R.string.join_course_success);
                            mView.initJoinCourseLayout();
                            setCourseLearningProgress(courseId);
                        }
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

    private boolean isCourseMemberExpired(String deadline) {
        return !CommonConstant.EXPIRED_MODE_FOREVER.equals(deadline) && TimeUtils.getUTCtoDate(deadline).compareTo(new Date()) < 0;
    }
}
