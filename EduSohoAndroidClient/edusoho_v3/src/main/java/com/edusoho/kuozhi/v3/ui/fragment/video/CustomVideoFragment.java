package com.edusoho.kuozhi.v3.ui.fragment.video;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.google.gson.reflect.TypeToken;
import com.plugin.edusoho.bdvideoplayer.BdVideoPlayerFragment;

import java.util.HashMap;

/**
 * Created by howzhi on 14-10-25.
 */
public class CustomVideoFragment extends BdVideoPlayerFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    LessonActivity lessonActivity = null;

    @Override
    protected void initView(View view) {
        super.initView(view);
        if (getActivity() instanceof LessonActivity) {
            lessonActivity = (LessonActivity) getActivity();
        }
        ivBack.setOnClickListener(this);
        chkLearned.setOnCheckedChangeListener(this);
        ivShare.setOnClickListener(this);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.isChecked()) {
            buttonView.setEnabled(false);
        }
        lessonActivity.changeLessonStatus(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ivBack.getId()) {
            getActivity().onBackPressed();
        } else if (v.getId() == ivShare.getId()) {
            RequestUrl requestUrl = lessonActivity.app.bindUrl(Const.COURSE, false);
            HashMap<String, String> params = requestUrl.getParams();
            params.put("courseId", lessonActivity.getCourseId() + "");
            lessonActivity.app.postUrl(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    CourseDetailsResult courseDetailsResult = lessonActivity.parseJsonValue(response, new TypeToken<CourseDetailsResult>() {
                    });
                    if (courseDetailsResult != null && courseDetailsResult.course != null) {
                        Course course = courseDetailsResult.course;
                        String title = course.title;
                        String about = course.about;
                        String pic = course.middlePicture;
                        String url = lessonActivity.app.host + "/course/" + lessonActivity.getCourseId();
                        final ShareTool shareTool = new ShareTool(lessonActivity, url, title, about, pic);
                        new Handler((lessonActivity.getMainLooper())).post(new Runnable() {
                            @Override
                            public void run() {
                                shareTool.shardCourse();
                            }
                        });
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }


}
