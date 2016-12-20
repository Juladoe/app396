package com.edusoho.kuozhi.v3.ui.fragment.lesson;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonStatus;
import com.edusoho.kuozhi.v3.listener.LessonPluginCallback;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.LearnStatus;
import com.edusoho.kuozhi.v3.model.provider.LessonProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.DetailActivity;
import com.edusoho.kuozhi.v3.ui.MenuPop;
import com.edusoho.kuozhi.v3.ui.NoteActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.ExerciseOptionDialog;
import com.edusoho.videoplayer.ui.AudioPlayerFragment;
import com.edusoho.videoplayer.util.ControllerOptions;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suju on 16/12/18.
 */

public class LessonAudioPlayerFragment extends AudioPlayerFragment {

    public static final String COVER = "cover";

    private int mLessonId;
    private int mCourseId;
    private List<ExerciseOptionDialog.GridViewItem> mExerciseItemList;
    protected String mCoverUrl;
    protected float mAudioCoverAnimOffset;
    protected ObjectAnimator mAudioCoverAnim;
    private ImageView mCoverImageView;
    private DetailActivity mMenuCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCoverUrl = getArguments().getString(COVER);
        mLessonId = getArguments().getInt(Const.LESSON_ID);
        mCourseId = getArguments().getInt(Const.COURSE_ID);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMenuCallback = (DetailActivity) activity;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ControllerOptions options = new ControllerOptions.Builder()
                .addOption(ControllerOptions.RATE, false)
                .addOption(ControllerOptions.SCREEN, false)
                .build();
        mVideoControllerView.setControllerOptions(options);
        initPlayContainer();
        initNavigationBar();
        loadLessonStatus();
    }

    protected void initPlayContainer() {
        View containerView = LayoutInflater.from(getContext()).inflate(R.layout.view_audio_container_layout, null);
        setContainerView(containerView);
        mCoverImageView = (ImageView) containerView.findViewById(R.id.rl_audio_cover);
        ImageLoader.getInstance().displayImage(mCoverUrl, mCoverImageView);
        initCoverSize();
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

    private void initCoverSize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int parentWidth = wm.getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams lp = mCoverImageView.getLayoutParams();
        lp.width = parentWidth / 3;
        lp.height = parentWidth / 3;
        mCoverImageView.setLayoutParams(lp);
    }

    @Override
    public void onPlayStatusChange(boolean isPlay) {
        super.onPlayStatusChange(isPlay);
        updateAudioCoverViewStatus(isPlay);
    }

    private void updateAudioCoverViewStatus(boolean isPlay) {
        if (mAudioCoverAnim == null) {
            mAudioCoverAnim = ObjectAnimator.ofFloat(mCoverImageView, "rotation", 0f, 359f);
            mAudioCoverAnim.setDuration(10000);
            mAudioCoverAnim.setInterpolator(new LinearInterpolator());
            mAudioCoverAnim.setRepeatCount(-1);
        }
        if (isPlay) {
            mAudioCoverAnim.setFloatValues(mAudioCoverAnimOffset, mAudioCoverAnimOffset + 359f);
            mAudioCoverAnim.start();
        } else {
            mAudioCoverAnimOffset = (float) mAudioCoverAnim.getAnimatedValue();
            mAudioCoverAnim.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAudioCoverAnim != null) {
            mAudioCoverAnim.cancel();
            mAudioCoverAnim = null;
        }
    }
}
