package com.edusoho.kuozhi.clean.module.courseset.info;

import com.edusoho.kuozhi.clean.api.CourseSetApi;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.http.HttpUtils;

import java.util.List;

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

    private static final int SHOW_MEMBER_COUNT=5;
    private int mCourseSetId;
    private CourseIntroduceContract.View mView;

    public CourseIntroducePresenter(int mCourseSetId, CourseIntroduceContract.View mView) {
        this.mView = mView;
        this.mCourseSetId = mCourseSetId;
    }

    @Override
    public void subscribe() {
        HttpUtils.getInstance()
                .createApi(CourseSetApi.class)
                .getCourseSet(mCourseSetId)
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
                .flatMap(new Func1<CourseSet, Observable<List<CourseMember>>>() {
                    @Override
                    public Observable<List<CourseMember>> call(CourseSet courseSet) {
                        return HttpUtils.getInstance()
                                .createApi(CourseSetApi.class)
                                .getCourseSetMembers(mCourseSetId, 0, SHOW_MEMBER_COUNT);

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CourseMember>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.setLoadViewVis(false);
                    }

                    @Override
                    public void onNext(List<CourseMember> courseMembers) {
                        if (courseMembers != null && courseMembers.size() > 0) {
                            mView.showStudent(courseMembers);
                        }
                    }
                });
    }

    @Override
    public void unsubscribe() {
    }

}
