package com.edusoho.kuozhi.clean.module.course.info;

import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public interface CourseProjectInfoContract {

    interface View extends BaseView<Presenter> {

        void showCourseProjectInfo(CourseProject course);

        void showVipAdvertising(String vipName);

        void showPrice(CourseProjectPriceEnum type, String price, String originPrice);

        void showServices(CourseProject.Service[] services);

        void showIntroduce(String content);

        void showAudiences(String[] audiences);

        void showTeacher(CourseProject.Teacher teacher);

        void showMemberNum(int count);

        void showMembers(List<CourseMember> members);

        void showRelativeCourseProjects(List<CourseProject> courseList);

        void launchCourseProject(String courseId);
    }

    interface Presenter extends BasePresenter {

    }

}
