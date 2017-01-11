package com.edusoho.kuozhi.v3.ui.fragment.video;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.LessonProvider;
import com.edusoho.kuozhi.v3.ui.DetailActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.helper.LessonMenuHelper;
import com.edusoho.videoplayer.ui.VideoPlayerFragment;

/**
 * Created by suju on 16/12/16.
 */

public class LessonVideoPlayerFragment extends VideoPlayerFragment implements View.OnFocusChangeListener {

    private int mLessonId;
    private int mCourseId;
    private long mSaveSeekTime;
    private DetailActivity mMenuCallback;
    private LessonMenuHelper mLessonMenuHelper;
    private SharedPreferences mSeekPositionSetting;
    private static final String SEEK_POSITION = "seek_position";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLessonId = getArguments().getInt(Const.LESSON_ID);
        mCourseId = getArguments().getInt(Const.COURSE_ID);
        mSeekPositionSetting = getContext().getSharedPreferences(SEEK_POSITION, Context.MODE_PRIVATE);
        mSaveSeekTime = mSeekPositionSetting.getLong(String.format("%d-%d", mCourseId, mLessonId), 0);

        setSeekPosition(mSaveSeekTime);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DetailActivity) {
            mMenuCallback = (DetailActivity) activity;
        }
    }

//    @Override
//    protected void requestMediaUri() {
//        loadPlayUrl();
//    }

    private void loadPlayUrl() {
        new LessonProvider(getContext()).getLesson(mLessonId)
                .success(new NormalCallback<LessonItem>() {
                    @Override
                    public void success(LessonItem lessonItem) {
                        if (lessonItem == null || TextUtils.isEmpty(lessonItem.mediaUri)) {
                            return;
                        }
                        Uri mediaUri = Uri.parse(lessonItem.mediaUri);
//                        playVideo(String.format("%s://%s%s", mediaUri.getScheme(), mediaUri.getHost(), mediaUri.getPath()));
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
            }
        });
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
            mLessonMenuHelper = new LessonMenuHelper(getContext(), mLessonId, mCourseId);
            mLessonMenuHelper.initMenu(mMenuCallback.getMenu());
        }
        loadPlayUrl();
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

    @Override
    public void onResume() {
        super.onResume();
        if (mLessonMenuHelper != null) {
            mLessonMenuHelper.updatePluginItemState();
        }
    }

    @Override
    protected void savePosition(long seekTime) {
        super.savePosition(seekTime);

        SharedPreferences.Editor editor = mSeekPositionSetting.edit();
        editor.putLong(String.format("%d-%d", mCourseId, mLessonId), seekTime);
        editor.commit();
    }
}
