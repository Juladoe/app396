package com.edusoho.kuozhi.clean.module.courseset;

import com.edusoho.kuozhi.clean.bean.CourseSet;
import com.edusoho.kuozhi.clean.bean.CourseStudyPlan;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by DF on 2017/3/31.
 */

public interface CourseUnLearnContract {

    interface View extends BaseView<Presenter> {

        void setCourseSet(CourseSet courseSet);

        void showFragments(String[] titleArray, String[] fragmentArray);

        void showBackGround(String img);

        void showDiscountInfo(String name, long time);

        void showFavorite(boolean isFavorite);

        void newFinish(boolean isShow, int content);

        void showProcessDialog(boolean isShow);

        void showLoadView(boolean isShow);

        void showToast(int content);

        void showPlanDialog(List<CourseStudyPlan> list, List<VipInfo> vipInfo, CourseSet courseSet);

        void goToConfirmOrderActivity(CourseStudyPlan courseStudyPlan);

        void goToCourseProjectActivity(String courseProjectId);
    }

    interface Presenter extends BasePresenter {
        void isJoin();

        void joinStudy();
    }
}
