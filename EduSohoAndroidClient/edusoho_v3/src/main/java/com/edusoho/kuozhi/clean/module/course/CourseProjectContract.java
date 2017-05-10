package com.edusoho.kuozhi.clean.module.course;

import com.edusoho.kuozhi.clean.bean.CourseMember;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.TaskResultEnum;
import com.edusoho.kuozhi.clean.bean.innerbean.Teacher;
import com.edusoho.kuozhi.clean.module.base.BasePresenter;
import com.edusoho.kuozhi.clean.module.base.BaseView;

import android.content.DialogInterface;

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

        void initJoinCourseLayout(CourseProject.LearnMode mode);

        void initTrailTask(CourseTask trialTask);

        void initNextTask(CourseTask nextTask, boolean isFirstTask);

        void initLearnLayout(CourseProject.LearnMode mode);

        void setJoinButton(CourseProjectActivity.JoinButtonStatusEnum statusEnum);

        void launchConfirmOrderActivity(int courseSetId, int courseId);

        void showExitDialog(int msgRes, DialogInterface.OnClickListener onClickListener);

        void setShowError(CourseProjectPresenter.ShowActionHelper helper);

        void setPlayLayoutVisible(boolean visible);

        void exitCourseLayout();

        void setTaskFinishButtonBackground(boolean learned);

        void setCurrentTaskStatus(TaskResultEnum status);

        void launchLoginActivity();

        void clearCoursesCache(int... courseIds);
    }

    interface Presenter extends BasePresenter {

        void consult();

        void joinCourseProject();

        void exitCourse();

        boolean isJoin();

        void finishTask(CourseTask task);

        CourseMember getCourseMember();
    }
}
