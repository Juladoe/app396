package com.edusoho.kuozhi.clean.module.course.task;

import com.edusoho.kuozhi.clean.bean.TaskItem;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public interface CourseTasksContract {

    interface View extends BaseView<Presenter> {
        void showCourseTasks(List<TaskItem> taskItems);
    }

    interface Presenter extends BasePresenter {

    }
}
