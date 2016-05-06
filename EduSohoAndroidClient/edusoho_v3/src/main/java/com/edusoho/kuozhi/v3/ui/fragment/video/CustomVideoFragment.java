package com.edusoho.kuozhi.v3.ui.fragment.video;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import com.plugin.edusoho.bdvideoplayer.StreamInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * Created by howzhi on 14-10-25.
 */
public class CustomVideoFragment extends BdVideoPlayerFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    LessonActivity lessonActivity = null;
    private static final String SD = "SD";
    private static final String HD = "HD";
    private static final String SHD = "SHD";

    List<StreamInfo> streamInfoLists = new ArrayList<>();

    @Override
    protected void initView(View view) {
        super.initView(view);
        ivBack.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        tvStreamType.setOnClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof LessonActivity) {
            lessonActivity = (LessonActivity) getActivity();
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
        if (mCurMediaSource != null) {
            mEventHandler.sendEmptyMessage(EVENT_START);
        } else {
            if (mVideoSource != null) {
                getVideoStream(mVideoSource, new NormalCallback<StreamInfo[]>() {
                    @Override
                    public void success(StreamInfo[] streamInfos) {
                        if (streamInfos != null) {
                            for (StreamInfo streamInfo : streamInfos) {
                                streamInfoLists.add(streamInfo);
                            }
                            initPopupWindows(streamInfoLists);
                            mCurMediaSource = streamInfoLists.get(0).src;
                            mEventHandler.sendEmptyMessage(EVENT_START);
                        } else {
                            showErrorDialog(lessonActivity);
                        }
                    }
                });
            } else {
                showErrorDialog(lessonActivity);
            }
        }
//
//        if (mVideoSource != null && streamInfoLists.size() == 0) {
//            getVideoStream(mVideoSource, new NormalCallback<StreamInfo[]>() {
//                @Override
//                public void success(StreamInfo[] streamInfos) {
//                    if (streamInfos != null) {
//                        for (StreamInfo streamInfo : streamInfos) {
//                            streamInfoLists.add(streamInfo);
//                        }
//                        setStreamTypeBtnStatus();
//                        setCurMediaSource();
//                        if (mLastPos > 0) {
//                            reloadLessonMediaUrl(new NormalCallback<LessonItem>() {
//                                @Override
//                                public void success(LessonItem lessonItem) {
//                                    mVideoHead = lessonItem.headUrl;
//                                    mVideoSource = lessonItem.mediaUri;
//                                    mEventHandler.sendEmptyMessage(EVENT_START);
//                                }
//                            });
//                        } else {
//                            mEventHandler.sendEmptyMessage(EVENT_START);
//                        }
//
//                    } else {
//                        showErrorDialog(lessonActivity);
//                    }
//                }
//            });
//        } else {
//            mEventHandler.sendEmptyMessage(EVENT_START);
//        }
    }

    private void getVideoStream(String url, final NormalCallback<StreamInfo[]> normalCallback) {
        RequestUrl requestUrl = lessonActivity.app.bindNewApiUrl(url, true);
        requestUrl.url = url;
        lessonActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                StreamInfo[] streamInfos = lessonActivity.app.parseJsonValue(response, new TypeToken<StreamInfo[]>() {
                });
                if (streamInfos != null && streamInfos.length > 0) {
                    normalCallback.success(streamInfos);
                } else {
                    normalCallback.success(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                normalCallback.success(null);
            }
        });
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
                        final ShareTool shareTool = new ShareTool(lessonActivity, url, title, about, pic, 2);
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
//        } else if (v.getId() == tvSDVideo.getId()) {
//            recordCurrentPosition();
//            mCurMediaSource = tvSDVideo.getTag().toString();
//            mEventHandler.sendEmptyMessage(EVENT_REPLAY);
//        } else if (v.getId() == tvHDVideo.getId()) {
//            recordCurrentPosition();
//            mCurMediaSource = tvHDVideo.getTag().toString();
//            mEventHandler.sendEmptyMessage(EVENT_REPLAY);
//            recordCurrentPosition();
//        } else if (v.getId() == tvSHDVideo.getId()) {
//
//            mCurMediaSource = tvSHDVideo.getTag().toString();
//            mEventHandler.sendEmptyMessage(EVENT_REPLAY);
        } else if (v.getId() == tvStreamType.getId()) {
            showPopupWindows();
        }
    }

    public void setStreamTypeBtnStatus() {
        Iterator<StreamInfo> iterator = streamInfoLists.iterator();
        while (iterator.hasNext()) {
            StreamInfo streamInfo = iterator.next();
//            if (SD.equals(streamInfo.name.toUpperCase())) {
//                tvSDVideo.setVisibility(View.VISIBLE);
//                tvSDVideo.setTag(streamInfo.src);
//                tvSDVideo.setOnClickListener(this);
//            } else if (HD.equals(streamInfo.name.toUpperCase())) {
//                tvHDVideo.setVisibility(View.VISIBLE);
//                tvHDVideo.setTag(streamInfo.src);
//                tvHDVideo.setOnClickListener(this);
//            } else if (SHD.equals(streamInfo.name.toUpperCase())) {
//                tvSHDVideo.setVisibility(View.VISIBLE);
//                tvSHDVideo.setTag(streamInfo.src);
//                tvSHDVideo.setOnClickListener(this);
//            }
        }
    }

    private void setCurMediaSource() {
//        if (tvSDVideo.getTag() != null) {
//            mCurMediaSource = String.valueOf(tvSDVideo.getTag());
//        } else if (tvHDVideo.getTag() != null) {
//            mCurMediaSource = String.valueOf(tvHDVideo.getTag());
//        } else if (tvSHDVideo.getTag() != null) {
//            mCurMediaSource = String.valueOf(tvSHDVideo.getTag());
//        }
    }
}

