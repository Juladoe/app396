package com.edusoho.kuozhi.clean.module.course;

import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
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

        void launchImChatWithTeacher(Teacher teacher);

        void showCacheButton(boolean visible);

        void showShareButton(boolean visible);

        void initJoinCourseLayout();

        void initLearnedLayout();

        void setJoinButton(boolean isCourseStarted);

        void setProgressBar(int progress);

        void launchDialogProgress(CourseLearningProgress progress, CourseMember member);

        void launchConfirmOrderActivity(int courseSetId, int courseId);

        void showExitDialog(CourseProjectActivity.DialogType type);

        void toast(String resId);
    }

    interface Presenter extends BasePresenter {

        void consult();

        void joinCourseProject(int courseId);

        void showCourseProgressInfo();

    }
}
