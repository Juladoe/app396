package com.edusoho.kuozhi.ui.liveCourse;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.LiveLesson;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.ESExpandableTextView;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.soooner.EplayerPluginLibary.EplayerPluginActivity;
import com.soooner.EplayerPluginLibary.EplayerPluginPadActivity;
import com.soooner.EplayerPluginLibary.util.ActivityUtil;
import com.soooner.source.common.util.DeviceUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cn.trinea.android.common.util.SystemUtils;

/**
 * Created by onewoman on 2015/2/2.
 */
public class liveLessonFragment extends BaseFragment {
    private TextView mTvLiveTime;
    private ESExpandableTextView mTvLiveIntroduction;
    private TextView mTvLiveCountDown;
    private Button mLiveCourseClick;
    private ProgressDialog mProgressDialog;
//    private View mLoading;

    private int mLiveStartDay;
    private int mLiveStartHour;
    private int mLiveStartMin;
    private int mLiveStartSec;

    private int mCourseId;
    private int mLessonId;

    private long mNowTime;
    private long mLiveStartTime;
    private long mLiveEndTime;
    private long mLiveStartTimeDiff;
    private String mLiveIntroduction;
    private String mTitle;
    private String mReplayStatus;

    private static final int COUNTDOWN = 0;

    public static final String STARTTIME = "startTime";
    public static final String ENDTIME = "endTime";
    public static final String SUMMARY = "summary";
    public static final String REPLAYSTATUS = "replayStatus";
    @Override
    public String getTitle() {
        return null;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == COUNTDOWN) {
                setLiveCountDownTimeText(mLiveStartDay, mLiveStartHour, mLiveStartMin, mLiveStartSec);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.live_lesson_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        initDate();
        changeTitle(mTitle);

        mTvLiveTime = (TextView) view.findViewById(R.id.live_time);
        mTvLiveIntroduction = (ESExpandableTextView) view.findViewById(R.id.live_introduction);
        mTvLiveCountDown = (TextView) view.findViewById(R.id.live_count_down);
        mLiveCourseClick = (Button) view.findViewById(R.id.live_course_click);
        mProgressDialog = AppUtil.initProgressDialog(mActivity, "正在直播中");
        mProgressDialog.setCancelable(true);

        SimpleDateFormat startDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String liveStartDate = startDateFormat.format(new Date(mLiveStartTime));
        SimpleDateFormat endDateFormat = new SimpleDateFormat("HH:mm");
        String liveEndDate = endDateFormat.format(new Date(mLiveEndTime));

        mTvLiveTime.setText(String.format("%s ~ %s", liveStartDate, liveEndDate));
        mTvLiveIntroduction.setMyText(AppUtil.removeHtmlSpace(Html.fromHtml(AppUtil.removeImgTagFromString(mLiveIntroduction)).toString()));
        mLiveCourseClick.setOnClickListener(liveOnClickListener);
        showLiveCountDown();
    }

