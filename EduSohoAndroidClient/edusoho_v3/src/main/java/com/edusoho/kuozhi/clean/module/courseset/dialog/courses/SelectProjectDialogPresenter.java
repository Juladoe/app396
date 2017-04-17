package com.edusoho.kuozhi.clean.module.courseset.dialog.courses;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.RetrofitService;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.JsonObject;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DF on 2017/4/13.
 */

public class SelectProjectDialogPresenter implements SelectProjectDialogContract.Presenter {

    private static final String BUY_ABLE = "1";
    private static final String IS_FREE = "1";
    private static final String FREE = "free";
    private static final String VIP = "vip";

    private SelectProjectDialogContract.View mView;

    public SelectProjectDialogPresenter(SelectProjectDialogContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void confirm(CourseProject courseProject) {
        if (!BUY_ABLE.equals(courseProject.buyable)) {
            mView.showToastOrFinish(R.string.course_limit_join, false);
            return;
        }
        if (IS_FREE.equals(courseProject.isFree) || EdusohoApp.app.loginUser.vip != null
                && courseProject.vipLevelId != 0
                && EdusohoApp.app.loginUser.vip.levelId >= courseProject.vipLevelId) {
            mView.showProcessDialog(true);
            joinFreeOrVipCourse(courseProject.id , IS_FREE.equals(courseProject.isFree) ? FREE : VIP);
            return;
        }
        mView.goToConfirmOrderActivity();
    }

    private void joinFreeOrVipCourse(int courseId, String joinWay) {
        RetrofitService.joinFreeOrVipCourse(EdusohoApp.app.token, courseId, joinWay)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showProcessDialog(false);
                        mView.showToastOrFinish(R.string.join_fail, false);
                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        mView.showProcessDialog(false);
                        mView.showToastOrFinish(R.string.join_success, true);
                        mView.goToCourseProjectActivity();
                    }
                });
    }

    @Override
    public void unsubscribe() {

    }
}
