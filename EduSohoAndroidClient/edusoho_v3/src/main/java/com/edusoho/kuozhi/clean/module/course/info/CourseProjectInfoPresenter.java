package com.edusoho.kuozhi.clean.module.course.info;

import android.util.Log;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.v3.model.bal.Member;
import com.edusoho.kuozhi.v3.model.bal.VipLevel;
import com.edusoho.videoplayer.util.AndroidDevices;

import java.util.List;

import cn.trinea.android.common.util.StringUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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
    public void subscribe() {
        mView.showCourseProjectInfo(mCourseProject);
        showPrice();
        showVip(mCourseProject.vipLevelId);
        showServices(mCourseProject.services);
        showIntroduce();
        showAudiences(mCourseProject.audiences);
        if (mCourseProject.teachers != null && mCourseProject.teachers.length > 0) {
            showTeacher(mCourseProject.teachers[0]);
        }
        showMemberNum(mCourseProject.studentNum);
        showMembers(mCourseProject.id);
        showRelativeCourseProjects(mCourseProject.courseSetId, mCourseProject.id);
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

    private void showServices(CourseProject.Service[] services) {
        mView.showServices(services);
    }

    private void showAudiences(String[] audiences) {
        mView.showAudiences(audiences);
    }

    private void showTeacher(CourseProject.Teacher teacher) {
        mView.showTeacher(teacher);
    }

    private void showIntroduce() {
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
                        if (StringUtils.isEmpty(mCourseProject.summary)) {
                            mView.showIntroduce(mCourseProject.summary);
                        } else {
                            mView.showIntroduce(courseSet.summary);
                        }
                    }
                });
    }

    private void showMemberNum(int count) {
        mView.showMemberNum(count);
    }

    private void showMembers(String courseId) {
        RetrofitService.getCourseMembers(courseId, 0, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataPageResult<CourseMember>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("showMembers", "onError: " + e.toString());
                    }

                    @Override
                    public void onNext(DataPageResult<CourseMember> memberDataPageResult) {
                        mView.showMembers(memberDataPageResult.data);
                    }
                });
    }

    private void showRelativeCourseProjects(String courseSetId, final String currentCourseProjectId) {
        RetrofitService.getCourseProjects(courseSetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<List<CourseProject>, Observable<CourseProject>>() {
                    @Override
                    public Observable<CourseProject> call(List<CourseProject> courseProjects) {
                        return Observable.from(courseProjects);
                    }
                })
                .filter(new Func1<CourseProject, Boolean>() {
                    @Override
                    public Boolean call(CourseProject courseProject) {
                        return !courseProject.id.equals(currentCourseProjectId);
                    }
                })
                .toList()
                .subscribe(new Subscriber<List<CourseProject>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("RelativeCourse", "onError: " + e.toString());
                    }

                    @Override
                    public void onNext(List<CourseProject> courseProjects) {
                        mView.showRelativeCourseProjects(courseProjects);
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }
}
