package com.edusoho.kuozhi.v3.ui.fragment.lesson;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import com.baidu.cyberplayer.core.BVideoView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.FragmentPageActivity;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.view.dialog.ExitCoursePopupDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.plugin.edusoho.bdvideoplayer.BdVideoPlayerFragment;
import cn.trinea.android.common.util.PreferencesUtils;

/**
 * Created by howzhi on 14-9-26.
 */
public class VideoLessonFragment extends BdVideoPlayerFragment {

    public static final String PLAYER_POSITION_PREF = "lesson_video_seek";
    ActionBarBaseActivity lessonActivity = null;
    PopupDialog backPopupDialog = null;
    private static final int NO_LESSON = 10001;
    private boolean isDialogShowed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lessonActivity = (FragmentPageActivity) getActivity();
        mDecodeMode = getMediaCoderType() == 0 ? BVideoView.DECODE_SW : BVideoView.DECODE_HW;
        if (!isCacheVideo && !AppUtil.isWiFiConnect(getActivity()) && lessonActivity.app.config.offlineType == 0) {
            PopupDialog popupDialog = PopupDialog.createMuilt(lessonActivity,
                    lessonActivity.getString(R.string.notification),
                    lessonActivity.getString(R.string.player_4g_info), new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            isDialogShowed = false;
                            if (button == PopupDialog.CANCEL) {
                                lessonActivity.finish();
                            } else {
                                lessonActivity.app.config.offlineType = 1;
                                lessonActivity.app.saveConfig();
                                resumePlay();
                            }
                        }
                    });
            popupDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        lessonActivity.finish();
                    }
                    return false;
                }
            });
            popupDialog.setOkText(lessonActivity.getString(R.string.yes));
            popupDialog.setCancelText(lessonActivity.getString(R.string.no));
            popupDialog.setCanceledOnTouchOutside(false);
            popupDialog.show();
            isDialogShowed = true;
        }
        setCurrentPos(PreferencesUtils.getInt(PLAYER_POSITION_PREF, lessonActivity, lessonActivity.app.loginUser.id + ":" + lessonActivity.app.domain + "/api/lessons/" + mLessonId, 0));
    }

    protected int getMediaCoderType() {
        SharedPreferences sp = lessonActivity.getSharedPreferences("mediaCoder", Context.MODE_PRIVATE);
        return sp.getInt("type", 0);
    }

    private void setViewStatus(boolean isHidden) {
        ivLearnStatus.setVisibility(View.INVISIBLE);
        ivShare.setVisibility(View.INVISIBLE);
        ivQuestion.setVisibility(View.INVISIBLE);
        ivNote.setVisibility(View.INVISIBLE);
        tvLearn.setVisibility(View.INVISIBLE);
        tvStreamType.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        tvVideoTitle.setText(mLessonName);

        setViewStatus(isCacheVideo);
        backPopupDialog = PopupDialog.createMuilt(lessonActivity, getString(R.string.player_exit_dialog_title), getString(R.string.player_exit_dialog_msg), new PopupDialog.PopupClickListener() {
            @Override
            public void onClick(int button) {
                if (button == PopupDialog.OK) {
                    getActivity().onBackPressed();
                }
            }
        });
        backPopupDialog.setOkText("退出");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        if (getView() != null) {
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            backPopupDialog.show();
                        }
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void resumePlay() {
        /**
         * 发起一次播放任务,当然您不一定要在这发起
         */
        if (isCacheVideo) {
            mEventHandler.sendEmptyMessage(EVENT_START);
            return;
        }

        Log.d(TAG, "resumePlay: " + mCurMediaSource);
        if (isDialogShowed) {
            return;
        }
        if (mCurMediaSource != null) {
            mEventHandler.sendEmptyMessage(EVENT_START);
        }
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
        PreferencesUtils.putInt(PLAYER_POSITION_PREF, lessonActivity, lessonActivity.app.loginUser.id + ":" + lessonActivity.app.domain + "/api/lessons/" + mLessonId, getCurrentPos());
    }

    protected View createNetErrorView() {
        return LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.view_net_error_layout, null);
    }

    /**
     * 100
     *
     * @param what
     * @param extra
     */
    @Override
    protected void showErrorDialog(int what, int extra) {
        Log.d(getClass().getSimpleName(), String.format("what：%d, extra：%d", what, extra));
        if (!isCacheVideo) {
            mViewContainerView.addView(createNetErrorView());
            return;
        }
        if (what == NO_LESSON) {
            PopupDialog popupDialog = PopupDialog.createNormal(
                    getActivity(), "播放提示", "课时不存在");
            popupDialog.setOkListener(new PopupDialog.PopupClickListener() {
                @Override
                public void onClick(int button) {
                    getActivity().finish();
                }
            });
            popupDialog.show();
            return;
        }
        if (what == 100 || what == -38) {
            resumePlay();
            return;
        }
        showSwiftMediaCoderDlg();
    }

    protected ExitCoursePopupDialog getMediaCoderInfoDlg() {
        ExitCoursePopupDialog dialog = ExitCoursePopupDialog.createNormal(
                getActivity(), "视频播放选择", new ExitCoursePopupDialog.PopupClickListener() {
                    @Override
                    public void onClick(int button, int position, String selStr) {
                        if (button == ExitCoursePopupDialog.CANCEL) {
                            return;
                        }

                        saveMediaCoderType(position);
                    }
                }
        );
        dialog.setStringArray(R.array.mediacoder_array);
        return dialog;
    }

    protected void saveMediaCoderType(int type) {
        SharedPreferences sp = getActivity().getSharedPreferences("mediaCoder", Context.MODE_PRIVATE);
        sp.edit().putInt("type", type).commit();
    }

    protected void showSwiftMediaCoderDlg() {
        PopupDialog popupDialog = PopupDialog.createMuilt(
                getActivity(),
                "播放提示",
                "视频播放出了点问题\n可以去尝试设置里选择播放器解码方式解决",
                new PopupDialog.PopupClickListener() {
                    @Override
                    public void onClick(int button) {
                        if (button == PopupDialog.OK) {
                            getMediaCoderInfoDlg().show();
                        }
                    }
                });
        popupDialog.setOkText("去设置");
        popupDialog.show();
    }

    @Override
    protected void setPlayerFunctionButton(int visibility) {
        //none
    }
}
