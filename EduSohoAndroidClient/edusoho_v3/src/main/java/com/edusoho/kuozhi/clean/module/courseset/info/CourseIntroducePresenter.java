package com.edusoho.kuozhi.clean.module.courseset.info;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.CourseSetMember;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/1.
 */

public class CourseIntroducePresenter implements CourseIntroduceContract.Presenter {

    private String mCourseId;
    private CourseIntroduceContract.View mView;

    public CourseIntroducePresenter(String mCourseId, CourseIntroduceContract.View mView) {
        this.mView = mView;
        this.mCourseId = mCourseId;
    }

    @Override
    public void subscribe() {
        getCourseSetIntro(mCourseId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseSet>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.setLoadViewVis(false);
                    }

                    @Override
                    public void onNext(CourseSet courseSet) {
                        mView.setLoadViewVis(false);
                        if (courseSet != null) {
                            mView.setData(courseSet);
                            mView.showHead();
                            mView.showInfoAndPeople();
                        }
                    }
                });
        getCourseSetMember(mCourseId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseSetMember>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CourseSetMember courseMembers) {
                        if (courseMembers != null) {
                            mView.showStudent(courseMembers.data);
                        }
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }

    private Observable<CourseSet> getCourseSetIntro(String id){
        return RetrofitService.getCourseSet(id);
    }

    private Observable<CourseSetMember> getCourseSetMember(String id){
        return RetrofitService.getCourseSetMember(id);
    }

}
