package com.gensee.player;

/**
 * Created by suju on 16/10/24.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gensee.common.ServiceType;
import com.gensee.config.ConfigApp;
import com.gensee.entity.InitParam;
import com.gensee.entity.UserInfo;
import com.gensee.fragement.ChatFragment;
import com.gensee.fragement.DocFragment;
import com.gensee.fragement.QaFragment;
import com.gensee.fragement.ViedoFragment;
import com.gensee.fragement.VoteFragment;
import com.gensee.net.AbsRtAction;
import com.gensee.taskret.OnTaskRet;
import com.gensee.utils.GenseeLog;

public class GenseeLivePlayActivity extends AppCompatActivity implements OnPlayListener {

    static final String TAG = "GenseeLivePlayActivity";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ServiceType serviceType = ServiceType.ST_TRAINING;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private RelativeLayout relTip;
    private TextView txtTip;
    private ViedoFragment mViedoFragment;
    private Player mPlayer;
    private FragmentManager mFragmentManager;

    private String mDomain;
    private String mRoomNumber;
    private String mToken;
    private String mLoginAccount;
    private String mLoginPwd;
    private String mNickname;
    private String mKey;
    private String mServiceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gensee_live_player);
        mFragmentManager = getSupportFragmentManager();
        initWidget();

        Bundle liveBundle = getIntent().getExtras();
        if (liveBundle == null || liveBundle.isEmpty()) {
            Toast.makeText(getBaseContext(), "直播课时信息不存在", Toast.LENGTH_LONG).show();
            return;
        }
        getLiveInfo(getIntent().getExtras());
    }

    protected void getLiveInfo(Bundle liveBundle) {
        mDomain = liveBundle.getString("domain");
        mRoomNumber = liveBundle.getString("roomNumber");
        mToken = liveBundle.getString("joinPwd");
        mLoginAccount = liveBundle.getString("loginAccount");
        mLoginPwd = liveBundle.getString("loginPwd");
        mToken = liveBundle.getString("joinPwd");
        mNickname = liveBundle.getString("nickName");
        mServiceType = liveBundle.getString("serviceType");
        mKey = liveBundle.getString("k");
        initInitParam();
    }

    public void initWidget() {

        mPlayer = new Player();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mPlayer);

        relTip = (RelativeLayout) findViewById(R.id.rl_tip);
        txtTip = (TextView) findViewById(R.id.tv_tip);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mTabLayout.addTab(mTabLayout.newTab().setText("文档"), true);//添加 Tab,默认选中
        mTabLayout.addTab(mTabLayout.newTab().setText("聊天"), false);//添加 Tab,默认不选中
        mTabLayout.addTab(mTabLayout.newTab().setText("问答"), false);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mSectionsPagerAdapter);

        initPlayFrameContainer();
    }

    private void initPlayFrameContainer() {
        android.support.v4.app.FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (mViedoFragment == null) {
            mViedoFragment = new ViedoFragment(mPlayer);
            ft.add(R.id.frame_container, mViedoFragment);
            ft.commit();
        } else {
            ft.show(mViedoFragment);
        }

        if (null != mViedoFragment) {
            mViedoFragment.setVideoViewVisible(true);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void initInitParam() {
        InitParam initParam = new InitParam();
        // 设置域名
        initParam.setDomain(mDomain);
        //设置编号,8位数字字符串，
        initParam.setNumber(mRoomNumber);
        // 如果只有直播间id（混合字符串）可以使用setLiveId("")代替setNumber()
        //initParam.setLiveId("a4f3c8cb2b094c369617888917bf221e");
        // 设置站点登录帐号（根据配置可选）
        initParam.setLoginAccount(mLoginAccount);
        // 设置站点登录密码（根据配置可选）
        initParam.setLoginPwd(mLoginPwd);
        // 设置显示昵称，如果设置为空，请确保
        initParam.setNickName(mNickname);
        // 设置加入口令（根据配置可选）
        initParam.setJoinPwd(mToken);
        // 设置服务类型，如果站点是webcast类型则设置为ServiceType.ST_CASTLINE，
        // training类型则设置为ServiceType.ST_TRAINING
        initParam.setServiceType("webcast".equals(mServiceType) ? ServiceType.ST_CASTLINE : ServiceType.ST_TRAINING);
        if (!TextUtils.isEmpty(mKey)) {
            //站点 系统设置 的 第三方集成 中直播模块 “认证“  启用时请确保”第三方K值“（你们的k值）的正确性 ；如果没有启用则忽略这个参数
            initParam.setK(mKey);
        }

        initPlayer(initParam);
    }

    public void initPlayer(InitParam p) {
        mPlayer.join(getApplicationContext(), p, this);
    }

    private void showTip(final boolean isShow, final String tip) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (isShow) {
                    if (relTip.getVisibility() != View.VISIBLE) {
                        relTip.setVisibility(View.VISIBLE);
                    }
                    txtTip.setText(tip);
                } else {
                    relTip.setVisibility(View.GONE);
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(Player player, int sectionNumber) {
            switch (sectionNumber) {
                case 0:
                    return new DocFragment(player);
                case 1:
                    return new ChatFragment(player);
                case 2:
                    return new QaFragment(player);
            }
            return null;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_gensee_live_player, container, false);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Player mPlayer;

        public SectionsPagerAdapter(FragmentManager fm, Player player) {
            super(fm);
            this.mPlayer = player;
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(mPlayer, position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "文档";
                case 1:
                    return "聊天";
                case 2:
                    return "问答";
            }
            return null;
        }
    }

    interface HANDlER {
        int USERINCREASE = 1;
        int USERDECREASE = 2;
        int USERUPDATE = 3;
        int SUCCESSJOIN = 4;
        int SUCCESSLEAVE = 5;
        int CACHING = 6;
        int CACHING_END = 7;
        int RECONNECTING = 8;
        int VIDEO_CLOSE = 9;
        int VIDEO_ERROR = 10;
        int VIDEO_NOSTART = 11;
    }

    private AlertDialog dialog;
    private int inviteMediaType;
    private boolean bJoinSuccess = false;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case HANDlER.SUCCESSJOIN:
                    bJoinSuccess = true;
                    if (mViedoFragment != null) {
                        mViedoFragment.onJoin(bJoinSuccess);
                    }
                    break;
                case HANDlER.SUCCESSLEAVE:
                    dialog();
                    break;
                case HANDlER.CACHING:
                    mViedoFragment.setPlayStatus(ViedoFragment.BUFFERING);
                    break;
                case HANDlER.CACHING_END:
                    mViedoFragment.setPlayStatus(ViedoFragment.LIVE);
                    break;
                case HANDlER.RECONNECTING:
                    mViedoFragment.setPlayStatus(ViedoFragment.BUFFERING);
                    break;
                case HANDlER.VIDEO_CLOSE:
                    mViedoFragment.setPlayStatus(ViedoFragment.CLOSE);
                    break;
                case HANDlER.VIDEO_ERROR:
                    mViedoFragment.setPlayStatus(ViedoFragment.ERROR);
                    break;
                case HANDlER.VIDEO_NOSTART:
                    mViedoFragment.setPlayStatus(ViedoFragment.NO_START);
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("你已经被踢出");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    private void showErrorMsg(final String sMsg) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(GenseeLivePlayActivity.this);
                builder.setTitle("提示");
                builder.setMessage(sMsg);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        finish();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });

    }

    private void toastMsg(final String msg) {
        if (msg != null) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), msg,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoFullScreen();
        } else {
            videoNormalScreen();
        }
    }

    private void videoFullScreen() {
        View containerView = findViewById(R.id.frame_container);
        containerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void videoNormalScreen() {
        View containerView = findViewById(R.id.frame_container);
        int height = getResources().getDimensionPixelSize(R.dimen.video_height);
        containerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
    }

    private void releasePlayer() {
        if (null != mPlayer && bJoinSuccess) {
            mPlayer.leave();
            mPlayer.release(this);
            bJoinSuccess = false;
        }
    }

    @Override
    public void onLeave(int reason) {
        // 当前用户退出
        // bJoinSuccess = false;
        String msg = null;
        switch (reason) {
            case LEAVE_NORMAL:
                msg = "您已经退出直播间";
                break;
            case LEAVE_KICKOUT:
                msg = "您已被踢出直播间";
                mHandler.sendEmptyMessage(HANDlER.SUCCESSLEAVE);
                break;
            case LEAVE_TIMEOUT:
                msg = "连接超时，您已经退出直播间";
                break;
            case LEAVE_CLOSE:
                msg = "直播已经停止";
                break;
            case LEAVE_UNKNOWN:
                msg = "您已退出直播间，请检查网络、直播间等状态";
                break;
            default:
                break;
        }
        if (null != msg) {
            showErrorMsg(msg);
        }
    }

    @Override
    public void onUserUpdate(UserInfo info) {
        mHandler.sendMessage(mHandler.obtainMessage(HANDlER.USERUPDATE, info));
    }

    @Override
    public void onMicNotify(int notify) {
        GenseeLog.d(TAG, "onMicNotify notify = " + notify);
    }

    @Override
    public void onVideoEnd() {
        GenseeLog.d(TAG, "onVideoEnd");
        mHandler.sendEmptyMessage(HANDlER.RECONNECTING);
    }

    @Override
    public void onLottery(int cmd, String info) {
        toastMsg("抽奖\n指令：" + (cmd == 1 ? "开始" : (cmd == 2 ? "结束" : "取消"))
                + "\n结果：" + info);
    }

    @Override
    public void onFileShare(int i, String s, String s1) {
    }

    @Override
    public void onLiveText(String language, String text) {
        toastMsg("文字直播\n语言：" + language + "\n内容：" + text);
    }

    @Override
    public void onSubject(String subject) {
        Log.d(TAG, "onSubject subject = " + subject);
    }

    @Override
    public void onJoin(int result) {
        String msg = null;
        switch (result) {
            case JOIN_OK:
                msg = "加入成功";
                mHandler.sendEmptyMessage(HANDlER.SUCCESSJOIN);
                break;
            case JOIN_CONNECTING:
                msg = "正在加入";
                break;
            case JOIN_CONNECT_FAILED:
                msg = "连接失败";
                mHandler.sendEmptyMessage(HANDlER.VIDEO_ERROR);
                break;
            case JOIN_RTMP_FAILED:
                msg = "连接服务器失败";
                mHandler.sendEmptyMessage(HANDlER.VIDEO_ERROR);
                break;
            case JOIN_TOO_EARLY:
                msg = "直播还未开始";
                mHandler.sendEmptyMessage(HANDlER.VIDEO_NOSTART);
                break;
            case JOIN_LICENSE:
                msg = "人数已满";
                break;
            default:
                msg = "加入返回错误" + result;
                break;
        }
        toastMsg(msg);
    }

    @Override
    public void onDocSwitch(int i, String s) {
    }

    @Override
    public void onVideoBegin() {
        GenseeLog.d(TAG, "onVideoBegin");
        mHandler.sendEmptyMessage(HANDlER.CACHING_END);
    }

    @Override
    public void onRollcall(final int timeOut) {
        mHandler.post(new Runnable() {
            private AlertDialog dialog = null;
            private int itimeOut;

            private void rollcallAck(final boolean isAccept) {
                mHandler.removeCallbacks(this);
                mPlayer.rollCallAck(isAccept, new OnTaskRet() {

                    @Override
                    public void onTaskRet(boolean arg0, int arg1, String arg2) {
                        toastMsg(arg0 ? (isAccept ? "本次签到成功" : "您本次未签到") : "操作失败");
                    }
                });
            }

            @Override
            public void run() {
                if (dialog == null) {
                    this.itimeOut = timeOut;
                    dialog = new AlertDialog.Builder(GenseeLivePlayActivity.this)
                            .setMessage("")
                            .setPositiveButton("签到", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            rollcallAck(true);
                        }
                    }).setCancelable(false).create();
                    dialog.show();
                }
                dialog.setMessage("点名倒计时剩余秒数：" + itimeOut);
                itimeOut--;
                if (itimeOut < 0) {
                    dialog.dismiss();
                    rollcallAck(false);
                } else {
                    mHandler.postDelayed(this, 1000);
                }
            }
        });
    }

    @Override
    public void onInvite(final int type, final boolean isOpen) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                postInvite(type, isOpen);
            }
        });
    }

    private void postInvite(int type, boolean isOpen) {
        if (isOpen) {
            inviteMediaType = type;
            String media = null;
            if (type == INVITE_AUIDO) {
                media = "音频";
            } else if (type == INVITE_VIDEO) {
                media = "视频";
            } else {
                media = "音视频";
            }
            if (dialog == null) {
                dialog = new AlertDialog.Builder(this)
                        .setPositiveButton("接受",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        accept(true);
                                    }
                                })
                        .setNegativeButton("拒绝",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        accept(false);
                                    }
                                }).create();
            }
            dialog.setMessage("老师邀请你打开" + media);
            dialog.show();
        } else {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            accept(false);
        }
    }

    private void accept(boolean isAccept) {
        mPlayer.openMic(this, isAccept, null);
    }

    @Override
    public void onPageSize(int pos, int w, int h) {
        Log.d(TAG, "文档分辨率 w = " + w + " h = " + h);
    }

    @Override
    public void onReconnecting() {
        GenseeLog.d(TAG, "onReconnecting");
        mHandler.sendEmptyMessage(HANDlER.RECONNECTING);
    }

    @Override
    public void onAudioLevel(int i) {
    }

    @Override
    public void onUserJoin(UserInfo info) {
        mHandler.sendMessage(mHandler.obtainMessage(HANDlER.USERINCREASE, info));
    }

    @Override
    public void onCaching(boolean isCaching) {
        GenseeLog.d(TAG, "onCaching isCaching = " + isCaching);
        mHandler.sendEmptyMessage(isCaching ? HANDlER.CACHING
                : HANDlER.CACHING_END);
    }

    @Override
    public void onErr(int errCode) {
        String msg = null;
        switch (errCode) {
            case AbsRtAction.ErrCode.ERR_DOMAIN:
                msg = "域名domain不正确";
                break;
            case AbsRtAction.ErrCode.ERR_TIME_OUT:
                msg = "请求超时，稍后重试";
                break;
            case AbsRtAction.ErrCode.ERR_SITE_UNUSED:
                msg = "站点不可用，请联系客服或相关人员";
                break;
            case AbsRtAction.ErrCode.ERR_UN_NET:
                msg = "网络不可用，请检查网络连接正常后再试";
                mHandler.sendEmptyMessage(HANDlER.VIDEO_ERROR);
                break;
            case AbsRtAction.ErrCode.ERR_SERVICE:
                msg = "service  错误，请确认是webcast还是training";
                mHandler.sendEmptyMessage(HANDlER.VIDEO_ERROR);
                break;
            case AbsRtAction.ErrCode.ERR_PARAM:
                msg = "initparam参数不全";
                break;
            case AbsRtAction.ErrCode.ERR_THIRD_CERTIFICATION_AUTHORITY:
                msg = "第三方认证失败";
                break;
            case AbsRtAction.ErrCode.ERR_NUMBER_UNEXIST:
                msg = "编号不存在";
                break;
            case AbsRtAction.ErrCode.ERR_TOKEN:
                msg = "口令错误";
                break;
            case AbsRtAction.ErrCode.ERR_LOGIN:
                msg = "站点登录帐号或登录密码错误";
                break;
            default:
                msg = "错误：errCode = " + errCode;
                break;
        }
        if (msg != null) {
            toastMsg(msg);
        }
    }

    @Override
    public void onFileShareDl(int i, String s, String s1) {
    }

    @Override
    public void onPublish(boolean isPlaying) {
        mViedoFragment.setPlayStatus(isPlaying ? ViedoFragment.LIVE : ViedoFragment.PAUSE);
        toastMsg(isPlaying ? "直播（上课）中" : "直播暂停（下课）");
    }

    @Override
    public void onRosterTotal(int total) {
        Log.d(TAG, "onRosterTotal total = " + total);
    }

    @Override
    public void onPublicMsg(long userId, String msg) {
        Log.d(TAG, "onPublicMsg userId = " + userId + " msg = " + msg);
    }

    @Override
    public void onUserLeave(UserInfo info) {
        mHandler.sendMessage(mHandler.obtainMessage(HANDlER.USERDECREASE, info));
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(ConfigApp.PARAMS_JOINSUCCESS, bJoinSuccess);
        outState.putBoolean(
                ConfigApp.PARAMS_VIDEO_FULLSCREEN,
                getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        if (bJoinSuccess) {
            outState.putString(ConfigApp.PARAMS_DOMAIN, mDomain);
            outState.putString(ConfigApp.PARAMS_NUMBER, mRoomNumber);
            outState.putString(ConfigApp.PARAMS_NICKNAME, mNickname);
            outState.putString(ConfigApp.PARAMS_JOINPWD, mToken);

            if (serviceType == ServiceType.ST_CASTLINE) {
                outState.putString(ConfigApp.PARAMS_TYPE, ConfigApp.WEBCAST);
            } else if (serviceType == ServiceType.ST_TRAINING) {
                outState.putString(ConfigApp.PARAMS_TYPE, ConfigApp.TRAINING);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean bJoinSuccess = savedInstanceState
                .getBoolean(ConfigApp.PARAMS_JOINSUCCESS);
        boolean bVideoFullScreen = savedInstanceState
                .getBoolean(ConfigApp.PARAMS_VIDEO_FULLSCREEN);
        if (bVideoFullScreen) {
            videoFullScreen();
        }
        if (bJoinSuccess) {
            mDomain = savedInstanceState.getString(ConfigApp.PARAMS_DOMAIN);
            mRoomNumber = savedInstanceState.getString(ConfigApp.PARAMS_NUMBER);
            mNickname = savedInstanceState.getString(ConfigApp.PARAMS_NICKNAME);
            mToken = savedInstanceState.getString(ConfigApp.PARAMS_JOINPWD);

            String type = (String) savedInstanceState
                    .get(ConfigApp.PARAMS_TYPE);
            if (type.equals(ConfigApp.WEBCAST)) {
                serviceType = ServiceType.ST_CASTLINE;
            } else if (type.equals(ConfigApp.TRAINING)) {
                serviceType = ServiceType.ST_TRAINING;
            }
            initInitParam();
        }
    }
}
