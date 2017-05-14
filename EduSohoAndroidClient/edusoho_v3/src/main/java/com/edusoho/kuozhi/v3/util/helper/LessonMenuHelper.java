package com.edusoho.kuozhi.v3.util.helper;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.bean.TaskEvent;
import com.edusoho.kuozhi.clean.utils.biz.TaskFinishHelper;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonStatus;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.LessonProvider;
import com.edusoho.kuozhi.v3.ui.MenuPop;
import com.edusoho.kuozhi.v3.util.Const;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

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
    private CourseTask mCourseTask;
    private CourseProject mCourseProject;
    private TaskFinishHelper.Builder builder;
    private TaskFinishHelper mTaskFinishHelper;

    public LessonMenuHelper(Context context, int lessonId, int courseId) {
        this.mContext = context;
        this.mLessonId = lessonId;
        this.mCourseId = courseId;
    }

    public LessonMenuHelper addMenuHelperListener(MenuHelperFinishListener listener) {
        this.mMenuHelperFinishListener = listener;
        return this;
    }

    public LessonMenuHelper addCourseProject(CourseProject courseProject) {
        this.mCourseProject = courseProject;
        return this;
    }

    public LessonMenuHelper setCourseTask(CourseTask courseTask) {
        this.mCourseTask = courseTask;
        return this;
    }

    public LessonMenuHelper initTaskHelper() {
        builder = new TaskFinishHelper.Builder().setCourseId(mCourseId).setCourseTask(mCourseTask).setEnableFinish(mCourseProject.enableFinish);
        mTaskFinishHelper = new TaskFinishHelper(builder, mContext);
        mTaskFinishHelper.setActionListener(new TaskFinishHelper.ActionListener() {
            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onFinish(TaskEvent taskEvent) {
                mCurrentLearnState = taskEvent.result.status;
                EventBus.getDefault().postSticky(new MessageEvent<>(mCourseTask.id, MessageEvent.FINISH_TASK_SUCCESS));
                setLearnBtnState(true);
                if (mMenuHelperFinishListener != null) {
                    mMenuHelperFinishListener.showFinishTaskDialog(taskEvent);
                }
            }
        }).onInvoke();
        return this;
    }

    public MenuPop getMenuPop() {
        return mMenuPop;
    }

    public LessonMenuHelper initMenu(MenuPop menuPop) {
        if (menuPop == null) {
            throw new RuntimeException("Menupop is not null");
        }
        this.mMenuPop = menuPop;
        mMenuPop.removeAll();
        mMenuPop.addItem("记笔记");
        mMenuPop.addItem("学完");
        mMenuPop.setVisibility(true);
        mMenuPop.setOnMenuClickListener(getMenuClickListener());
        loadLessonStatus();
        return this;
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
                })
                .fail(new NormalCallback<VolleyError>() {
                    @Override
                    public void success(VolleyError obj) {

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
                taskFinish();
                break;
        }
    }

    private synchronized void taskFinish() {
        if ("finish".equals(mCurrentLearnState)) {
            return;
        }
        mTaskFinishHelper.finish();
    }

    public void menuClickTaskFinish() {
        if (mTaskFinishHelper != null) {
            mTaskFinishHelper.finish();
        }
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
