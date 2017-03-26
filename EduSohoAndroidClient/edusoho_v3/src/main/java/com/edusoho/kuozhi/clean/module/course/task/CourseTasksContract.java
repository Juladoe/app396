package com.edusoho.kuozhi.clean.module.course.task;

import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public interface CourseTasksContract {

    interface View extends BaseView<Presenter> {

        void showLearningProgress();

        void showCourseTasks();
    }

    interface Presenter extends BasePresenter {

        void getCourseTasks(int courseProjectId);

        void showLearningProgress();
    }
}
