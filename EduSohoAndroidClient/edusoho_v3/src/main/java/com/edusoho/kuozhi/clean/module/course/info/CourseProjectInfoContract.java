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

        void showVipAdvertising(String vipName);

        void showPrice(CourseProjectPriceEnum type, String price, String originPrice);

        void showServices(CourseProject.Service[] services);

        void showIntroduce(String content);

        void showCover(String coverUrl);

        void showAudiences(String[] audiences);

        void showTeacher(CourseProject.Teacher teacher);
    }

    interface Presenter extends BasePresenter {

        void getTaskInfo();

        void getRelativeTask();

        void getTaskMembers();

    }

}
