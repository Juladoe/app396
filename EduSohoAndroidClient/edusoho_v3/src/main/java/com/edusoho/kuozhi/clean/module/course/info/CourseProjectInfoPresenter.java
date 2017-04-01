package com.edusoho.kuozhi.clean.module.course.info;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.v3.model.bal.VipLevel;
import com.edusoho.kuozhi.v3.util.CommonUtil;

import java.util.List;

import cn.trinea.android.common.util.StringUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseProjectInfoPresenter implements CourseProjectInfoContract.Presenter {

    private static final String NO_VIP = "0";
    private static final String FREE = "0.00";
    private CourseProject mCourseProject;
    private CourseProjectInfoContract.View mView;

    public CourseProjectInfoPresenter(CourseProject courseProject, CourseProjectInfoContract.View view) {
        this.mCourseProject = courseProject;
        this.mView = view;
    }

    @Override
    public void getTaskInfo() {

    }

    @Override
    public void getRelativeTask() {

    }

    @Override
    public void getTaskMembers() {

    }

    @Override
    public void subscribe() {
        mView.showCourseProjectInfo(mCourseProject);
        showPrice();
        showVip(mCourseProject.vipLevelId);
        showServices(mCourseProject.services);

        RetrofitService.getCourseSet(mCourseProject.courseSetId)
                .subscribeOn(Schedulers.io())
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
                        mView.showCover(courseSet.cover);
                        showIntroduce(mCourseProject, courseSet);
                    }
                });

        showAudiences(mCourseProject.audiences);
        if (mCourseProject.teachers != null && mCourseProject.teachers.length > 0) {
            showTeacher(mCourseProject.teachers[0]);
        }
    }

    private void showPrice() {
        if (mCourseProject.originPrice.compareTo(mCourseProject.price) == 0 && FREE.equals(mCourseProject.originPrice)) {
            mView.showPrice(CourseProjectPriceEnum.FREE, mCourseProject.price, mCourseProject.originPrice);
        } else if (mCourseProject.originPrice.compareTo(mCourseProject.price) == 0) {
            mView.showPrice(CourseProjectPriceEnum.ORIGINAL, mCourseProject.price, mCourseProject.originPrice);
        } else {
            mView.showPrice(CourseProjectPriceEnum.SALE, mCourseProject.price, mCourseProject.originPrice);
        }
    }

    private void showVip(String vipLevelId) {
        if (!NO_VIP.equals(vipLevelId)) {
            RetrofitService.getVipLevel(vipLevelId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<VipLevel>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(VipLevel vipLevel) {
                            mView.showVipAdvertising(vipLevel.name);
                        }
                    });
        }
    }

    private void showIntroduce(CourseProject courseProject, CourseSet courseSet) {
        if (StringUtils.isEmpty(courseProject.summary)) {
            mView.showIntroduce(courseProject.summary);
        } else {
            mView.showIntroduce(courseSet.summary);
        }
    }

    private void showServices(CourseProject.Service[] services) {
        mView.showServices(services);
    }

    private void showAudiences(String[] audiences) {
        mView.showAudiences(audiences);
    }

    private void showTeacher(CourseProject.Teacher teacher) {
        mView.showTeacher(teacher);
    }

    private void showStudents(){

    }

    @Override
    public void unsubscribe() {

    }
}
