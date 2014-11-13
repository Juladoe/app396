package com.edusoho.plugin.video;

import android.app.Activity;
import android.util.Log;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.ui.course.LessonActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.NormalCallback;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.plugin.edusoho.bdvideoplayer.BdVideoPlayerFragment;

/**
 * Created by howzhi on 14-10-25.
 */
public class CustomVideoFragment extends BdVideoPlayerFragment {

    @Override
    protected void resumePlay() {
        /**
         * 发起一次播放任务,当然您不一定要在这发起
         */
        if (mLastPos > 0) {
            Log.d(null, "resumePlay--->");
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

    private void reloadLessonMediaUrl(final NormalCallback<LessonItem> callback)
    {
        final LessonActivity lessonActivity = (LessonActivity) getActivity();
        RequestUrl requestUrl = lessonActivity.app.bindUrl(Const.COURSELESSON, true);

        requestUrl.setParams(new String[] {
                "courseId", String.valueOf(lessonActivity.getCourseId()),
                "lessonId", String.valueOf(lessonActivity.getLessonId())
        });

        lessonActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                LessonItem lessonItem = lessonActivity.parseJsonValue(
                        object, new TypeToken<LessonItem<String>>() {
                });
                if (lessonItem == null) {
                    showErrorDialog(lessonActivity);
                    return;
                }

                callback.success(lessonItem);
            }
        });
    }

    private void showErrorDialog(Activity activity)
    {
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
