package com.edusoho.kuozhi.clean.module.courseset.dialog.courses;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.utils.biz.CourseHelper;
import com.edusoho.kuozhi.v3.EdusohoApp;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/13.
 */

class SelectProjectDialogPresenter implements SelectProjectDialogContract.Presenter {

    private static final String END_DATE_MODE = "end_date";
    private static final String DAYS_MODE = "days";
    private static final String DATE_MODE = "date";

    private SelectProjectDialogContract.View mView;
    private List<CourseProject> mList;
    private CourseProject mCourseProject;

    SelectProjectDialogPresenter(SelectProjectDialogContract.View mView, List<CourseProject> courseProjects) {
        this.mView = mView;
        this.mList = courseProjects;
    }

    public void reFreshData(List<CourseProject> courseProjects){
        mList = courseProjects;
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void setData(int position) {
        mCourseProject = mList.get(position);
        mView.showWayAndServiceView(mCourseProject);
        mView.showPriceView(mCourseProject);
        if (END_DATE_MODE.equals(mCourseProject.learningExpiryDate.expiryMode)) {
            mView.showValidityView(R.string.validity, mCourseProject.learningExpiryDate.expiryEndDate.substring(0, 10));
        } else if (DATE_MODE.equals(mCourseProject.learningExpiryDate.expiryMode)) {
            mView.showValidityView(R.string.validity_date, mCourseProject.learningExpiryDate.expiryEndDate.substring(0, 10)
                    , mCourseProject.learningExpiryDate.expiryEndDate.substring(0, 10));
        } else if (DAYS_MODE.equals(mCourseProject.learningExpiryDate.expiryMode)) {
            mView.showValidityView(R.string.validity_day, mCourseProject.learningExpiryDate.expiryDays);
        } else {
            mView.showValiditView(R.string.validity_forever);
        }
        mView.showTaskView(mCourseProject.taskNum);
        mView.showVipView(mCourseProject.vipLevelId);
    }

    @Override
    public void confirm() {
        if ("success".equals(mCourseProject.access.code)) {
            joinFreeOrVipCourse(mCourseProject.id);
        } else {
            mView.showToast(CourseHelper.getCourseErrorRes(mCourseProject.access.code));
        }
    }

    private void joinFreeOrVipCourse(final int courseProjectId) {
        mView.showProcessDialog(true);
        HttpUtils.getInstance()
                .addTokenHeader(EdusohoApp.app.token)
                .createApi(CourseApi.class)
                .joinFreeOrVipCourse(courseProjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CourseMember>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showProcessDialog(false);
                        mView.showToastOrFinish(R.string.join_fail, false);
                    }

                    @Override
                    public void onNext(CourseMember courseMember) {
                        mView.showProcessDialog(false);
                        if (courseMember != null && courseMember.user != null) {
                            mView.showToastOrFinish(R.string.join_success, true);
                            mView.goToCourseProjectActivity(courseProjectId);
                        } else {
                            mView.goToConfirmOrderActivity(mCourseProject);
                        }
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }
}
