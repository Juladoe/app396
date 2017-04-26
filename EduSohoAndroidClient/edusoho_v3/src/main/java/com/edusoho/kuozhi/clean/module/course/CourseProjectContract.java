package com.edusoho.kuozhi.clean.module.course;

import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import android.support.v4.app.Fragment;

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

        void launchImChatWithTeacher(Teacher teacher);

        void showCacheButton(boolean visible);

        void showShareButton(boolean visible);

        void initJoinCourseLayout(int isFree);

        void initTrailTask(CourseTask trialTask);

        void initNextTask(CourseTask nextTask);

        void initLearnLayout(int isFree);

        void setJoinButton(boolean isCourseStarted);

        void launchConfirmOrderActivity(int courseSetId, int courseId);

        void showExitDialog(CourseProjectActivity.DialogType type);

        void setPlayLayoutVisible(boolean visible);

        void exitCourseLayout();
    }

    interface Presenter extends BasePresenter {

        void consult();

        void joinCourseProject(int courseId);

        void exitCourse();

        boolean isJoin();
    }
}
