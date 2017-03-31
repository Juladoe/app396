package com.edusoho.kuozhi.clean.module.course.info;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public interface CourseProjectInfoContract {

    interface View extends BaseView<Presenter> {

        void showCourseProjectInfo(CourseProject courseProject);

        void showVipAdvertising(String vipId);

        void showPrice(CourseProjectPriceEnum type, String price, String originPrice);
    }

    interface Presenter extends BasePresenter {

        void getTaskInfo();

        void getRelativeTask();

        void getTaskMembers();

    }

}
