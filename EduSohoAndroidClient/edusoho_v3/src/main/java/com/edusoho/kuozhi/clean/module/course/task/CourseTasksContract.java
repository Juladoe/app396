package com.edusoho.kuozhi.clean.module.course.task;

import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;
import com.edusoho.kuozhi.v3.model.bal.course.Course;

import java.util.List;

import rx.Observable;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public interface CourseTasksContract {

    interface View extends BaseView<Presenter> {

        void showCourseTasks(List<CourseTask> courseTasks);
    }

    interface Presenter extends BasePresenter {

        Observable<List<CourseTask>> getCourseTasks(String courseProjectId);
    }
}
