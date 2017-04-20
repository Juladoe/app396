package com.edusoho.kuozhi.clean.module.courseset.plan;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.VipInfo;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by DF on 2017/4/1.
 */

interface CourseProjectsContract {

    interface View extends BaseView<Presenter> {

        void setLoadViewVis(boolean isVis);

        void showComPanies(List<CourseProject> list, List<VipInfo> vipInfos);

    }

    interface Presenter extends BasePresenter {

    }
}
