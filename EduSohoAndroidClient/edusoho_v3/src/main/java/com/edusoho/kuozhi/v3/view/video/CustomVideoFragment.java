package com.edusoho.kuozhi.v3.view.video;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.google.gson.reflect.TypeToken;
import com.plugin.edusoho.bdvideoplayer.BdVideoPlayerFragment;

/**
 * Created by howzhi on 14-10-25.
 */
public class CustomVideoFragment extends BdVideoPlayerFragment {

    private LessonActivity lessonActivity;
    private boolean isDialogShow = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lessonActivity = (LessonActivity) getActivity();
        if (AppUtil.isWiFiConnect(getActivity()) && lessonActivity.app.config.offlineType == 0) {
            PopupDialog popupDialog = PopupDialog.createMuilt(lessonActivity,
                    lessonActivity.getString(R.string.notification),
                    lessonActivity.getString(R.string.player_4g_info), new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            isDialogShow = false;
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
            isDialogShow = true;
        }
    }

    @Override
    protected void resumePlay() {
        /**
         * 发起一次播放任务,当然您不一定要在这发起
         */
        if (!isDialogShow) {
            if (mLastPos > 0) {
                Log.d(null, "resumePlay--->");
                if (isCacheVideo) {
                    mEventHandler.sendEmptyMessage(EVENT_START);
                    return;
                }
                reloadLessonMediaUrl(new NormalCallback<LessonItem>() {
                    @Override
                    public void success(LessonItem lessonItem) {
                        mVideoHead = lessonItem.headUrl;
                        mVideoSource = lessonItem.mediaUri;
                        mEventHandler.sendEmptyMessage(EVENT_START);
                    }
                });
            } else {
                mEventHandler.sendEmptyMessage(EVENT_START);
            }
        }
    }

    private void reloadLessonMediaUrl(final NormalCallback<LessonItem> callback) {

        RequestUrl requestUrl = lessonActivity.app.bindUrl(Const.COURSELESSON, true);

        requestUrl.setParams(new String[]{
                "courseId", String.valueOf(lessonActivity.getCourseId()),
                "lessonId", String.valueOf(lessonActivity.getLessonId())
        });

        lessonActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LessonItem lessonItem = lessonActivity.parseJsonValue(
                        response, new TypeToken<LessonItem<String>>() {
                        });
                if (lessonItem == null) {
                    showErrorDialog(lessonActivity);
                    return;
                }

                callback.success(lessonItem);
            }
        }, null);
    }

    private void showErrorDialog(Activity activity) {
        PopupDialog popupDialog = PopupDialog.createNormal(
                activity, "播放提示", "该视频播放出现了问题！请联系网站管理员!");
        popupDialog.setOkListener(new PopupDialog.PopupClickListener() {
            @Override
            public void onClick(int button) {
                getActivity().finish();
            }
        });
        popupDialog.show();
    }
}
