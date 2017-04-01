package com.edusoho.kuozhi.clean.module.courseset.info;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseSet;

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
        this.mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        getCourseIntro(mCourseId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
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
                        
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }

    private Observable<CourseSet> getCourseIntro(String id){
        return RetrofitService.getCourseSet(id);
    }
}
