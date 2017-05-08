package com.edusoho.kuozhi.clean.module.courseset;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by DF on 2017/3/31.
 */

interface CourseUnLearnContract {

    interface View extends BaseView<Presenter> {

        void setCourseSet(CourseSet courseSet);

        void showFragments(String[] titleArray, String[] fragmentArray);

        void showBackGround(String img);

        void showDiscountInfo(String name, long time);

        void showFavorite(boolean isFavorite);

        void showFavoriteCourseSet(boolean isFavorite);

        void newFinish();

        void showProcessDialog(boolean isShow);

        void setDialogData(List<CourseProject> list);

        void showLoadView(boolean isShow);

        void showPlanDialog(List<CourseProject> list, List<VipInfo> vipInfo, CourseSet courseSet);

        void goToConfirmOrderActivity(CourseProject courseStudyPlan);

        void goToCourseProjectActivity(int courseProjectId);

        void goToImChatActivity(Teacher creator);

        void goToLoginActivity();
    }

    interface Presenter extends BasePresenter {

        void isJoinCourseSet();

        void joinStudy();

        void consultTeacher();

        void favoriteCourseSet();

        void cancelFavoriteCourseSet();
    }
}
