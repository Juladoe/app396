package com.edusoho.kuozhi.v3.ui.fragment.video;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonStatus;
import com.edusoho.kuozhi.v3.listener.LessonPluginCallback;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.LearnStatus;
import com.edusoho.kuozhi.v3.model.provider.LessonProvider;
import com.edusoho.kuozhi.v3.ui.DetailActivity;
import com.edusoho.kuozhi.v3.ui.MenuPop;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.ExerciseOptionDialog;
import com.edusoho.videoplayer.ui.VideoPlayerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suju on 16/12/16.
 */

public class LessonVideoPlayerFragment extends VideoPlayerFragment {

    private int mLessonId;
    private int mCourseId;
    private DetailActivity mMenuCallback;
    private List<ExerciseOptionDialog.GridViewItem> mExerciseItemList;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMenuCallback = (DetailActivity) activity;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initNavigationBar();
        loadLessonStatus();
    }

    @Override
    protected void changeScreenLayout(final int orientation) {
        if (orientation == getResources().getConfiguration().orientation) {
            return;
        }
        View playView = getView();
        ViewParent viewParent = playView.getParent();
        if (viewParent == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) viewParent.getParent();
        MessageEngine.getInstance().sendMsg(Const.FULL_SCREEN, null);

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        ViewGroup.LayoutParams lp = parent.getLayoutParams();
        lp.height = orientation == Configuration.ORIENTATION_LANDSCAPE ?
                wm.getDefaultDisplay().getHeight() : getContext().getResources().getDimensionPixelOffset(com.edusoho.videoplayer.R.dimen.video_height);
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        parent.setLayoutParams(lp);
    }

    @Override
    protected void changeHeaderViewStatus(boolean isShow) {
        String changeBarEvent = isShow ?
                Const.COURSE_SHOW_BAR : Const.COURSE_HIDE_BAR;
        MessageEngine.getInstance().sendMsg(changeBarEvent, null);
    }

    private void initNavigationBar() {
        mMenuCallback.getMenu().addItem("记笔记");
        mExerciseItemList = getExerciseItemList();
        if (mExerciseItemList != null) {
            for (int i = 0; i < mExerciseItemList.size(); i++) {
                mMenuCallback.getMenu()
                        .addItem(mExerciseItemList.get(i).title);
            }
        }
        mMenuCallback.getMenu().setVisibility(true);
        mMenuCallback.getMenu().setOnMenuClickListener(getMenuClickListener());
    }

    /**
     * 获取课时是否已学状态
     */
    private void loadLessonStatus() {
        new LessonProvider(getContext()).getLearnState(mLessonId, mCourseId)
                .success(new NormalCallback<LessonStatus>() {
                    @Override
                    public void success(LessonStatus state) {
                        if (state != null && LearnStatus.finished == state.learnStatus) {
                            mMenuCallback.getMenu().addItem("学完了");
                        } else {
                            mMenuCallback.getMenu().addItem("学完");
                        }
                    }
                });
    }

    private List<ExerciseOptionDialog.GridViewItem> getExerciseItemList() {
        List<ExerciseOptionDialog.GridViewItem> list = new ArrayList<>();

        Bundle bundle = new Bundle();
        bundle.putInt("lessonId", mLessonId);
        Intent intent = new Intent();
        intent.setPackage(getContext().getPackageName());
        intent.putExtra(Const.LESSON_ID, mLessonId);
        intent.setAction(Const.LESSON_PLUGIN);

        List<ResolveInfo> resolveInfos = getContext().getPackageManager().queryIntentActivities(
                intent, PackageManager.GET_ACTIVITIES);
        int index = 0;
        for (ResolveInfo resolveInfo : resolveInfos) {
            ExerciseOptionDialog.GridViewItem item = new ExerciseOptionDialog.GridViewItem();
            item.iconRes = getContext().getResources().getDrawable(resolveInfo.activityInfo.icon);
            item.title = resolveInfo.loadLabel(getContext().getPackageManager()).toString();
            item.bundle = intent.getExtras();
            item.action = resolveInfo.activityInfo.name;
            try {
                Class lessonPluginCallbackCls = Class.forName(resolveInfo.activityInfo.name + "$Callback");
                item.callback = (LessonPluginCallback) lessonPluginCallbackCls.getConstructor(Context.class).newInstance(getContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            list.add(item);
        }

        return list;
    }

    private MenuPop.OnMenuClickListener getMenuClickListener() {
        return new MenuPop.OnMenuClickListener() {
            @Override
            public void onClick(View v, int position, String name) {
                handlerMenuClick(v, position);
            }
        };
    }

    protected void handlerMenuClick(View v, int position) {
        switch (position) {
            case 0:
                startNodeActivity();
                break;
            case 1:
                startExerciseOrHomeWorkActivity(v, 0);
                break;
            case 2:
                startExerciseOrHomeWorkActivity(v, 1);
        }
    }

    private void startExerciseOrHomeWorkActivity(View v, int index) {
        ExerciseOptionDialog.GridViewItem item = mExerciseItemList.get(index);
        if (item.callback.click(null, v, index)) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtras(item.bundle);
        intent.setClassName(getContext().getPackageName(), item.action);
        getContext().startActivity(intent);
    }

    private void startNodeActivity() {
        Bundle bundle = new Bundle();
        bundle.putInt(Const.COURSE_ID, mCourseId);
        bundle.putInt(Const.LESSON_ID, mLessonId);
        CoreEngine.create(getContext()).runNormalPluginWithBundle("NoteActivity", getContext(), bundle);
    }
}
