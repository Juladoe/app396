package com.edusoho.kuozhi.clean.module.course.task.menu.info;

import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.module.course.info.CourseProjectInfoContract;

/**
 * Created by JesseHuang on 2017/4/24.
 */

public class CourseMenuInfoContract {

    interface MenuCourseInfoView extends CourseProjectInfoContract.View {

        void showCourseProgress(CourseLearningProgress courseLearningProgress);
    }

    interface MenuCourseInfoPresenter extends CourseProjectInfoContract.Presenter {
    }
}
