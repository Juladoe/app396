package com.edusoho.kuozhi.v3.view.video;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.baidu.cyberplayer.core.BVideoView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.ExitCoursePopupDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.google.gson.reflect.TypeToken;
import com.plugin.edusoho.bdvideoplayer.BdVideoPlayerFragment;

/**
 * Created by howzhi on 14-10-25.
 */
public class CustomVideoFragment extends BdVideoPlayerFragment {

    private static final int NO_LESSON = 10010;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDecodeMode = getMediaCoderType() == 0 ? BVideoView.DECODE_SW : BVideoView.DECODE_HW;
    }

    protected int getMediaCoderType() {
        SharedPreferences sp = getActivity().getSharedPreferences("mediaCoder", Context.MODE_PRIVATE);
        return sp.getInt("type", 0);
    }

    @Override
    protected void resumePlay() {
        /**
         * 发起一次播放任务,当然您不一定要在这发起
         */
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

    private void reloadLessonMediaUrl(final NormalCallback<LessonItem> callback) {
        final LessonActivity lessonActivity = (LessonActivity) getActivity();
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
                    showErrorDialog(NO_LESSON, 0);
                    return;
                }

                callback.success(lessonItem);
            }
        }, null);
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
}