    private View.OnClickListener liveOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //跳转到直播课时
            getLiveCourseRequest(false, false);
        }
    };

    private View.OnClickListener replayOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //跳转到回看
            getLiveCourseRequest(true, false);
        }
    };

    public void initDate() {
        mNowTime = new Date().getTime();
        Bundle IntentData = getArguments();
        mTitle = IntentData.getString(Const.ACTIONBAR_TITLE);

        mLiveStartTime = IntentData.getLong(STARTTIME);
        mLiveEndTime = IntentData.getLong(ENDTIME);

        mCourseId = IntentData.getInt(Const.COURSE_ID);
        mLessonId = IntentData.getInt(Const.LESSON_ID);

        mLiveIntroduction = IntentData.getString(SUMMARY);
        mReplayStatus = IntentData.getString(REPLAYSTATUS);
    }

    public void showLiveCountDown() {
        if("generated".equals(mReplayStatus)){
            mLiveCourseClick.setText("点击回看");
            mLiveCourseClick.setOnClickListener(replayOnclickListener);
            return ;
        }
        if (liveEnd()) {
            mTvLiveCountDown.setText("直播已经结束");
            mLiveCourseClick.setVisibility(View.GONE);
        } else if ((mLiveStartTimeDiff = liveStart()) > 0) {
            mLiveStartTimeDiff = mLiveStartTimeDiff / 1000;
            getLiveCountDownTime();
            Timer timer = new Timer();
            timer.schedule(new CountDownTimeTask(), 1000, 1000);
        } else {
            //直播已经开始，自动进入教室
            getLiveCourseRequest(false, true);
        }
    }

    public boolean liveEnd() {
        return (mLiveEndTime - mNowTime) < 0;
    }

    public long liveStart() {
        return mLiveStartTime - mNowTime;
    }

    public void setLiveCountDownTimeText(int day, int hour, int min, int sec) {
        if(day != 0){
            mTvLiveCountDown.setText(String.format("倒计时:%d天%d小时%d分钟%d秒", day, hour, min, sec));
        }else if(hour != 0){
            mTvLiveCountDown.setText(String.format("倒计时:%d小时%d分钟%d秒", hour, min, sec));
        }else if(min != 0){
            mTvLiveCountDown.setText(String.format("倒计时:%d分钟%d秒", min, sec));
        }else{
            mTvLiveCountDown.setText(String.format("倒计时:%d秒", sec));
        }
    }

    public void getLiveCountDownTime() {
        mLiveStartDay = 0;
        mLiveStartHour = 0;
        mLiveStartMin = 0;
        mLiveStartSec = 0;
        if (mLiveStartTimeDiff < 60 && mLiveStartTimeDiff > 0) {
            mLiveStartSec = (int) mLiveStartTimeDiff;
        } else if (mLiveStartTimeDiff < 60 * 60) {
            mLiveStartMin = (int) (mLiveStartTimeDiff / 60);
            mLiveStartSec = (int) (mLiveStartTimeDiff % 60);
        } else if (mLiveStartTimeDiff < 60 * 60 * 24) {
            mLiveStartHour = (int) (mLiveStartTimeDiff / (60 * 60));
            mLiveStartMin = (int) (mLiveStartTimeDiff / 60 % 60);
            mLiveStartSec = (int) (mLiveStartTimeDiff % 60);
        } else {
            mLiveStartDay = (int) (mLiveStartTimeDiff / (60 * 60 * 24));
            mLiveStartHour = (int) (mLiveStartTimeDiff / (60 * 60) % 24);
            mLiveStartMin = (int) (mLiveStartTimeDiff / 60 % 60);
            mLiveStartSec = (int) (mLiveStartTimeDiff % 60);
        }
    }

    public class CountDownTimeTask extends TimerTask {
        @Override
        public void run() {
            if (mLiveStartTimeDiff > 0) {
                mLiveStartTimeDiff--;
            }
            getLiveCountDownTime();
            Message message = Message.obtain(handler, COUNTDOWN);
            handler.sendMessage(message);
        }
    }

    public void getLiveCourseRequest(final boolean replayState, final boolean autoLive) {
        RequestUrl url = app.bindUrl(Const.LIVE_COURSE, true);
        url.setParams(new String[]{
                "courseId", String.valueOf(mCourseId),
                "lessonId", String.valueOf(mLessonId)
        });

        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                if(autoLive){
                    mProgressDialog.show();
                    Handler handler1 = new Handler();
                    handler1.postAtTime(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.cancel();
                        }
                    }, SystemClock.uptimeMillis() + 500);
                }

                LiveLesson liveLesson = mActivity.parseJsonValue(object, new TypeToken<LiveLesson>() {
                });
                String param[] = liveLesson.data.result.url.split("&");
                int liveClassroomIdPoint = param[0].indexOf("liveClassroomId=");
                String liveClassroomId = param[0].substring(liveClassroomIdPoint + "liveClassroomId=".length());

                int exStrPoint = param[param.length - 1].indexOf("exStr=");
                String exStr = param[param.length - 1].substring(exStrPoint + "exStr=".length());
                Log.d(null, "liveClassroomId " + liveClassroomId);

                startLiveActivity(liveClassroomId, exStr, replayState);
            }
        });
    }

    private void startLiveActivity(String liveClassroomId, String exStr, boolean replayState)
    {
        Bundle bundle = new Bundle();
        bundle.putString(EplayerPluginActivity.EPLAY_EXSTR, exStr);
        bundle.putString(EplayerPluginActivity.EPLAY_LIVECLASSROOMID, liveClassroomId);
        bundle.putString(EplayerPluginActivity.EPLAY_CUSTOMER, "edusoho");
        if(replayState) {
            bundle.putString(EplayerPluginActivity.EPLAY_PID, null);
        }

        Intent intent = null;
        if(DeviceUtil.getDeviceType(mContext)== DeviceUtil.DEVICE_TYPE_PHONE){
            intent = new Intent(mContext, EplayerPluginActivity.class);
        }else{
            intent = new Intent(mContext, EplayerPluginPadActivity.class);
        }

        intent.putExtras(bundle);
        startActivity(intent);

    }
}
