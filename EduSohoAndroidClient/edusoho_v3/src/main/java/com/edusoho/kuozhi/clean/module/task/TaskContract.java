package com.edusoho.kuozhi.clean.module.task;

import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

/**
 * Created by JesseHuang on 2017/3/22.
 * 学习计划Contract
 */
public interface TaskContract {

    interface View extends BaseView<Presenter> {

        void showTaskInfo();

        void showTasks();

        void showRates();

        void showTasksCover();

        void setTitle(String title);
    }

    interface Presenter extends BasePresenter {

        void getTaskInfo(int taskId);

        void getTasks(int taskId);

        void learnTask(int taskId);

        void favorite(int taskId);

        void consult();
    }
}
