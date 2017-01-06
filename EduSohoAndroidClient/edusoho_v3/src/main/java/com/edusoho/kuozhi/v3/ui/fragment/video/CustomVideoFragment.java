package com.edusoho.kuozhi.v3.ui.fragment.video;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.cyberplayer.core.BVideoView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.shard.ShardDialog;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.lesson.LessonStatus;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.LearnStatus;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModel;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.NoteActivity;
import com.edusoho.kuozhi.v3.ui.ThreadActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.DataUsageUploadUtil;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.view.dialog.ExitCoursePopupDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.google.gson.reflect.TypeToken;
import com.plugin.edusoho.bdvideoplayer.BdVideoPlayerFragment;
import com.plugin.edusoho.bdvideoplayer.StreamInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.trinea.android.common.util.PreferencesUtils;


/**
 * Created by howzhi on 14-10-25.
 */
public class CustomVideoFragment extends BdVideoPlayerFragment implements CompoundButton.OnCheckedChangeListener,
        View.OnClickListener, BdVideoPlayerFragment.LessonLearnStatus {

    public static final String PLAYER_POSITION_PREF = "lesson_video_seek";
    LessonActivity lessonActivity = null;
    PopupDialog backPopupDialog = null;
    private static final int NO_LESSON = 10001;
    private static final int HEAD_ERROR = 10002;
    private boolean isDialogShowed = false;

    List<StreamInfo> streamInfoLists = new ArrayList<>();
    private DataUsageUploadUtil dataUsageUploadUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof LessonActivity) {
            lessonActivity = (LessonActivity) getActivity();
        }
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
        if (isHidden) {
            ivLearnStatus.setVisibility(View.INVISIBLE);
            ivShare.setVisibility(View.INVISIBLE);
            ivQuestion.setVisibility(View.INVISIBLE);
            ivNote.setVisibility(View.INVISIBLE);
            tvLearn.setVisibility(View.INVISIBLE);
            tvStreamType.setVisibility(View.VISIBLE);
            tvStreamType.setEnabled(false);
            tvStreamType.setText("缓存");
        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        ivBack.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        tvStreamType.setOnClickListener(this);
        ivLearnStatus.setOnClickListener(this);
        tvLearn.setOnClickListener(this);
        ivQuestion.setOnClickListener(this);
        ivNote.setOnClickListener(this);
        tvVideoTitle.setText(mLessonName);

        setViewStatus(isCacheVideo);
        RequestUrl requestUrl = lessonActivity.app.bindUrl(Const.LESSON_STATUS, true);
        requestUrl.setParams(new String[]{
                "courseId", mCourseId + "",
                "lessonId", mLessonId + ""
        });

        lessonActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LessonStatus mLessonStatus = lessonActivity.parseJsonValue(
                        response, new TypeToken<LessonStatus>() {
                        });
                if (mLessonStatus != null && mLessonStatus.learnStatus == LearnStatus.finished) {
                    mLearnStatus = true;
                } else {
                    mLearnStatus = false;
                }
                if (mLearnStatus) {
                    ivLearnStatus.setImageResource(R.drawable.icon_learn);
                } else {
                    ivLearnStatus.setImageResource(R.drawable.icon_unlearn);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mLearnStatus = false;
            }
        });
        mLearnStatusChanged = this;
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
    public void onPrepared() {
        super.onPrepared();
        if (isCacheVideo) {
            dataUsageUploadUtil = new DataUsageUploadUtil(mLessonId, mDurationCount, lessonActivity);
            dataUsageUploadUtil.startTimer();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

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
        } else {
            if (!TextUtils.isEmpty(mVideoHead)) {
                getVideoHeadStream(mVideoHead, new NormalCallback<StreamInfo>() {
                    @Override
                    public void success(StreamInfo headStream) {
                        if (headStream != null) {
                            mCurMediaHeadSource = headStream.src;
                        }
                        if (!TextUtils.isEmpty(mVideoSource)) {
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
                                        showErrorDialog(NO_LESSON, 0);
                                    }
                                }
                            });
                        } else {
                            showErrorDialog(NO_LESSON, 0);
                        }
                    }
                });
            } else if (!TextUtils.isEmpty(mVideoSource)) {
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
                            showErrorDialog(NO_LESSON, 0);
                        }
                    }
                });
            } else {
                showErrorDialog(NO_LESSON, 0);
            }
        }
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
        PreferencesUtils.putInt(PLAYER_POSITION_PREF, lessonActivity, lessonActivity.app.loginUser.id + ":" + lessonActivity.app.domain + "/api/lessons/" + mLessonId, getCurrentPos());
    }

    private void getVideoHeadStream(String url, final NormalCallback<StreamInfo> normalCallback) {
        RequestUrl requestUrl = lessonActivity.app.bindNewApiUrl(url, true);
        requestUrl.url = url;
        lessonActivity.ajaxGet(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                StreamInfo[] streamInfos = lessonActivity.app.parseJsonValue(response, new TypeToken<StreamInfo[]>() {
                });
                if (streamInfos != null && streamInfos.length > 0) {
                    normalCallback.success(streamInfos[0]);
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.isChecked()) {
            buttonView.setEnabled(false);
        }
        lessonActivity.changeLessonStatus(true);
    }

    private void backActivity() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            backPopupDialog.show();
        } else {
            try {
                if (lessonActivity != null) {
                    lessonActivity.onBackPressed();
                }
            } catch (Exception ex) {
                Log.d(TAG, "backActivity: " + ex.getMessage());
            }

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ivBack.getId()) {
            backActivity();
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
                        CustomVideoFragment.this.pause();
                        shareTool.setDismissEvent(new ShardDialog.DismissEvent() {
                            @Override
                            public void afterDismiss() {
                                CustomVideoFragment.this.resume();
                            }
                        });
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
        } else if (v.getId() == tvStreamType.getId()) {
            showPopupWindows();
        } else if ((v.getId() == ivLearnStatus.getId() || v.getId() == tvLearn.getId()) && !mLearnStatus) {
            if (mLearnStatusChanged != null) {
                mLearnStatusChanged.setLearnStatus();
            }
        } else if (v.getId() == ivQuestion.getId()) {
            Intent intent = new Intent();
            intent.setClass(lessonActivity, ThreadActivity.class);
            intent.putExtra(Const.LESSON_ID, mLessonId);
            intent.putExtra(Const.COURSE_ID, mCourseId);
            startActivity(intent);
            lessonActivity.overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_up);
        } else if (v.getId() == ivNote.getId()) {
            Intent intent = new Intent();
            intent.setClass(lessonActivity, NoteActivity.class);
            intent.putExtra(Const.LESSON_ID, mLessonId);
            intent.putExtra(Const.COURSE_ID, mCourseId);
            startActivity(intent);
            lessonActivity.overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_up);
        }
    }

    @Override
    public void setLearnStatus() {
        RequestUrl requestUrl = lessonActivity.app.bindUrl(Const.LEARN_LESSON, true);
        requestUrl.setParams(new String[]{
                Const.COURSE_ID, mCourseId + "",
                Const.LESSON_ID, mLessonId + ""
        });

        lessonActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LearnStatus result = lessonActivity.parseJsonValue(response, new TypeToken<LearnStatus>() {
                });
                if (result == null) {
                    return;
                }
                ivLearnStatus.setImageResource(R.drawable.icon_learn);
                tvLearn.setTextColor(lessonActivity.getResources().getColor(R.color.white));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ivLearnStatus.setImageResource(R.drawable.icon_unlearn);
                tvLearn.setTextColor(lessonActivity.getResources().getColor(R.color.grey));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dataUsageUploadUtil == null) {
            return;
        }
        dataUsageUploadUtil.stopTimer();
        String uploadUrl = null;
        try {
            uploadUrl = URLEncoder.encode(dataUsageUploadUtil.getUploadUrl(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (uploadUrl != null && !("").equals(uploadUrl)) {
            lessonActivity.ajaxGet(String.format(Const.UPLOAD_SAVED_DATA_USAGE, dataUsageUploadUtil.getDataUsageSave(), uploadUrl), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }
}

