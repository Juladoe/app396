package com.edusoho.kuozhi.v3.ui.fragment.video;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.lesson.LessonStatus;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.LearnStatus;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.NoteActivity;
import com.edusoho.kuozhi.v3.ui.ThreadActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.google.gson.reflect.TypeToken;
import com.plugin.edusoho.bdvideoplayer.BdVideoPlayerFragment;
import com.plugin.edusoho.bdvideoplayer.StreamInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by howzhi on 14-10-25.
 */
public class CustomVideoFragment extends BdVideoPlayerFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, BdVideoPlayerFragment.LessonLearnStatus {

    LessonActivity lessonActivity = null;
    PopupDialog backPopupDialog = null;
    private static final int NO_LESSON = 10001;
    private static final int HEAD_ERROR = 10002;

    List<StreamInfo> streamInfoLists = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof LessonActivity) {
            lessonActivity = (LessonActivity) getActivity();
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
                    isBackPressed = true;
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
                        backPopupDialog.show();
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

    /**
     * 100
     *
     * @param what
     * @param extra
     */
    @Override
    protected void showErrorDialog(int what, int extra) {
        Log.d(getClass().getSimpleName(), String.format("what：%d, extra：%d", what, extra));
        String content = "该视频播放出现了问题！请联系网站管理员!";
        if (what == NO_LESSON) {
            content = "课时不存在!";
        }
        if (what == 100) {
            return;
        }
        PopupDialog popupDialog = PopupDialog.createNormal(
                getActivity(), "播放提示", content);
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
            backPopupDialog.show();
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
                tvLearn.setTextColor(getResources().getColor(android.R.color.white));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ivLearnStatus.setImageResource(R.drawable.icon_unlearn);
                tvLearn.setTextColor(getResources().getColor(R.color.grey));
            }
        });
    }
}

