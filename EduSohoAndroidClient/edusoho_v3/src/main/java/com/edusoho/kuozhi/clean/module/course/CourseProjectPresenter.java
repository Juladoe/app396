package com.edusoho.kuozhi.clean.module.course;

import android.content.DialogInterface;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.api.UserApi;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.utils.CommonConstant;
import com.edusoho.kuozhi.clean.utils.CourseHelper;
import com.edusoho.kuozhi.clean.utils.TimeUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

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
    private CourseMember mMember;
    private CourseTask mFirstTrailTask;
    private CourseProject mCourseProject;
    private boolean mIsJoin = false;

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
                .addTokenHeader(EdusohoApp.app.token)
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

                        int errorRes = CourseHelper.getCourseErrorRes(courseProject.access.code);
                        switch (courseProject.access.code) {
                            case CourseHelper.USER_NOT_LOGIN:
                                initLogoutCourseMemberStatus(courseProject);
                                initTrialFirstTask(mCourseProjectId);
                                break;
                            case CourseHelper.COURSE_SUCCESS:
                                initLoginCourseMemberStatus(courseProject);
                                break;
                            case CourseHelper.COURSE_EXPIRED:
                                initLoginCourseMemberStatus(courseProject);
                                mView.setShowError(new ShowDialogHelper().showErrorType(ShowDialogHelper.TYPE_TOAST)
                                        .showErrorMsgResId(errorRes));
                                break;
                            case CourseHelper.COURSE_NOT_BUYABLE:
                            case CourseHelper.COURSE_BUY_EXPIRED:
                                initLogoutCourseMemberStatus(courseProject);
                                initTrialFirstTask(mCourseProjectId);
                                mView.setShowError(new ShowDialogHelper().showErrorType(ShowDialogHelper.TYPE_TOAST)
                                        .showErrorMsgResId(errorRes)
                                        .showError());
                                break;

                        }
                    }
                });
    }

    private void initLoginCourseMemberStatus(final CourseProject courseProject) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getCourseMember(courseProject.id)
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
                        mIsJoin = member.user != null;
                        if (mIsJoin) {
                            mView.showFragments(initCourseModules(true), courseProject);
                            mView.initLearnLayout(CourseProject.LearnMode.getMode(courseProject.learnMode));
                            setCourseLearningProgress(courseProject.id);
                            if (CourseHelper.COURSE_EXPIRED.equals(courseProject.access.code)) {
                                mView.setShowError(new ShowDialogHelper().showErrorType(ShowDialogHelper.TYPE_DIALOG)
                                        .showErrorMsgResId(R.string.course_expired_dialog)
                                        .setAction(ShowDialogHelper.POSITIVE_ACTION_EXIT_COURSE)
                                        .showError());
                                return;
                            }

                            int errRes = CourseHelper.getCourseMemberErrorRes(member.access.code);
                            if (member.access.code.equals(CourseHelper.MEMBER_EXPIRED)) {
                                mView.setShowError(new ShowDialogHelper().showErrorType(ShowDialogHelper.TYPE_DIALOG)
                                        .showErrorMsgResId(R.string.course_member_expired_dialog)
                                        .setAction(ShowDialogHelper.POSITIVE_ACTION_EXIT_COURSE)
                                        .showError());
                            } else if (member.access.code.equals(CourseHelper.COURSE_NOT_ARRIVE)) {
                                mView.setShowError(new ShowDialogHelper().showErrorType(ShowDialogHelper.TYPE_TOAST)
                                        .showErrorMsgResId(errRes)
                                        .showError());
                            } else if (member.access.code.equals(CourseHelper.MEMBER_VIP_EXPIRED)) {
                                mView.setShowError(new ShowDialogHelper().showErrorType(ShowDialogHelper.TYPE_DIALOG)
                                        .showErrorMsgResId(R.string.course_member_vip_expired_dialog)
                                        .setAction(ShowDialogHelper.POSITIVE_ACTION_BUY_VIP)
                                        .showError());
                            } else if (!member.access.code.equals(CourseHelper.MEMBER_SUCCESS)) {
                                mView.setShowError(new ShowDialogHelper().showErrorType(ShowDialogHelper.TYPE_TOAST)
                                        .showErrorMsgResId(errRes)
                                        .showError());
                            }
                        } else {
                            mView.showFragments(initCourseModules(false), courseProject);
                            initTrialFirstTask(mCourseProjectId);
                            if (CourseHelper.COURSE_EXPIRED.equals(courseProject.access.code)) {
                                mView.setShowError(new ShowDialogHelper().showErrorType(ShowDialogHelper.TYPE_TOAST)
                                        .showErrorMsgResId(CourseHelper.getCourseErrorRes(courseProject.access.code))
                                        .showError());
                                return;
                            }
                            if (courseProject.learningExpiryDate.expired) {
                                mView.setJoinButton(CourseProjectActivity.JoinButtonStatusEnum.COURSE_EXPIRED);
                            } else if (EdusohoApp.app.loginUser.vip != null && EdusohoApp.app.loginUser.vip.levelId <= mCourseProject.vipLevelId) {
                                mView.setJoinButton(CourseProjectActivity.JoinButtonStatusEnum.VIP_FREE);
                            }
                        }
                    }
                });
    }

    private void initLogoutCourseMemberStatus(final CourseProject courseProject) {
        mView.showFragments(initCourseModules(false), courseProject);
        if (courseProject.learningExpiryDate.expired) {
            mView.setJoinButton(CourseProjectActivity.JoinButtonStatusEnum.COURSE_EXPIRED);
        }
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
                            mFirstTrailTask = trialTask;
                            mView.initTrailTask(trialTask);
                        } else {
                            mView.setPlayLayoutVisible(false);
                        }
                    }
                });
    }

    @Override
    public void joinCourseProject() {
        if (mCourseProject.learningExpiryDate.expired) {
            return;
        }
        if (mCourseProject.originPrice == FREE_PRICE) {
            joinFreeOrVipCourse(mCourseProjectId);
        } else if (EdusohoApp.app.loginUser.vip != null && EdusohoApp.app.loginUser.vip.levelId <= mCourseProject.vipLevelId) {
            joinFreeOrVipCourse(mCourseProjectId);
        } else {
            mView.launchConfirmOrderActivity(mCourseProject.courseSet.id, mCourseProjectId);
        }
    }

    @Override
    public void finishTask(CourseTask task) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .setCourseTaskStatus(mCourseProjectId, task.id, CourseTask.CourseTaskStatusEnum.FINISH.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TaskEvent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TaskEvent taskEvent) {
                        mView.setTaskFinishButtonBackground(true);
                        mView.setCurrentTaskStatus(CourseTask.CourseTaskStatusEnum.FINISH);
                    }
                });
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
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.COURSE_EXIT));
                            mIsJoin = false;
                            mView.showToast(R.string.exit_course_success);
                            initTrialFirstTask(mCourseProjectId);
                            mView.exitCourseLayout();
                        } else {
                            mView.showToast(R.string.exit_course_failure);
                        }
                    }
                });
    }

    private void setCourseLearningProgress(int courseId) {
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(UserApi.class)
                .getMyCourseLearningProgress(courseId)
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
                    public void onNext(CourseLearningProgress progress) {
                        // TODO: 2017/4/25 非常不好的处理方式，需要封装
                        MessageEvent<CourseLearningProgress> progressMsg = new MessageEvent<>(progress, MessageEvent.COURSE_JOIN);
                        EventBus.getDefault().post(progressMsg);
                        if (progress.nextTask != null) {
                            mView.initNextTask(progress.nextTask);
                        } else {
                            mView.setPlayLayoutVisible(false);
                        }
                    }
                });
    }

    private void joinFreeOrVipCourse(final int courseId) {

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
                            mIsJoin = true;
                            mView.showToast(R.string.join_course_success);
                            mView.initJoinCourseLayout(CourseProject.LearnMode.getMode(mCourseProject.learnMode));
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
    public boolean isJoin() {
        return mIsJoin;
    }

    @Override
    public CourseMember getCourseMember() {
        return mMember;
    }

    @Override
    public void unsubscribe() {

    }

    private boolean isCourseMemberExpired(String deadline) {
        return !CommonConstant.EXPIRED_MODE_FOREVER.equals(deadline) && TimeUtils.getUTCtoDate(deadline).compareTo(new Date()) < 0;
    }

    private boolean isVipExpired(int courseVipLevelId) {
        return EdusohoApp.app.loginUser.vip != null && EdusohoApp.app.loginUser.vip.levelId <= courseVipLevelId;
    }

    private boolean isCourseStart(String startDate) {
        //放到coursemember
        return !CommonConstant.EXPIRED_MODE_FOREVER.equals(startDate) && TimeUtils.getUTCtoDate(startDate).compareTo(new Date()) > 0;
    }

    class ShowDialogHelper {
        private static final int TYPE_TOAST = 1;
        private static final int TYPE_DIALOG = 2;
        private static final int POSITIVE_ACTION_EXIT_COURSE = 3;
        private static final int POSITIVE_ACTION_BUY_VIP = 4;

        private int mShowType;
        private int mMsgResId;
        private int mActionType;

        ShowDialogHelper showError() {
            if (mShowType == TYPE_TOAST) {
                mView.showToast(mMsgResId);
            } else if (mShowType == TYPE_DIALOG) {
                mView.showExitDialog(mMsgResId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mActionType == POSITIVE_ACTION_EXIT_COURSE) {
                            exitCourse();
                        } else if (mActionType == POSITIVE_ACTION_BUY_VIP) {
                            // TODO: 2017/5/5
                            mView.showToast("购买vip，暂未做处理");
                        }
                        mView.setShowError(null);
                    }
                });
            }
            return this;
        }

        ShowDialogHelper showErrorType(int showType) {
            mShowType = showType;
            return this;
        }

        ShowDialogHelper showErrorMsgResId(int msgResId) {
            mMsgResId = msgResId;
            return this;
        }

        public ShowDialogHelper setAction(int action) {
            mActionType = action;
            return this;
        }
    }
}
