package com.edusoho.kuozhi.clean.module.course.info;

import android.util.Log;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.Member;
import com.edusoho.kuozhi.clean.bean.CourseMemberRoleEnum;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.v3.model.bal.VipLevel;

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
    private static final float FREE = 0;
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
        showMembers(mCourseProject.id, CourseMemberRoleEnum.STUDENT.toString());
        showRelativeCourseProjects(mCourseProject.courseSetId, mCourseProject.id);
    }

    private void showPrice() {
        if (mCourseProject.originPrice == mCourseProject.price && FREE == mCourseProject.originPrice) {
            mView.showPrice(CourseProjectPriceEnum.FREE, mCourseProject.price, mCourseProject.originPrice);
        } else if (mCourseProject.originPrice == mCourseProject.price) {
            mView.showPrice(CourseProjectPriceEnum.ORIGINAL, mCourseProject.price, mCourseProject.originPrice);
        } else {
            mView.showPrice(CourseProjectPriceEnum.SALE, mCourseProject.price, mCourseProject.originPrice);
        }
    }

    private void showVip(int vipLevelId) {
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

    private void showMembers(int courseId, String role) {
        RetrofitService.getCourseMembers(courseId, role, 0, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataPageResult<Member>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("showMembers", "onError: " + e.toString());
                    }

                    @Override
                    public void onNext(DataPageResult<Member> memberDataPageResult) {
                        mView.showMembers(memberDataPageResult.data);
                    }
                });
    }

    private void showRelativeCourseProjects(int courseSetId, final int currentCourseProjectId) {
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
                        return courseProject.id == currentCourseProjectId;
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
