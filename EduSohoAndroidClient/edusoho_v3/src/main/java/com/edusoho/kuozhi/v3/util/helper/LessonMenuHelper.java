package com.edusoho.kuozhi.v3.util.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.api.CourseApi;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.http.HttpUtils;
import com.edusoho.kuozhi.clean.module.course.dialog.TaskFinishDialog;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonStatus;
import com.edusoho.kuozhi.v3.entity.lesson.PluginViewItem;
import com.edusoho.kuozhi.v3.listener.LessonPluginCallback;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.LearnStatus;
import com.edusoho.kuozhi.v3.model.provider.LessonProvider;
import com.edusoho.kuozhi.v3.ui.MenuPop;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by suju on 16/12/21.
 */

public class LessonMenuHelper {

    private int mLessonId;
    private int mCourseId;
    private String mCurrentLearnState;
    private Context mContext;
    private MenuPop mMenuPop;
    private MenuHelperFinishListener mMenuHelperFinishListener;

    public LessonMenuHelper(Context context, int lessonId, int courseId) {
        this.mContext = context;
        this.mLessonId = lessonId;
        this.mCourseId = courseId;
    }

    public LessonMenuHelper addMenuHelperListener(MenuHelperFinishListener listener) {
        mMenuHelperFinishListener = listener;
        return this;
    }

    public MenuPop getMenuPop() {
        return mMenuPop;
    }

    public void initMenu(MenuPop menuPop) {
        if (menuPop == null) {
            return;
        }
        this.mMenuPop = menuPop;
        mMenuPop.removeAll();
        mMenuPop.addItem("记笔记");
        mMenuPop.addItem("学完");
        mMenuPop.setVisibility(true);
        mMenuPop.setOnMenuClickListener(getMenuClickListener());
        loadLessonStatus();
    }

    public void show(View view, int x, int y) {
        mMenuPop.showAsDropDown(view, x, y);
    }

    /**
     * 获取课时是否已学状态
     */
    private void loadLessonStatus() {
        new LessonProvider(mContext).getLearnState(mLessonId, mCourseId)
                .success(new NormalCallback<LessonStatus>() {
                    @Override
                    public void success(LessonStatus state) {
                        if (state.learnStatus == null) {
                            return;
                        }
                        mCurrentLearnState = state.learnStatus.status;
                        setLearnBtnState("finish".equals(state.learnStatus.status));
                    }
                });
    }

    private MenuPop.OnMenuClickListener getMenuClickListener() {
        return new MenuPop.OnMenuClickListener() {
            @Override
            public void onClick(View v, int position, String name) {
                handlerMenuClick(v, position);
                mMenuPop.dismiss();
            }
        };
    }

    protected void handlerMenuClick(View v, int position) {
        switch (position) {
            case 0:
                MobclickAgent.onEvent(mContext, "timeToLearn_topThreePoints_takeNotes");
                startNodeActivity();
                break;
            case 1:
                MobclickAgent.onEvent(mContext, "timeToLearn_topThreePoints_finished");
                changeLessonLearnState(v);
                break;
        }
    }

    private synchronized void changeLessonLearnState(final View view) {
        if ("finish".equals(mCurrentLearnState)) {
            return;
        }
        view.setEnabled(false);
        HttpUtils.getInstance()
                .createApi(CourseApi.class)
                .getCourseProject(mCourseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseProject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CourseProject courseProject) {
                        if (courseProject.enableFinish == 1) {
                            HttpUtils.getInstance()
                                    .createApi(CourseApi.class)
                                    .setCourseTaskStatus(mCourseId, mLessonId, CourseTask.CourseTaskStatusEnum.FINISH.toString())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Subscriber<TaskEvent>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onNext(TaskEvent taskEvent) {
                                            view.setEnabled(true);
                                            if (taskEvent.result != null && CourseTask.CourseTaskStatusEnum.FINISH.toString().equals(taskEvent.result.status)) {
                                                MessageEngine.getInstance().sendMsg(Const.LESSON_STATUS_REFRESH, null);
                                                setLearnBtnState(true);
                                                mCurrentLearnState = "finish";
                                                if (mMenuHelperFinishListener != null) {
                                                    mMenuHelperFinishListener.showFinishTaskDialog(taskEvent);
                                                }
                                            }
                                        }
                                    });
                        } else {
                            CommonUtil.longToast(mContext, mContext.getString(R.string.course_limit_task));
                        }
                    }
                });
    }

    private void setLearnBtnState(boolean isLearn) {
        if (isLearn) {
            MenuPop.Item item = mMenuPop.getItem(1);
            item.setName("已学完");
            item.setColor(mContext.getResources().getColor(R.color.primary_color));
        } else {
            mMenuPop.getItem(1).setName("学完");
        }
    }

    private void startNodeActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt(Const.COURSE_ID, mCourseId);
        bundle.putInt(Const.LESSON_ID, mLessonId);
        CoreEngine.create(mContext).runNormalPluginWithBundle("NoteActivity", mContext, bundle);
    }

    public interface MenuHelperFinishListener {
        void showFinishTaskDialog(TaskEvent taskEvent);
    }
}
