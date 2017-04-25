package com.edusoho.kuozhi.clean.module.course.info;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.Member;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public interface CourseProjectInfoContract {

    interface View extends BaseView<Presenter> {

        void initCourseProjectInfo(CourseProject course);

        void showCourseProjectInfo(boolean show);

        void showVipAdvertising(String vipName);

        void showPrice(CourseProjectPriceEnum type, float price, float originPrice);

        void showServices(CourseProject.Service[] services);

        void showIntroduce(String content);

        void showAudiences(String[] audiences);

        void showTeacher(Teacher teacher);

        void showMemberNum(int count);

        void showMembers(List<Member> members);

        void showRelativeCourseProjects(List<CourseProject> courseList);

        void launchCourseProject(int courseId);
    }

    interface Presenter extends BasePresenter {

    }
}
