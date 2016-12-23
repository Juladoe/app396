package com.edusoho.kuozhi.v3.ui.fragment.video;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.ui.DetailActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.helper.LessonMenuHelper;
import com.edusoho.videoplayer.ui.VideoPlayerFragment;

import org.videolan.libvlc.IVLCVout;

/**
 * Created by suju on 16/12/16.
 */

public class LessonVideoPlayerFragment extends VideoPlayerFragment implements View.OnFocusChangeListener {

    private int mLessonId;
    private int mCourseId;
    private DetailActivity mMenuCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLessonId = getArguments().getInt(Const.LESSON_ID);
        mCourseId = getArguments().getInt(Const.COURSE_ID);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DetailActivity) {
            mMenuCallback = (DetailActivity) activity;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            play();
        } else {
            pause();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mMenuCallback != null && mMenuCallback.getMenu() != null) {
            new LessonMenuHelper(getContext(), mLessonId, mCourseId).initMenu(mMenuCallback.getMenu());
        }
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

    @Override
    public void onPause() {
        super.onPause();
        if (mMenuCallback != null && mMenuCallback.getMenu() != null) {
            mMenuCallback.getMenu().dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMenuCallback != null && mMenuCallback.getMenu() != null) {
            mMenuCallback.getMenu().setVisibility(false);
        }
    }
}
