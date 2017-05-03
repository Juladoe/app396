package com.edusoho.kuozhi.clean.module.course.task.catalog;

import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public interface CourseTasksContract {

    interface View extends BaseView<Presenter> {
        void showCourseTasks(List<CourseItem> taskItems);

        void showCourseMenuButton(boolean show);
    }

    interface Presenter extends BasePresenter {
        void initCourseProgress(CourseLearningProgress progress);
    }
}
