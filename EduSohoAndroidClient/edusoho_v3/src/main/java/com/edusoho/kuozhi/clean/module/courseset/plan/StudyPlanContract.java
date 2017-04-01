package com.edusoho.kuozhi.clean.module.courseset.plan;

import com.edusoho.kuozhi.clean.bean.CourseStudyPlan;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by DF on 2017/4/1.
 */

public interface StudyPlanContract {

    interface View extends BaseView<Presenter> {
        void setLoadViewVis(boolean isVis);

        void showComPanies(List<CourseStudyPlan> list);

    }

    interface Presenter extends BasePresenter {

    }
}
