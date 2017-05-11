package com.edusoho.kuozhi.clean.module.course.task.menu.info;

import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseMemberRoleEnum;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.module.course.info.CourseProjectInfoPresenter;

/**
 * Created by JesseHuang on 2017/4/24.
 */

public class CourseMenuInfoPresenter extends CourseProjectInfoPresenter
        implements CourseMenuInfoContract.MenuCourseInfoPresenter {

    private CourseMenuInfoContract.MenuCourseInfoView mView;
    private CourseLearningProgress mCourseLearningProgress;
    private CourseProject mCourseProject;

    public CourseMenuInfoPresenter(CourseMenuInfoContract.MenuCourseInfoView view
            , CourseProject courseProject
            , CourseLearningProgress courseLearningProgress) {
        super(courseProject, view);
        this.mView = view;
        this.mCourseProject = courseProject;
        this.mCourseLearningProgress = courseLearningProgress;
    }

    @Override
    public void subscribe() {
        mView.showCourseProjectInfo(false);
        mView.initCourseProjectInfo(mCourseProject);
    }
}
