package com.edusoho.kuozhi.clean.module.courseset.info;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.DataPageResult;
import com.edusoho.kuozhi.v3.model.bal.course.CourseMember;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/1.
 */

public class CourseIntroducePresenter implements CourseIntroduceContract.Presenter {

    private String mCourseSetId;
    private CourseIntroduceContract.View mView;

    public CourseIntroducePresenter(String mCourseSetId, CourseIntroduceContract.View mView) {
        this.mView = mView;
        this.mCourseSetId = mCourseSetId;
    }

    @Override
    public void subscribe() {
        getCourseSetIntro(mCourseSetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<CourseSet>() {
                    @Override
                    public void call(CourseSet courseSet) {
                        mView.setLoadViewVis(false);
                        if (courseSet != null) {
                            mView.setData(courseSet);
                            mView.showHead();
                            mView.showInfoAndPeople();
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<CourseSet, Observable<DataPageResult<CourseMember>>>() {
                    @Override
                    public Observable<DataPageResult<CourseMember>> call(CourseSet courseSet) {
                        return getCourseSetMember(mCourseSetId);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DataPageResult<CourseMember>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.setLoadViewVis(false);
                    }

                    @Override
                    public void onNext(DataPageResult<CourseMember> courseMembers) {
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

    private Observable<DataPageResult<CourseMember>> getCourseSetMember(String id){
        return RetrofitService.getCourseSetMember(id);
    }

}
