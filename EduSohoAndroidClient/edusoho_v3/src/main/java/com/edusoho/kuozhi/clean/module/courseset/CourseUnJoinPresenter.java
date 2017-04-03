package com.edusoho.kuozhi.clean.module.courseset;

import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.CourseSetMember;
import com.edusoho.kuozhi.clean.module.courseset.info.CourseIntroduceFragment;
import com.edusoho.kuozhi.clean.module.courseset.plan.StudyPlayFragment;
import com.edusoho.kuozhi.clean.module.courseset.review.CourseEvaluateFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.course.CourseMember;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/3/31.
 */

public class CourseUnJoinPresenter implements CourseUnJoinContract.Presenter {

    private CourseUnJoinContract.View mView;
    private String mCourseId;

    public CourseUnJoinPresenter(String mCourseId, CourseUnJoinContract.View view) {
        this.mCourseId = mCourseId;
        this.mView = view;
    }

    @Override
    public void subscribe() {
        mView.showFragments(getTitleArray(), getFragmentArray());
        if (mCourseId == null || "0".equals(mCourseId)) {
            mView.newFinish(true);
            return;
        }
        isJoin();
    }

    @Override
    public void isJoin() {
        if (EdusohoApp.app.loginUser != null) {
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
                            mView.newFinish(true);
                        }

                        @Override
                        public void onNext(CourseSetMember courseSetMember) {
                            boolean isMember = false;
                            for (CourseMember courseMember : courseSetMember.data) {
                                if (EdusohoApp.app.loginUser.id == courseMember.id) {
                                    isMember = true;
                                    break;
                                }
                            }
                            if (isMember) {
                                // TODO: 2017/4/3 已加入课程，直接进入任务
                            } else {
                                getCourseSet(mCourseId)
                                        .subscribeOn(Schedulers.io())
                                        .unsubscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<CourseSet>() {
                                            @Override
                                            public void onCompleted() {

                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                mView.newFinish(true);
                                            }

                                            @Override
                                            public void onNext(CourseSet courseSet) {

                                            }
                                        });
                            }
                        }
                    });
            return;
        }
        getCourseSet(mCourseId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseSet>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.newFinish(true);
                    }

                    @Override
                    public void onNext(CourseSet courseSet) {

                    }
                });
    }

    private String[] getFragmentArray() {
        return new String[]{
                CourseIntroduceFragment.class.getName(),
                StudyPlayFragment.class.getName(),
                CourseEvaluateFragment.class.getName()
        };
    }

    private String[] getTitleArray() {
        return new String[]{
                "简介", "计划", "评价"
        };
    }

    @Override
    public void unsubscribe() {

    }

    private Observable<CourseSet> getCourseSet(String id) {
        return RetrofitService.getCourseSet(id);
    }

    private Observable<CourseSetMember> getCourseSetMember(String id) {
        return RetrofitService.getCourseSetMember(id);
    }

}
