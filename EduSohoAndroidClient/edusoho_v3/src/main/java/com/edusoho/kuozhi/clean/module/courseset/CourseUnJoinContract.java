package com.edusoho.kuozhi.clean.module.courseset;

import com.edusoho.kuozhi.clean.bean.CourseStudyPlan;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by DF on 2017/3/31.
 */

public interface CourseUnJoinContract {

    interface View extends BaseView<Presenter> {
        void showFragments(String[] titleArray, String[] fragmentArray);

        void newFinish(boolean isShow);

        void setPlanData(List<CourseStudyPlan> list, List<VipInfo> vipInfo);
    }

    interface Presenter extends BasePresenter {
        void isJoin();
    }
}
