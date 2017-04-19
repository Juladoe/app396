package com.gensee.player;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.gensee.common.ServiceType;
import com.gensee.entity.ChatMsg;
import com.gensee.entity.DocInfo;
import com.gensee.entity.InitParam;
import com.gensee.entity.QAMsg;
import com.gensee.entity.VodObject;
import com.gensee.fragement.VodDocFragment;
import com.gensee.media.VODPlayer;
import com.gensee.pdu.GSDocView;
import com.gensee.taskret.OnTaskRet;
import com.gensee.view.GSVideoView;
import com.gensee.vod.VodSite;
import java.util.List;


/**
 * Created by suju on 16/7/1.
 */
public class GenseeVodPlayerActivity extends AppCompatActivity implements
        VodSite.OnVodListener, VODPlayer.OnVodPlayListener, GSDocView.OnDocViewEventListener {

    private VodSite vodSite;
    private VODPlayer mVodPlayer;
    private GSVideoView mGSVideoView;
    private static final int DURITME = 2000;
    private static final String DURATION = "DURATION";

    @Override
    public void onDocInfo(List<DocInfo> list) {
    }

    public interface RESULT {;
        int ON_GET_VODOBJ = 100;
        int HIDE_CONTROLLER = 101;
        int SHOW_CONTROLLER = 102;
        int SHOW_VIDEO_LOAD = 103;
        int HIDE_VIDEO_LOAD = 104;
    }

    interface MSG {
        int MSG_ON_INIT = 1;
        int MSG_ON_STOP = 2;
        int MSG_ON_POSITION = 3;
        int MSG_ON_VIDEOSIZE = 4;
        int MSG_ON_PAGE = 5;
        int MSG_ON_SEEK = 6;
        int MSG_ON_AUDIOLEVEL = 7;
        int MSG_ON_ERROR = 8;
        int MSG_ON_PAUSE = 9;
        int MSG_ON_RESUME = 10;
    }

    private ServiceType serviceType = ServiceType.ST_TRAINING;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private Context mContext;
    private SeekBar mSeekBar;
    private ProgressBar mLoadView;
    private TextView mTotalView;
    private TextView mTimeView;
    private ImageView mPlayBtn;
    private CheckBox mScreenBtn;
    private View mControllerView;
    private View mDocLayoutView;
    private VodDocFragment mVodDocFragment;
    private android.support.v4.app.FragmentManager mFragmentManager;

    private String mDomain;
    private String mRoomNumber;
    private String mToken;
    private String mLoginAccount;
    private String mLoginPwd;
    private String mNickname;
    private String mKey;
    private String mServiceType;

    private int VIEDOPAUSEPALY = 0;
    private int lastPostion = 0;
    private boolean isTouch = false;
    private boolean isPlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_layout);
        mContext = getBaseContext();

        mFragmentManager = getSupportFragmentManager();
        VodSite.init(this, new OnTaskRet() {
            @Override
            public void onTaskRet(boolean arg0, int arg1, String arg2) {
            }
        });
        initWidget();
        getLiveReplayInfo(getIntent().getExtras());
    }

    public void initWidget() {

        mVodPlayer = new VODPlayer();
        mVodDocFragment = new VodDocFragment(mVodPlayer);
        // Set up the ViewPager with the sections adapter
        mGSVideoView = (GSVideoView) findViewById(R.id.gsvideoview);
        mGSVideoView.setRenderMode(GSVideoView.RenderMode.RM_FILL_CENTER_CROP);
        mSeekBar = (SeekBar) findViewById(R.id.media_progress);
        mTimeView = (TextView) findViewById(R.id.time_current);
        mTotalView = (TextView) findViewById(R.id.time_total);
        mPlayBtn = (ImageView) findViewById(R.id.play_btn);
        mLoadView = (ProgressBar) findViewById(R.id.iv_live_progressbar);
        mScreenBtn = (CheckBox) findViewById(R.id.iv_play_screen);
        mControllerView = findViewById(R.id.fl_controller);
        mDocLayoutView = findViewById(R.id.fl_doc_layout);

        mScreenBtn.setOnCheckedChangeListener(mScreenClickListener);

        mPlayBtn.setOnClickListener(mPlayClickListener);
        mGSVideoView.setOnClickListener(mVideoViewClickListener);
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl_doc_layout, mVodDocFragment, "doc_fragment");
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                changeScreenToPortrait();
                return true;
            }
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showControllerView() {
        mControllerView.setVisibility(View.VISIBLE);
        mHandler.removeMessages(RESULT.HIDE_CONTROLLER);
        mHandler.sendEmptyMessageDelayed(RESULT.HIDE_CONTROLLER, 5000);
        getSupportActionBar().show();
    }

    private void hideControllerView() {
        mControllerView.setVisibility(View.GONE);
        getSupportActionBar().hide();
    }

    private View.OnClickListener mVideoViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mControllerView.getVisibility() == View.VISIBLE) {
                mHandler.removeMessages(RESULT.HIDE_CONTROLLER);
                hideControllerView();
                return;
            }
            showControllerView();
        }
    };

    private CompoundButton.OnCheckedChangeListener mScreenClickListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            int orientation = isChecked ? Configuration.ORIENTATION_LANDSCAPE : Configuration.ORIENTATION_PORTRAIT;
            int currentOrientation = getResources().getConfiguration().orientation;
            if (orientation == currentOrientation) {
                return;
            }
            if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                changeScreenToLandspace();
            } else  {
                changeScreenToPortrait();
            }
        }
    };

    private void changeScreenToPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View containerView = findViewById(R.id.frame_container);
        ViewGroup.LayoutParams lp = containerView.getLayoutParams();
        lp.height = getResources().getDimensionPixelOffset(R.dimen.video_height);
        mDocLayoutView.setVisibility(View.VISIBLE);
        containerView.setLayoutParams(lp);
    }

    private void changeScreenToLandspace() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        View containerView = findViewById(R.id.frame_container);
        ViewGroup.LayoutParams lp = containerView.getLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mDocLayoutView.setVisibility(View.INVISIBLE);
        containerView.setLayoutParams(lp);
    }

    private View.OnClickListener mPlayClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isPlay) {
                mVodPlayer.pause();
                myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_PAUSE, 0));
            } else {
                mVodPlayer.resume();
                myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_RESUME, 0));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(RESULT.HIDE_CONTROLLER);
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {
            if (fromUser) {
                mVodPlayer.seekTo(position);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void getLiveReplayInfo(Bundle liveBundle) {
        mDomain = liveBundle.getString("domain");
        mRoomNumber = liveBundle.getString("roomNumber");
        mLoginAccount = liveBundle.getString("loginAccount");
        mLoginPwd = liveBundle.getString("loginPwd");
        mToken = liveBundle.getString("vodPwd");
        mNickname = liveBundle.getString("nickName");
        mServiceType = liveBundle.getString("serviceType");
        mKey = liveBundle.getString("k");
        initVoidParam();
    }

    private void initPlayer(String vodId) {
        if (vodId == null) {
            Toast.makeText(this, "路径不对", Toast.LENGTH_SHORT).show();
            return;
        }

        mVodPlayer.setGSVideoView(mGSVideoView);
        mVodPlayer.play(vodId, this, "", false);
        mHandler.sendEmptyMessage(RESULT.SHOW_VIDEO_LOAD);
    }

    protected void initVoidParam() {
        // initParam的构造
        InitParam initParam = new InitParam();
        // domain 域名
        initParam.setDomain(mDomain);
        //8个数字的字符串为编号
        if (mRoomNumber.length() == 8) {
            // 点播编号 （不是点播id）
            initParam.setNumber(mRoomNumber);
        } else {
            // 设置点播id，和点播编号对应，两者至少要有一个有效才能保证成功
            String liveId = mRoomNumber;
            initParam.setLiveId(liveId);
        }
        // 站点认证帐号
        initParam.setLoginAccount(mLoginAccount);
        // 站点认证密码
        initParam.setLoginPwd(mLoginPwd);
        // 点播口令
        initParam.setVodPwd(mToken);
        // 昵称 用于统计和显示
        initParam.setNickName(mNickname);
        // 服务类型（站点类型）
        // webcast - ST_CASTLINE
        // training - ST_TRAINING
        // meeting - ST_MEETING
        initParam.setServiceType("webcast".equals(mServiceType) ? ServiceType.ST_CASTLINE : ServiceType.ST_TRAINING);
        //站点 系统设置 的 第三方集成 中直播模块 “认证“  启用时请确保”第三方K值“（你们的k值）的正确性 ；如果没有启用则忽略这个参数
        //initParam.setK(k);
        if (!TextUtils.isEmpty(mKey)) {
            initParam.setK(mKey);
        }
        vodSite = new VodSite(this);
        vodSite.setVodListener(this);
        vodSite.getVodObject(initParam);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESULT.SHOW_CONTROLLER:
                    showControllerView();
                    break;
                case RESULT.HIDE_CONTROLLER:
                    hideControllerView();
                    break;
                case RESULT.SHOW_VIDEO_LOAD:
                    mLoadView.setVisibility(View.VISIBLE);
                    break;
                case RESULT.HIDE_VIDEO_LOAD:
                    mLoadView.setVisibility(View.GONE);
                default:
                    break;
            }
        }
    };

    /**
     * 聊天记录 getChatHistory 响应 vodId 点播id list 聊天记录
     */
    @Override
    public void onChatHistory(String vodId, List<ChatMsg> list, int pageIndex, boolean more) {
    }

    /**
     * 问答记录 getQaHistory 响应 list 问答记录 vodId 点播id
     */
    @Override
    public void onQaHistory(String vodId, List<QAMsg> list, int pageIndex, boolean more) {
    }


    /**
     * 获取点播详情
     */
    @Override
    public void onVodDetail(VodObject vodObj) {
        if (vodObj != null) {
            vodObj.getDuration();// 时长
            vodObj.getEndTime();// 录制结束时间 始于1970的utc时间毫秒数
            vodObj.getStartTime();// 录制开始时间 始于1970的utc时间毫秒数
            vodObj.getStorage();// 大小 单位为Byte
        }
    }

    @Override
    public void onVodErr(final int errCode) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String msg = getErrMsg(errCode);
                if (!TextUtils.isEmpty(msg)) {
                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    /**
     * getVodObject的响应，vodId 接下来可以下载后播放
     */
    @Override
    public void onVodObject(String vodId) {
        mHandler.sendMessage(mHandler
                .obtainMessage(RESULT.ON_GET_VODOBJ, vodId));
        initPlayer(vodId);
    }

    /**
     * 错误码处理
     *
     * @param errCode
     *            错误码
     * @return 错误码文字表示内容
     */
    private String getErrMsg(int errCode) {
        String msg = "";
        switch (errCode) {
            case ERR_DOMAIN:
                msg = "domain 不正确";
                break;
            case ERR_TIME_OUT:
                msg = "超时";
                break;
            case ERR_SITE_UNUSED:
                msg = "站点不可用";
                break;
            case ERR_UN_NET:
                msg = "无网络请检查网络连接";
                break;
            case ERR_DATA_TIMEOUT:
                msg = "数据过期";
                break;
            case ERR_SERVICE:
                msg = "请检查填写的serviceType";
                break;
            case ERR_PARAM:
                msg = "请检查参数";
                break;
            case ERR_VOD_INTI_FAIL:
                msg = "调用getVodObject失败";
                break;
            case ERR_VOD_NUM_UNEXIST:
                msg = "点播编号不存在或点播不存在";
                break;
            case ERR_VOD_PWD_ERR:
                msg = "点播密码错误";
                break;
            case ERR_VOD_ACC_PWD_ERR:
                msg = "登录帐号或登录密码错误";
                break;
            case ERR_UNSURPORT_MOBILE:
                msg = "不支持移动设备";
                break;

            default:
                break;
        }
        return msg;
    }

    private String getTime(int time) {
        return String.format("%02d", time / 3600) + ":"
                + String.format("%02d", time % 3600 / 60) + ":"
                + String.format("%02d", time % 3600 % 60);
    }

    protected Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG.MSG_ON_INIT:
                    int max = msg.getData().getInt(DURATION);
                    mSeekBar.setMax(max);
                    max = max / 1000;
                    mTotalView.setText(getTime(max));
                    mVodPlayer.seekTo(lastPostion);
                    break;
                case MSG.MSG_ON_STOP:
                    break;
                case MSG.MSG_ON_VIDEOSIZE:
                    break;
                case MSG.MSG_ON_PAGE:
                    mVodDocFragment.showDocView();
                    break;
                case MSG.MSG_ON_PAUSE:
                    VIEDOPAUSEPALY = 1;
                    mPlayBtn.setImageResource(R.drawable.icon_play);
                    break;
                case MSG.MSG_ON_RESUME:
                    VIEDOPAUSEPALY = 0;
                    mPlayBtn.setImageResource(R.drawable.icon_pause);
                    break;
                case MSG.MSG_ON_POSITION:
                    if (isTouch) {
                        int anyPosition = (Integer) msg.obj;
                        anyPosition = anyPosition / 1000;
                        mTimeView.setText(getTime(anyPosition));
                        return;
                    }
                case MSG.MSG_ON_SEEK:
                    isTouch = false;
                    int anyPosition = (Integer) msg.obj;
                    anyPosition = anyPosition / 1000;
                    mTimeView.setText(getTime(anyPosition));
                    break;
                case MSG.MSG_ON_AUDIOLEVEL:
                    break;
                case MSG.MSG_ON_ERROR:
                    int errorCode = (Integer) msg.obj;
                    switch (errorCode) {
                        case ERR_PAUSE:
                            Toast.makeText(getApplicationContext(), "暂停失败", DURITME)
                                    .show();
                            break;
                        case ERR_PLAY:
                            Toast.makeText(getApplicationContext(), "播放失败", DURITME)
                                    .show();
                            break;
                        case ERR_RESUME:
                            Toast.makeText(getApplicationContext(), "恢复失败", DURITME)
                                    .show();
                            break;
                        case ERR_SEEK:
                            Toast.makeText(getApplicationContext(), "进度变化失败", DURITME)
                                    .show();
                            break;
                        case ERR_STOP:
                            Toast.makeText(getApplicationContext(), "停止失败", DURITME)
                                    .show();
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    @Override
    public void onInit(int result, boolean haveVideo, int duration,
                       List<DocInfo> docInfos) {
        if (lastPostion >= duration-1000) {
            lastPostion = 0;
        }
        Message message = new Message();
        message.what = MSG.MSG_ON_INIT;
        message.obj = docInfos;
        Bundle bundle = new Bundle();
        bundle.putInt(DURATION, duration);
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPlayStop() {
        isPlay = false;
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_STOP, 0));
    }

    @Override
    public void onPosition(int position) {
        mSeekBar.setProgress(position);
        lastPostion = position;
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_POSITION,
                position));
    }

    @Override
    public void onVideoSize(int position, int videoWidth, int videoHeight) {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_VIDEOSIZE, 0));
    }


    @Override
    public void onSeek(int position) {
        myHandler.sendMessage(myHandler
                .obtainMessage(MSG.MSG_ON_SEEK, position));
    }

    @Override
    public void onAudioLevel(int level) {
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_AUDIOLEVEL,
                level));
    }

    @Override
    public void onError(int errCode) {
        myHandler.sendMessage(myHandler
                .obtainMessage(MSG.MSG_ON_ERROR, errCode));
    }

    @Override
    public void onPlayPause() {
        isPlay = false;
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_PAUSE, 0));
    }

    @Override
    public void onPlayResume() {
        isPlay = true;
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_RESUME, 0));
    }

    @Override
    public void onPageSize(int position, int w, int h) {
        //文档翻页切换，开始显示
        myHandler.sendMessage(myHandler
                .obtainMessage(MSG.MSG_ON_PAGE, position));

    }

    @Override
    /**
     * type 边界类型
     * eventType  MotionEvent.ACTION_MOVE or MotionEvent.ACTION_UP
     */
    public boolean onEndHDirection(GSDocView arg0, int arg1,int eventType) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onSingleClicked(GSDocView arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onCaching(boolean isCatching) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onDoubleClicked(GSDocView gsDocView) {
        return false;
    }

    @Override
    public void onChat(List<ChatMsg> list) {
    }

    @Override
    public void onVideoStart() {
        isPlay = true;
        myHandler.sendMessage(myHandler.obtainMessage(MSG.MSG_ON_RESUME, 0));
        mHandler.sendEmptyMessage(RESULT.HIDE_VIDEO_LOAD);
        mHandler.sendEmptyMessage(RESULT.SHOW_CONTROLLER);
    }

    @Override
    public void onBackPressed() {
        getPreferences(MODE_PRIVATE).edit().putInt("lastPos", lastPostion).commit();
        release();
        super.onBackPressed();
    }

    private void stopPlay() {
        isPlay = false;
        if (mVodPlayer != null) {
            mVodPlayer.stop();
        }
    }

    private void release() {
        stopPlay();
        if (mVodPlayer != null) {
            mVodPlayer.release();
        }
    }
}