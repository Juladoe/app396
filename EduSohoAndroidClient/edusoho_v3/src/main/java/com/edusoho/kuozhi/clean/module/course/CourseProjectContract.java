package com.edusoho.kuozhi.clean.module.course;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;


/**
 * Created by JesseHuang on 2017/3/22.
 * 学习计划Contract
 */
public interface CourseProjectContract {

    interface View extends BaseView<Presenter> {

        void showCover(String imageUrl);

        void showBottomLayout(boolean visible);

        void showFragments(List<CourseProjectEnum> courseProjectModules, CourseProject courseProject);

        void launchImChatWithTeacher(CourseProject.Teacher teacher);

        void showCacheButton(boolean visible);

        void showShareButton(boolean visible);

        void initLearnedLayout();
    }

    interface Presenter extends BasePresenter {

        //Observable<CourseProject> getCourseProject(int id);

        void consult();

        //CourseProjectEnum[] initCourseModules();
    }
}
