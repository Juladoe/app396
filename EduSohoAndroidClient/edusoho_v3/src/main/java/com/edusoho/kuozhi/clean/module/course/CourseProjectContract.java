package com.edusoho.kuozhi.clean.module.course;

import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;


/**
 * Created by JesseHuang on 2017/3/22.
 * 学习计划Contract
 */
public interface CourseProjectContract {

    interface View extends BaseView<Presenter> {

        void showTasksCover(String imageUrl);

        void setTitle(String title);

        void showFragments(CourseProjectEnum[] courseProjectModules, CourseProject courseProject);
    }

    interface Presenter extends BasePresenter {

        //Observable<CourseProject> getCourseProject(int id);

        void learnTask(int taskId);

        void favorite(int taskId);

        void consult();

        //CourseProjectEnum[] initCourseModules();
    }
}
