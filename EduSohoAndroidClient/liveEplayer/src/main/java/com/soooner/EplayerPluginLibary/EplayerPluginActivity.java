package com.soooner.EplayerPluginLibary;

import android.app.AlertDialog;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.soooner.EplayerPluginLibary.adapter.SpeakAdapter;
import com.soooner.EplayerPluginLibary.util.*;
import com.soooner.EplayerPluginLibary.util.LogUtil;
import com.soooner.EplayerPluginLibary.util.StringUtils;
import com.soooner.EplayerPluginLibary.util.ToastUtil;
import com.soooner.EplayerPluginLibary.widget.*;
import com.soooner.EplayerSetting;
import com.soooner.playback.PlaybackEngin;
import com.soooner.playback.PlaybackLoading;
import com.soooner.playback.entity.EPlaybackSessionInfo;
import com.soooner.source.common.net.Protocol;
import com.soooner.source.common.util.*;
import com.soooner.source.entity.Prainse;
import com.soooner.source.entity.SessionData.*;
import com.soooner.source.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.soooner.source.entity.SessionEmun.DrawPadColorType;
import com.soooner.source.entity.SessionEmun.LiveRoomLiveStatus;
import com.soooner.source.entity.SessionEmun.LiveRoomStreamType;
import com.soooner.source.protocol.GetMusicInfoProtocol;
import com.soooner.source.protocol.GetWayProtocol;
import com.soooner.source.protocol.UserLoginProtocol;
import com.soooner.widget.DrawPadView;
import com.soooner.ws.event.LiveRoomEvent.*;
import com.soooner.ws.net.EplayerSocket;
import com.soooner.ws.net.Sender;
import de.greenrobot.event.EventBus;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import java.util.*;

/**
 * Created by junhai on 14-8-14.
 */
//TODO
//TODO 此 activity 代码和算法极其不优雅，需要重构
//TODO 需要将UI结构化，算法需要抽象化，目前的代码结构过于混乱，逻辑不够清晰
//TODO 重构时需要考虑 EplayerPluginPadActivity 中相同的部分
//TODO
public class EplayerPluginActivity extends EplayerPluginBaseActivity implements PlaybackEngin.OnEnginListener {


    public static String EPLAY_LIVECLASSROOMID = "eplay_liveClassroomId";
    public static String EPLAY_USERNAME = "eplay_username";
    public static String EPLAY_USERPWD = "eplay_userpwd";
    public static String EPLAY_CUSTOMER= "eplay_customer";
    public static String EPLAY_PID= "eplay_pid";

    public static String EPLAY_EXSTR = "eplay_exStr";

   // public boolean chatForbid=false;//禁言标识位
    MyChatView chatView;
    int video_margin_left_right;

    public String key_liveClassroomId, key_username, key_userpwd, key_exstr,key_customer,key_pid;

    private static final String TAG = EplayerPluginActivity.class.getSimpleName();
    Context context;
    private DrawPadView drawPadView;
    MyVideoView fl_myvideoview;
    RelativeLayout fl_all;
    LinearLayout li_drawpaddview;
   View view_title;
    PlaybackEngin playbackEngin;
    PlaybackLoading loading;

    PlayerControllerView playerControllerView;
    VoteControllerView voteControllerView;

    private boolean living;

    TextView  top_title_title,top_title_right,top_title_left;
    LinearLayout li_top_title_left;
    ListView  listview;
    SpeakAdapter adapter;
    ScrollView scrollview;
    TextView tv_middle_state,tv_online_num;

     public boolean loadingTimeoutShow;
    InputMethodManager inputMethodManager;


    int BASE_WIDTH =0;//视频图基础宽度
    private long exitTime;//保存上一次的退出时间 ；单位 毫秒
    private boolean   isPlayDone;

    boolean playbackLoadingFlag =false;//todo 回看时，当正在缓冲时按两次返回键会因为播放器出现崩溃的问题，所有专门为这种情况设置了一个变量

    @Override
    public boolean playDoneIfHasMedia() {
        return fl_myvideoview.playDoneIfHasMedia();
    }

    @Override
    public long playTimeIfHasMedia() {
        return fl_myvideoview.playTimeIfHasMedia();
    }

    @Override
    public void stopPlaybackEngin() {

    }

    @Override
    public void currentAbsTime(long time) {
////        currentPlaybackTime  =  time;
    }

    @Override
    public void showProgressTime(long progress, long totalTime, long playbackTime, long playbackBeginTime, long playbackEndTime) {
        com.soooner.source.common.util.LogUtil.d("------------" + progress + "-------------");
        currentPlaybackTime  =  progress;

        playerControllerView.showProgressTime(progress,totalTime,playbackTime,playbackBeginTime,playbackEndTime);

    }



    LinearLayout fl_chat,fl_status,scrollview_content,all_activity_layout;

    LinearLayout li_progressbar_bg,li_load;
    MyProgressBar myprogressbar,li_load_myprogressbar;



    long currentPlaybackTime;




    List<SocketMessage> bufferList=new ArrayList<SocketMessage>();
    HashSet<String> msgKeys = new HashSet<String>();

    Timer loginTimer;
    public long  currentStaticLoginTimeMillis;

    Timer loadingTimer;
    public long  currentStaticTimeMillis;

    Timer errorTimer;
    public long  currentStaticErrorTimeMillis;

    public int streamErrorTimes;

    Timer refreshTimer=new Timer();
    boolean enable_refresh_listview =true;

    boolean progressbar_show = true;

    boolean playerStartPlay = false;


    public final  int   RECONNECTION_MAX_NUM=3;
    public final  int   RECONNECTION_DEFAULT_NUM=0;
    int curent_reconnection=RECONNECTION_DEFAULT_NUM;




    MyVideoView.VideoViewListener videoViewListener=new MyVideoView.VideoViewListener(){

        @Override
        public void onVideoviewClick() {
                LogUtil.d(TAG,"my_videoview onClick MotionEvent.ACTION_DOWN");
                if (screenstate == MyVideoView.SCREENSTATE.NORMAL || screenstate == MyVideoView.SCREENSTATE.FULLSCREEN_VIDEOVIEW) {
                    if (screenstate == MyVideoView.SCREENSTATE.FULLSCREEN_VIDEOVIEW) {
                        if (playerControllerView != null && playerControllerView.getVisibility() == View.VISIBLE) {
                            playerControllerView.setVisibility(View.INVISIBLE);
                            return;
                        }
                    }
                    changedVideoViewScreen();

                }
        }

        @Override
        public void onAudioViewClick() {
            if ( EplayerSetting.isPlayback) {
                if (screenstate == MyVideoView.SCREENSTATE.FULLSCREEN_VIDEOVIEW) {
                    if (playerControllerView != null && playerControllerView.getVisibility() == View.VISIBLE) {
                        playerControllerView.setVisibility(View.INVISIBLE);
                        return;
                    }
                }
                if (screenstate == MyVideoView.SCREENSTATE.FULLSCREEN_VIDEOVIEW) {
                    changedVideoViewScreen();
                }
            }
        }

        @Override
        public void onChangedVideoViewScreen() {
            changedVideoViewScreen();
        }

        @Override
        public void onCompletion(IMediaPlayer mp) {
            if(EplayerSessionInfo.sharedSessionInfo().infoData.playMusic){
                new GetMusicInfoThread(EplayerSessionInfo.sharedSessionInfo().userInfo.liveClassroomId,EplayerSessionInfo.sharedSessionInfo().infoData.musicType+"").start();
            }
        }

        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            LogUtil.d(TAG,"OnErrorListener.MEDIA_MISS_VIDEO");

            if(EplayerPluginActivity.this.playbackEngin!=null) {
                EplayerPluginActivity.this.playbackEngin.pausePlayback();
            }

            EplayerPluginActivity.this.currentStaticErrorTimeMillis = System.currentTimeMillis();
            if(errorTimer!=null) {
                errorTimer.cancel();
                errorTimer = null;
            }
            errorTimer = new Timer();
            errorTimer.schedule(new ErrorTimerTask(EplayerPluginActivity.this.currentStaticErrorTimeMillis),5000);

            return true;
        }

        @Override
        public void onStateChanged(IMediaPlayer mp, boolean pause) {
            progressbar_show =pause;

            if(!EplayerSessionInfo.sharedSessionInfo().infoData.playMusic) {
                if (pause) {

                    if(EplayerPluginActivity.this.playerStartPlay) {

                        EplayerPluginActivity.this.currentStaticTimeMillis = System.currentTimeMillis();
                        if (loadingTimer != null) {
                            loadingTimer.cancel();
                            loadingTimer = null;
                        }
                        loadingTimer = new Timer();
                        loadingTimer.schedule(new LoadingTimerTask(EplayerPluginActivity.this.currentStaticTimeMillis), 15000);
                    }

                } else {
                    EplayerPluginActivity.this.playerStartPlay = true;

                    EplayerPluginActivity.this.currentStaticTimeMillis = System.currentTimeMillis();
                    if(loadingTimer!=null) {
                        loadingTimer.cancel();
                        loadingTimer = null;
                    }

                }
                if(null!=playerControllerView&&playerControllerView.isManualPausePlayer())
                    return;

                EplayerPluginActivity.this.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        if(progressbar_show){
                            fl_myvideoview.showVideoviewProgressbar();
                        }else{
                            fl_myvideoview.hideVideoviewProgressbar();
                        }
                    }
                });
            }
        }



        @Override
        public void onLoadingStart(IMediaPlayer mp) {
            LogUtil.d("onVideoSizeChanged  onLoadingStart");
            playbackLoadingFlag =true;
            if(!EplayerSessionInfo.sharedSessionInfo().infoData.playMusic) {

                EplayerPluginActivity.this.currentStaticTimeMillis = System.currentTimeMillis();
                if(loadingTimer!=null) {
                    loadingTimer.cancel();
                    loadingTimer = null;
                }
                loadingTimer = new Timer();
                loadingTimer.schedule(new LoadingTimerTask(EplayerPluginActivity.this.currentStaticTimeMillis),30000);

            }
        }

        @Override
        public void onLoadingEnd(IMediaPlayer mp) {
            LogUtil.d("onVideoSizeChanged  onLoadingEnd");
            playbackLoadingFlag =false;
            if(!EplayerSessionInfo.sharedSessionInfo().infoData.playMusic) {
                curent_reconnection=RECONNECTION_DEFAULT_NUM;

                handler.sendEmptyMessage(TaskType.MESSAGE_ENABLE_CONTROL);


                EplayerPluginActivity.this.currentStaticTimeMillis = System.currentTimeMillis();
                if(loadingTimer!=null) {
                    loadingTimer.cancel();
                    loadingTimer = null;
                }

            }
        }
    };





    public class LoginTimerTask extends TimerTask{

        private long  currentTimeMillis;

        public LoginTimerTask(long  currentTimeMillis){
            this.currentTimeMillis = currentTimeMillis;
        }

        @Override
        public void run() {
            if(EplayerPluginActivity.this.loadingTimeoutShow)
                return;


            long currentStaticTimeMillis = EplayerPluginActivity.this.currentStaticLoginTimeMillis;
            if(currentTimeMillis==currentStaticTimeMillis) {


                    EplayerPluginActivity.this.loadingTimeoutShow = true;
                    EplayerPluginActivity.this.requestStop();

                    EplayerPluginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(EplayerPluginActivity.this)
                                    .setTitle("提示")
                                    .setMessage("您的网络太差啦，无法登录房间，请重试或者切换到稳定网络！")
                                    .setPositiveButton(
                                            "确定",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int whichButton) {
                                                    finish();
                                                }
                                            }).setCancelable(false).create().show();

                        }
                    });
                LogUtil.d(TAG,"ErrorTimerTask excute");
            }else{

                LogUtil.d(TAG,"ErrorTimerTask igron");
            }
        }
    }

    public class ErrorTimerTask extends TimerTask{

        private long  currentTimeMillis;

        public ErrorTimerTask(long  currentTimeMillis){
            this.currentTimeMillis = currentTimeMillis;
        }

        @Override
        public void run() {
            if(EplayerPluginActivity.this.loadingTimeoutShow)
                return;


            long currentStaticTimeMillis = EplayerPluginActivity.this.currentStaticErrorTimeMillis;

            if (curent_reconnection < RECONNECTION_MAX_NUM) {
                LogUtil.d(TAG,"ErrorTimerTask connect age,curent_reconnection:" + curent_reconnection);
                curent_reconnection++;
                if(EplayerSetting.isPlayback){

                    LogUtil.d(TAG, "ErrorTimerTask currentPlaybackTime:" + EplayerPluginActivity.this.currentPlaybackTime);
                    EplayerPluginActivity.this.playerStartPlay= false;

                    fl_myvideoview.stopPlayback();


                    if( EplayerPluginActivity.this.playbackEngin!=null)
                    EplayerPluginActivity.this.playbackEngin.resumePlayback(EplayerPluginActivity.this.currentPlaybackTime);


                }else {


                    //TODO: MESSAGE_CHANGE_LIVE_STATUS
                    LiveRoomInfoData infoData = EplayerSessionInfo.sharedSessionInfo().infoData;

                    if (infoData!=null&&!infoData.playMusic) {
                        handler.sendEmptyMessage(TaskType.MESSAGE_CHANGE_LIVE_STATUS);
                    }
                }
            } else {


            if(currentTimeMillis==currentStaticTimeMillis) {

                if (EplayerSessionInfo.sharedSessionInfo()!=null&&EplayerSessionInfo.sharedSessionInfo().infoData!=null&&EplayerSessionInfo.sharedSessionInfo().infoData.liveStatus == LiveRoomLiveStatus.LiveRoomLiveStatusPlay
                        && EplayerSessionInfo.sharedSessionInfo().infoData.isStreamPush)  {

                    EplayerPluginActivity.this.loadingTimeoutShow = true;
                    EplayerPluginActivity.this.requestStop();

                    EplayerPluginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(EplayerPluginActivity.this)
                                    .setTitle("提示")
                                    .setMessage("您的网络太差啦，无法稳定的播放音视频，请切换到稳定网络观看")
                                    .setPositiveButton(
                                            "确定",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int whichButton) {
                                                    finish();
                                                }
                                            }).setCancelable(false).create().show();

                        }
                    });
                }
                LogUtil.d(TAG,"ErrorTimerTask excute");
            }else{

                LogUtil.d(TAG,"ErrorTimerTask igron");
            }
        }
    }
    }



    boolean isValidChangeScreen = true;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TaskType.MESSAGE_VOTE_STATISTIC:{
                    VoteStatisticMsgInfo voteStatisticMsgInfo= (VoteStatisticMsgInfo) msg.obj;
                    voteControllerView.voteStatistic(voteStatisticMsgInfo);
                    break;
                }
                case TaskType.MESSAGE_VOTE_STUDENT_RES:{
                    VoteMsgInfoResEvent event= (VoteMsgInfoResEvent) msg.obj;
                    voteControllerView.sendMessageScuess(event.isSuccess());
                    break;
                }
                case TaskType.MESSAGE_VOTE_REQ:{

                    VoteMsgInfo msgInfo= (VoteMsgInfo) msg.obj;
                    if(msgInfo.action){
                        chatView.outFocuse();
                    }
                    voteControllerView.voteReq(msgInfo);
                    break;
                }
                case TaskType.MESSAGE_PRAISE_RES:{
                    Prainse prainse= (Prainse) msg.obj;
                    chatView.prainseRes(prainse);
                    break;
                }

                case TaskType.MESSAGE_CHANGESCREEN_SCUESS:{
                    isValidChangeScreen=true;

                    if(!EplayerSetting.isPlayback) {
                        fl_myvideoview.setChangeingSize(false);

                    }
                    break;
                }

                case TaskType.MESSAGE_HIDELOADING:{
                    scrollview.setVisibility(View.VISIBLE);
                    if(!EplayerSetting.isPlayback) {
                        chatView.setVisibility(View.VISIBLE);
                        fl_chat.setVisibility(View.VISIBLE);
                        fl_status.setVisibility(View.VISIBLE);
                        top_title_right.setVisibility(View.VISIBLE);
                        all_activity_layout.removeView(playerControllerView);
                    }else{

                        playerControllerView.setVisibility(View.VISIBLE);

                        LiveRoomInfoData infoData= EplayerSessionInfo.sharedSessionInfo().infoData;
                        if(null!=infoData){
                            top_title_title.setText(infoData.subject);

                            if(!infoData.playbackPrepare){
                                ToastUtil.showStringToast(context,"回看正在准备中");
                                finish();
                                return;
                            }
                        }


                        fl_chat.setVisibility(View.VISIBLE);
                        fl_status.setVisibility(View.VISIBLE);
                        scrollview_content.removeView(fl_chat);
                        scrollview_content.removeView(fl_status);
                        all_activity_layout.removeView(chatView);

                        {
                            long totalPlaybackTime =  EPlaybackSessionInfo.sharedSessionInfo().totalPlaybackTime;
                            long playbackBeginTime =  EPlaybackSessionInfo.sharedSessionInfo().playbackBeginTime;
                            long playbackEndTime =  EPlaybackSessionInfo.sharedSessionInfo().playbackEndTime;
                            playerControllerView.showProgressTime(0,totalPlaybackTime,playbackBeginTime,playbackBeginTime,playbackEndTime);
                        }

                        if(playbackEngin!=null)
                            playbackEngin.startPlayback();

                    }

                    hideLoading();
                    break;
                }

                case TaskType.MESSAGE_INIT_PADVIEW:{
                    if(drawPadView!=null)
                        drawPadView.startEventListener();
                    break;
                }
                case TaskType.MESSAGE_ENABLE_CONTROL:{
                    if(playerControllerView!=null){
                        playerControllerView.setEnabled(true);
                    }
                    break;
                }

                case TaskType.MESSAGE_FOURCELOGOUT:{
                    showAlertDialog();
                    break;
                }

                case TaskType.MESSAGE_CHANGE_LIVE_STATUS:{
                    LogUtil.d(TAG,"TaskType.MESSAGE_CHANGE_LIVE_STATUS");
                    LiveRoomInfoData data=EplayerSessionInfo.sharedSessionInfo().infoData;
                    if(null==data){
                        return;
                    }

                    initPlayState(data.liveStatus);

                    if(data.isStreamPush&&data.liveStatus==LiveRoomLiveStatus.LiveRoomLiveStatusPlay){
                        if(data.getPlayUrl()!=null)
                            handler.sendEmptyMessage(TaskType.MESSAGE_PLAY_STREAMPUSH);
                    }else{
                       if(data.playMusic){
                           return;
                       }

                        if (fl_myvideoview.isPlayState()) {
                            if (fl_myvideoview.isPlaying()||fl_myvideoview.isPaused()) {
                                EplayerPluginActivity.this.currentStaticTimeMillis = System.currentTimeMillis();
                                if(loadingTimer!=null) {
                                    loadingTimer.cancel();
                                    loadingTimer = null;
                                }
                                EplayerPluginActivity.this.playerStartPlay= false;
                                fl_myvideoview.stopPlayback();

                            }
                            fl_myvideoview.showViewBystate(MyVideoView.PalyTypeState.STATE_NONE);
                        }
                    }

                    break;
                }
                case TaskType.MESSAGE_PLAY_STREAMPUSH:{
                    LogUtil.d(TAG,"TaskType.MESSAGE_PLAY_STREAMPUSH");
                    LiveRoomInfoData data=EplayerSessionInfo.sharedSessionInfo().infoData;
                    boolean isChange=false;
                    if(data.streamType== LiveRoomStreamType.LiveRoomStreamTypeAudio){
                        fl_myvideoview.notIgronVideo(false);
                        isChange= fl_myvideoview.showViewBystate(MyVideoView.PalyTypeState.STATE_PLAY_AUDIO);

                    }else{
                        fl_myvideoview.notIgronVideo(true);
                        isChange=  fl_myvideoview.showViewBystate(MyVideoView.PalyTypeState.STATE_PLAY_VOIDE);
                    }

//                    if (isChange) {
                        if (fl_myvideoview.isPlaying()) {
                            EplayerPluginActivity.this.currentStaticTimeMillis = System.currentTimeMillis();
                            if(loadingTimer!=null) {
                                loadingTimer.cancel();
                                loadingTimer = null;
                            }
                            EplayerPluginActivity.this.playerStartPlay= false;
                            fl_myvideoview.stopPlayback();
                        }
                        LogUtil.d(TAG,"data.getPlayUrl():"+data.getPlayUrl());
                        fl_myvideoview.setVideoURI(data);
                        if(playerControllerView!=null){
                            playerControllerView.setEnabled(false);
                        }

                   //     fl_myvideoview.showVideoviewProgressbar();
                        resetVideoSize();
                        fl_myvideoview.start();
//                    }
                    break;
                }

                case TaskType.MESSAGE_STOP_MUSIC_ERROR:{
                    if (fl_myvideoview.isPlayMuiceState()) {
                        EplayerPluginActivity.this.currentStaticTimeMillis = System.currentTimeMillis();
                        if(loadingTimer!=null) {
                            loadingTimer.cancel();
                            loadingTimer = null;
                        }
                        fl_myvideoview.stopPlayback();
                        fl_myvideoview.stopRotateChanpian();
                    }
                    break;
                }
                case TaskType.MESSAGE_PLAY_MUSIC_ERROR:{
                    ToastUtil.showStringToast(context,"播放音乐失败");
                    break;
                }
                case TaskType.MESSAGE_PLAY_MUSIC:{
                    LogUtil.d(TAG,"TaskType.MESSAGE_PLAY_MUSIC");
                    if(fl_myvideoview.isPlaying()){
                        EplayerPluginActivity.this.currentStaticTimeMillis = System.currentTimeMillis();
                        if(loadingTimer!=null) {
                            loadingTimer.cancel();
                            loadingTimer = null;
                        }
                        fl_myvideoview.stopPlayback();
                    }
                    fl_myvideoview.startRotateChanpian();
                    fl_myvideoview.notIgronVideo(false);
                    fl_myvideoview.setMusicURI((String) msg.obj);
                    if(playerControllerView!=null){
                        playerControllerView.setEnabled(false);
                    }

                  //  fl_myvideoview.showVideoviewProgressbar();
                    resetVideoSize();
                    fl_myvideoview.start();
                    break;
                }
                case TaskType.MESSAGE_INITBLACKLIST:{
                    adapter.updateBlackList(EplayerSessionInfo.sharedSessionInfo().infoData.blackList, true);
                    break;
                }
                case TaskType.MESSAGE_CHATCONTROLRES:{
                    LiveRoomInfoData infoData= (LiveRoomInfoData) msg.obj;
                    adapter.updateBlackList(infoData.canChat);
                    chatView.initChatForbid(false);
                    break;
                }
                case TaskType.MESSAGE_FORBIDCHATRES:{
                    ForbidMessage forbidMessage= (ForbidMessage) msg.obj;
                    if(forbidMessage.userKey.equals(DeviceUtil.getUDID()) ){

                        personChatForbid=forbidMessage.chatForbid;
                        chatView.initChatForbid(false);


                    }
                        List<String> li = new ArrayList<String>();
                        li.add(forbidMessage.userKey);
                        adapter.updateBlackList(li, forbidMessage.chatForbid);


                    break;
                }
                case TaskType.MESSAGE_REFRESH_ONLINE_NUM:{
                    Integer count= (Integer) msg.obj;
                    tv_online_num.setText("在线人数："+count);
                    break;
                }
                case TaskType.MESSAGE_REFRESH_LISTVIEW:{
                    LogUtil.d(TAG,"MESSAGE_REFRESH_LISTVIEW");
                    if(!enable_refresh_listview){
                        break;
                    }
                     List<SocketMessage> list=new ArrayList<SocketMessage>();
                      synchronized (bufferList){
                          if(null!=bufferList&&bufferList.size()>0){
                              list.addAll(bufferList);
                              bufferList.clear();
                          }
                      }
                     if(list.size()>0){
                      adapter.addSpeakList(list);
                      adapter.notifyDataSetChanged();
                      listview.setSelection(0);
                     }

                    break;
                }

                case TaskType.MESSAGE_LOGIN_SUCESS: {
                    Log.d(TAG, "登陆成功");



                    if(EplayerSetting.isPlayback){
                        new PlaybackLoadingThread(EplayerPluginActivity.this.key_liveClassroomId,EplayerPluginActivity.this.key_pid).start();
                    }else {

                        if(EplayerPluginActivity.this.living) {
                            EplayerSocket.init();
                            initLiveRoomInfo();
                        }
                    }
                    break;
                }
                case TaskType.MESSAGE_PLAYBACK_LOGIN_SUCESS: {
                    Log.d(TAG, "获取回看数据完成");
                    adapter.updateBlackList(EplayerSessionInfo.sharedSessionInfo().infoData.blackList, true);
                    initLiveRoomInfo();
                    break;
                }


                case TaskType.MESSAGE_LOGIN_ERROR: {
                    hideLoading();
                    int errorCode = msg.arg1;
                    LogUtil.d(TAG,"errorCode:"+errorCode);
                    if(errorCode==1){
                        ToastUtil.showStringToast(context,"直播信息获取失败");
                    }else if(errorCode==2){
                        ToastUtil.showStringToast(context,"直播信息无效");
                    }else if(errorCode==3){
                        ToastUtil.showStringToast(context,"用户校验失败");
                    }else if(errorCode==4){
                        ToastUtil.showStringToast(context,"此账号未购买此课程");
                    }else if(errorCode==5){
                        ToastUtil.showStringToast(context,"账号密码错误");
                    }else if(errorCode== Protocol.DEAFULTE_CODE){
                        ToastUtil.showStringToast(context,"请检查您的网络");
                    }else{
                        ToastUtil.showStringToast(context,"进入房间失败");
                    }
                    finish();
                    break;
                }
                case TaskType.MESSAGE_JOIN_ROOM_FINISHED: {
                    Log.d(TAG, "加入房间完成");

                    break;
                }


            }
            super.handleMessage(msg);    //To change body of overridden methods use File | Settings | File Templates.
        }
    };

    public void finish(){
        EplayerPluginActivity.this.requestStop();
        super.finish();
    }

   public void initLiveRoomInfo(){

       LiveRoomInfoData data=EplayerSessionInfo.sharedSessionInfo().infoData;
       if(null!=data){
           top_title_title.setText(data.subject);
           initPlayState(data.liveStatus);
       }
   }



    public void changedVideoViewScreen(){
        if(!isValidChangeScreen){
            return;
        }else{
            isValidChangeScreen=false;
            fl_myvideoview.setChangeingSize(true);
        }

        LogUtil.d("---test1","changedVideoViewScreen is start");
        int requestedOrientation = EplayerPluginActivity.this.getRequestedOrientation();
        switch (requestedOrientation) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT: {
                scrollview.scrollTo(0, 0);


                if(playerControllerView!=null) {
                    all_activity_layout.removeView(playerControllerView);
                    fl_all.addView(playerControllerView);
                    playerControllerView.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) playerControllerView.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                }

                if (! EplayerSetting.isPlayback) {
                    view_title.setVisibility(View.GONE);
                    chatView.hideChat();
                }else{
                    view_title.setVisibility(View.GONE);
                }


                fl_myvideoview.resetSize(SCREEN_HEIGHT,SCREEN_WIDTH,0);

                //设置全屏
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                screenstate=MyVideoView.SCREENSTATE.FULLSCREEN_VIDEOVIEW;
                break;
            }
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE: {
                //取消全屏
                WindowManager.LayoutParams attrs = getWindow().getAttributes();
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().setAttributes(attrs);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                if(playerControllerView!=null) {
                    fl_all.removeView(playerControllerView);
                    playerControllerView.setVisibility(View.VISIBLE);
                    all_activity_layout.addView(playerControllerView);
                }
                if (! EplayerSetting.isPlayback) {
                    view_title.setVisibility(View.VISIBLE);
                    chatView.setVisibility(View.VISIBLE);
                }else{
                    view_title.setVisibility(View.VISIBLE);
                }

                fl_myvideoview.resetSize(BASE_WIDTH,(int)(BASE_WIDTH*CHANGPIAN_SCALE),video_margin_left_right);

                screenstate=MyVideoView.SCREENSTATE.NORMAL;
                break;

            }
        }

        LogUtil.d("---test1","changedVideoViewScreen is end");
    }

    public void changedDrawPadViewScreen() {
        if(!isValidChangeScreen){
            return;
        }else{
            isValidChangeScreen=false;
        }
        LogUtil.d("---test1","changedDrawPadViewScreen is start");
        int requestedOrientation = EplayerPluginActivity.this.getRequestedOrientation();
        int currentWidth=0, currentHeight=0;
        switch (requestedOrientation) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT: {
                if(null!=chatView){
                    chatView.hideChat();
                }
                //设置全屏
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                li_drawpaddview.removeView(drawPadView);
                fl_all.addView(drawPadView);
                RelativeLayout.LayoutParams lp4= (RelativeLayout.LayoutParams) drawPadView.getLayoutParams();
                currentWidth=SCREEN_HEIGHT;
                currentHeight=SCREEN_WIDTH;
                lp4.width= currentWidth;
                lp4.height=currentHeight;


                if(playerControllerView!=null) {
                    all_activity_layout.removeView(playerControllerView);
                    fl_all.addView(playerControllerView);
                    playerControllerView.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) playerControllerView.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                }


                drawPadView.initChangedScreen( currentWidth, currentHeight);
                screenstate=MyVideoView.SCREENSTATE.FULLSCREEN_DRAWPADVIEW;
                break;
            }
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE: {
                //取消全屏
                WindowManager.LayoutParams attrs = getWindow().getAttributes();
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().setAttributes(attrs);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fl_all.removeView(drawPadView);
                li_drawpaddview.addView(drawPadView);
                LinearLayout.LayoutParams lp4= (LinearLayout.LayoutParams) drawPadView.getLayoutParams();
                currentWidth=BASE_WIDTH;
                currentHeight=(int)(BASE_WIDTH *DRAWPADVIEW_SCALE);
                lp4.width= currentWidth;
                lp4.height=currentHeight;

                if(playerControllerView!=null) {
                    fl_all.removeView(playerControllerView);
                    playerControllerView.setVisibility(View.VISIBLE);
                    all_activity_layout.addView(playerControllerView);
                }

                if(null!=chatView){
                    chatView.setVisibility(View.VISIBLE);
                }
                drawPadView.initChangedScreen( currentWidth, currentHeight);
                screenstate=MyVideoView.SCREENSTATE.NORMAL;
                break;

            }
        }

        LogUtil.d("---test1","changedDrawPadViewScreen is end");

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogUtil.d("---test1", "onConfigurationChanged is runing");
        super.onConfigurationChanged(newConfig);
        int requestedOrientation = EplayerPluginActivity.this.getRequestedOrientation();
        if (screenstate == MyVideoView.SCREENSTATE.FULLSCREEN_VIDEOVIEW) {
            scrollview.scrollTo(0, 0);
        }
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            screenstate = MyVideoView.SCREENSTATE.NORMAL;
        }
        handler.sendEmptyMessageDelayed(TaskType.MESSAGE_CHANGESCREEN_SCUESS,500);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //横屏时按返回键，退回到竖屏模式
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            int requestedOrientation = getRequestedOrientation();
            if (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE == requestedOrientation) {
                switch (screenstate){
                    case FULLSCREEN_DRAWPADVIEW:{
                        changedDrawPadViewScreen();
                        return true;
                    }
                    case FULLSCREEN_VIDEOVIEW:{
                        changedVideoViewScreen();
                        return true;
                    }
                }

            }
            LiveRoomInfoData data=EplayerSessionInfo.sharedSessionInfo().infoData;

            if(null!=data&&!data.canSplice&&playbackLoadingFlag&&EplayerSetting.isPlayback){
                ToastUtil.showStringToast(context,"播放器正在缓冲中，请稍候退出");
                return true;
            }

            if ((System.currentTimeMillis() - exitTime) > 2500) // System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                ToastUtil.showStringToast(context, "再按一次返回键退出直播间");
                exitTime = System.currentTimeMillis();
            } else {
                EplayerSessionInfo.releaseALL();
                finish();

            }
            return true;
        }
        return super.onKeyDown (keyCode, event);
    }
    AlertDialog loadingFailedDialog=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate is running");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.living = true;

        com.soooner.source.common.util.LogUtil.d("-----onCreate---"+Thread.currentThread().getName());

        setContentView(R.layout.eplayer_activity);
        context = this;
        curent_reconnection=RECONNECTION_DEFAULT_NUM;
        LogUtil.d(TAG,"onCreate is running");

//        AudioManager audioManager = (AudioManager)context.getSystemService(Activity.AUDIO_SERVICE);// 获取音量服务
//        int maxSound = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 获取系统音量最大值
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxSound, 0); //tempVolume:音量绝对值

        video_margin_left_right= (int) getResources().getDimension(R.dimen.video_margin_left_right);

        voteControllerView= (VoteControllerView) findViewById(R.id.voteControllerView);
        voteControllerView.init(this,DeviceUtil.DEVICE_TYPE_PHONE);

        EplayerSetting.context = this.getApplicationContext();

        fl_all= (RelativeLayout) findViewById(R.id.fl_all);
        chatView= (MyChatView) findViewById(R.id.mychatview);
        chatView.init(this,DeviceUtil.DEVICE_TYPE_PHONE);

        drawPadView = (DrawPadView) findViewById(R.id.draw_Pad_View);

        view_title=findViewById(R.id.view_title);
        li_drawpaddview= (LinearLayout) findViewById(R.id.li_drawpaddview);


        drawPadView.setWhiteBoardListener(new DrawPadView.WhiteBoardListener() {

            @Override
            public void onClick(View view) {
                LogUtil.d(TAG, "drawPadView onclick");
                if (screenstate == MyVideoView.SCREENSTATE.NORMAL || screenstate == MyVideoView.SCREENSTATE.FULLSCREEN_DRAWPADVIEW) {

                    if (screenstate == MyVideoView.SCREENSTATE.FULLSCREEN_DRAWPADVIEW) {
                        if (playerControllerView != null && playerControllerView.getVisibility() == View.VISIBLE) {
                            playerControllerView.setVisibility(View.INVISIBLE);
                            return;
                        }
                    }
                    changedDrawPadViewScreen();
                }


            }

            @Override
            public void onLoadingFailed() {
                try {
                    EplayerPluginActivity.this.requestStop();
                    if (null == loadingFailedDialog) {
                        loadingFailedDialog = new AlertDialog.Builder(EplayerPluginActivity.this)
                                .setTitle("提示")
                                .setMessage("您的网络太糟糕，无法加载图片，请重试或者切换到稳定网络！")
                                .setPositiveButton(
                                        "确定",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int whichButton) {
                                                finish();
                                            }
                                        }).setCancelable(false).create();
                    }
                    if (!loadingFailedDialog.isShowing()) {
                        loadingFailedDialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDownLoadAllPPT(DrawPadInfo drawPadInfo) {

            }

            @Override
            public void onResertCurrentppt() {

            }


        });
        Bitmap defaulteBitmap = ImageUtil.readLocalBitMap(EplayerPluginActivity.this.getApplicationContext(), R.drawable.ppt_bg_bg);
        drawPadView.setDefaulteBitmap(defaulteBitmap);
        drawPadView.setWhiteBoardSwithListener(new DrawPadView.WhiteBoardSwithListener() {

            @Override
            public Bitmap whiteBoardBitmap(DrawPadColorType colorType) {

                if (colorType == DrawPadColorType.DrawPadColorTypeBlack) {
                    return ImageUtil.readLocalBitMap(EplayerPluginActivity.this.getApplicationContext(), R.drawable.blank_bg_black);
                } else if (colorType == DrawPadColorType.DrawPadColorTypeGreen) {
                    return ImageUtil.readLocalBitMap(EplayerPluginActivity.this.getApplicationContext(), R.drawable.blank_bg_green);
                } else if (colorType == DrawPadColorType.DrawPadColorTypeWhite) {
                    return ImageUtil.readLocalBitMap(EplayerPluginActivity.this.getApplicationContext(), R.drawable.blank_bg_white);
                }

                return ImageUtil.readLocalBitMap(EplayerPluginActivity.this.getApplicationContext(), R.drawable.blank_bg_green);

            }
        });



        scrollview= (ScrollView) findViewById(R.id.scrollview);
        scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(screenstate==MyVideoView.SCREENSTATE.FULLSCREEN_VIDEOVIEW){
                    return true;
                }
                return false;
            }
        });



        listview= (ListView) findViewById(R.id.listview);

        fl_chat= (LinearLayout) findViewById(R.id.fl_chat);
        fl_status= (LinearLayout) findViewById(R.id.fl_status);
        scrollview_content= (LinearLayout) findViewById(R.id.scrollview_content);
        all_activity_layout= (LinearLayout) findViewById(R.id.all_activity_layout);

        top_title_title= (TextView) findViewById(R.id.top_title_title);
        top_title_right= (TextView) findViewById(R.id.top_title_right);
        top_title_right.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String hintStr = chatView.changeSpeakState();
                top_title_right.setText(hintStr);
            }
        });
        li_top_title_left= (LinearLayout) findViewById(R.id.li_top_title_left);
        li_top_title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playbackLoadingFlag&&EplayerSetting.isPlayback){
                    ToastUtil.showStringToast(context,"播放器正在缓冲中，请稍候退出");
                    return ;
                }
                EplayerSessionInfo.releaseALL();
                finish();
            }
        });


        tv_middle_state=(TextView) findViewById(R.id.tv_middle_state);
        tv_online_num= (TextView) findViewById(R.id.tv_online_num);

        fl_myvideoview= (MyVideoView) findViewById(R.id.fl_myvideoview);
        fl_myvideoview.init(this,DeviceUtil.DEVICE_TYPE_PHONE,videoViewListener);



        li_load= (LinearLayout) findViewById(R.id.li_load);
        li_load.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //什么也不做，只是让后面的控件不响应点击事件
            }
        });
        li_load_myprogressbar= (MyProgressBar) findViewById(R.id.li_load_myprogressbar);
        li_load_myprogressbar.init(MyProgressBar.TYPE_LINEARLAYOUT_LAYOUTPARAMS);







        adapter=new SpeakAdapter(this);
        listview.setAdapter(adapter);
        listview.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        enable_refresh_listview=false;
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:{
                        enable_refresh_listview=true;
                        break;
                    }
                    case  MotionEvent.ACTION_UP:{
                        enable_refresh_listview=true;
                        break;
                    }
                }

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    scrollview.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        //下面给视频view、ppt设置显示的大小
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        SCREEN_WIDTH=metrics.widthPixels;
        SCREEN_HEIGHT=metrics.heightPixels;
        Resources res = getResources();
         video_margin_left_right = (int) res.getDimension(R.dimen.video_margin_left_right);
        BASE_WIDTH =(int)(SCREEN_WIDTH-video_margin_left_right*2);

        fl_myvideoview.resetSize(BASE_WIDTH,(int)(BASE_WIDTH *CHANGPIAN_SCALE),video_margin_left_right);


        LinearLayout.LayoutParams lp3= (LinearLayout.LayoutParams) li_drawpaddview.getLayoutParams();
        lp3.width= BASE_WIDTH;
        lp3.height=(int)(BASE_WIDTH *DRAWPADVIEW_SCALE);

        LinearLayout.LayoutParams lp4= (LinearLayout.LayoutParams) drawPadView.getLayoutParams();
        lp4.width= BASE_WIDTH;
        lp4.height=(int)(BASE_WIDTH *DRAWPADVIEW_SCALE);



        EventBus.getDefault().register(this);

        Bundle bd = getIntent().getExtras();
        key_liveClassroomId = StringUtils.getStringResFormBundle(bd, EPLAY_LIVECLASSROOMID);
        key_username = StringUtils.getStringResFormBundle(bd, EPLAY_USERNAME);
        key_userpwd = StringUtils.getStringResFormBundle(bd, EPLAY_USERPWD);

        key_exstr = StringUtils.getStringResFormBundle(bd, EPLAY_EXSTR);

        key_customer=StringUtils.getStringResFormBundle(bd, EPLAY_CUSTOMER);

//        if (EplayerSetting.isPlayback){
        if (StringUtils.containsKeyFormBundle(bd, EPLAY_PID)){
            EplayerSetting.isPlayback = true;

            playbackEngin =  new PlaybackEngin();
            playbackEngin.setListener(this);
        }else{
            EplayerSetting.isPlayback = false;
        }
        key_pid=StringUtils.getStringResFormBundle(bd, EPLAY_PID);

        DeviceUtil.getUserAgentString();


        if (!EplayerSetting.isPlayback) {

        }else {
            playerControllerView= (PlayerControllerView) findViewById(R.id.playerControllerView);
            playerControllerView.setVisibility(View.INVISIBLE);
            playerControllerView.init(this, DeviceUtil.DEVICE_TYPE_PHONE, new PlayerControllerView.MyControllerListener(){

                @Override
                public void previousPlaybackPPT() {
                    DrawPadInfo  drawPadInfo =  EplayerSessionInfo.sharedSessionInfo().drawPadInfo;
                    if(drawPadInfo==null)
                        return;
                    int page = playbackEngin.prePagePPtId(drawPadInfo.pptId,drawPadInfo.page);
                    if(page>=0){
                        EplayerPluginActivity.this.playerStartPlay= false;
                        fl_myvideoview.stopPlayback();

                        if (playbackEngin.isPlayback()){
                            playbackEngin.pausePlayback();
                        }else{
                            fl_myvideoview.showStateView(false);
                            playerControllerView.changePlayerState(PlayerControllerView.PlayerState.PLAYERSTATE_PAUSE);
                            fl_myvideoview.changeHintText("");
                        }
                        playbackEngin.resetPlaybackPPtId(drawPadInfo.pptId,page);
                    }
                }

                @Override
                public void nextPlaybackPPT() {
                    DrawPadInfo  drawPadInfo =  EplayerSessionInfo.sharedSessionInfo().drawPadInfo;
                    if(drawPadInfo==null)
                        return;
                    int page = playbackEngin.nextPagePPtId(drawPadInfo.pptId,drawPadInfo.page);
                    if(page>=0){
                        EplayerPluginActivity.this.playerStartPlay= false;
                        fl_myvideoview.stopPlayback();

                        if (playbackEngin.isPlayback()){
                            playbackEngin.pausePlayback();
                        }else{
                            fl_myvideoview.showStateView(false);
                            playerControllerView.changePlayerState(PlayerControllerView.PlayerState.PLAYERSTATE_PAUSE);
                            fl_myvideoview.changeHintText("");
                        }
                        playbackEngin.resetPlaybackPPtId(drawPadInfo.pptId,page);
                    }
                }

                @Override
                public void pausePlayback() {
                    EplayerPluginActivity.this.playerStartPlay= false;



                    fl_myvideoview.pause();
                    playbackEngin.pausePlayback();
                    playerControllerView.changePlayerState(PlayerControllerView.PlayerState.PLAYERSTATE_PLAY);
                    fl_myvideoview.changeHintText("播放暂停");

                    fl_myvideoview.showStateView(true);
                }

                @Override
                public void resumePlayback() {

                    fl_myvideoview.showStateView(false);

                    fl_myvideoview.start();
                    playbackEngin.resumePlayback();
                    playerControllerView.changePlayerState(PlayerControllerView.PlayerState.PLAYERSTATE_PAUSE);
                    fl_myvideoview.changeHintText("");

                }


                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    com.soooner.source.common.util.LogUtil.d("-----onStartTrackingTouch:"+seekBar.getProgress());
                    EplayerPluginActivity.this.playerStartPlay= false;
                    fl_myvideoview.stopPlayback();
                    playbackEngin.pausePlayback();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    fl_myvideoview.showStateView(false);
                    com.soooner.source.common.util.LogUtil.d("-----onStopTrackingTouch:" + seekBar.getProgress());
                    playbackEngin.resumePlayback(seekBar.getProgress());
                    playerControllerView.changePlayerState(PlayerControllerView.PlayerState.PLAYERSTATE_PAUSE);
                    fl_myvideoview.showVideoviewProgressbar();
                }
            });
            playerControllerView.setEnabled(false);


        }

        //TODO 隐藏
        scrollview.setVisibility(View.INVISIBLE);
        chatView.setVisibility(View.INVISIBLE);
        fl_chat.setVisibility(View.INVISIBLE);
        fl_status.setVisibility(View.INVISIBLE);
        top_title_right.setVisibility(View.INVISIBLE);


        ShowLoading();

        if (StringUtils.isValid(key_liveClassroomId)
                && StringUtils.isValid(key_username)
                && StringUtils.isValid(key_userpwd)&& StringUtils.isValid(key_customer)) {

            EplayerPluginActivity.this.currentStaticLoginTimeMillis = System.currentTimeMillis();
            if(loginTimer!=null) {
                loginTimer.cancel();
                loginTimer = null;
            }
            loginTimer = new Timer();
            loginTimer.schedule(new LoginTimerTask(EplayerPluginActivity.this.currentStaticLoginTimeMillis),100000);


            new GetLiveListThread(key_liveClassroomId, key_customer,key_username, key_userpwd).start();
        } else if (StringUtils.isValid(key_exstr) && StringUtils.isValid(key_liveClassroomId)&& StringUtils.isValid(key_customer)) {


            EplayerPluginActivity.this.currentStaticLoginTimeMillis = System.currentTimeMillis();
            if(loginTimer!=null) {
                loginTimer.cancel();
                loginTimer = null;
            }
            loginTimer = new Timer();
            loginTimer.schedule(new LoginTimerTask(EplayerPluginActivity.this.currentStaticLoginTimeMillis),100000);

            new GetLiveListThread(key_liveClassroomId,key_customer, key_exstr).start();
        } else if( StringUtils.isValid(key_liveClassroomId)&& StringUtils.isValid(key_customer)){
            EplayerPluginActivity.this.currentStaticLoginTimeMillis = System.currentTimeMillis();
            if(loginTimer!=null) {
                loginTimer.cancel();
                loginTimer = null;
            }
            loginTimer = new Timer();
            loginTimer.schedule(new LoginTimerTask(EplayerPluginActivity.this.currentStaticLoginTimeMillis),100000);

            new GetLiveListThread(key_liveClassroomId,key_customer, null).start();
        } else {
            ToastUtil.showToast(context, R.string.liveClassroomId_username_userpwd);
            EplayerSessionInfo.releaseALL();
            finish();
        }




    }

    //播放器不应该直接放到外面，因为播放器本身会根据视频大小改变自己的大小，会导致播放器重复使用的时候或者横竖屏切换后无法知道其父控件大小
    //目前只能使用这个非常办法：在每次播放之前重新设置一下播放器大小
    public void resetVideoSize(){


        if(screenstate==MyVideoView.SCREENSTATE.FULLSCREEN_VIDEOVIEW){
            fl_myvideoview.resetSize(SCREEN_HEIGHT,SCREEN_WIDTH,0);
        }else{
            fl_myvideoview.resetSize(BASE_WIDTH,(int)(BASE_WIDTH *CHANGPIAN_SCALE),video_margin_left_right);
        }

    }



    public class LoadingTimerTask extends TimerTask{

        private long  currentTimeMillis;

        public LoadingTimerTask(long  currentTimeMillis){
            this.currentTimeMillis = currentTimeMillis;
        }

        @Override
        public void run() {
//            if(EplayerPluginActivity.this.loadingTimeoutShow)
//                return;
//
//
           long currentStaticTimeMillis = EplayerPluginActivity.this.currentStaticTimeMillis;
            if(currentTimeMillis==currentStaticTimeMillis){

                EplayerPluginActivity.this.currentStaticTimeMillis = System.currentTimeMillis();

                if (curent_reconnection < RECONNECTION_MAX_NUM) {
                    LogUtil.d(TAG,"LoadingTimerTask connect age,curent_reconnection:" + curent_reconnection);
                    curent_reconnection++;
                //TODO: MESSAGE_CHANGE_LIVE_STATUS
                LiveRoomInfoData infoData= EplayerSessionInfo.sharedSessionInfo().infoData;

                if (!infoData.playMusic) {
                    handler.sendEmptyMessage(TaskType.MESSAGE_CHANGE_LIVE_STATUS);
                }
                } else {
                    EplayerPluginActivity.this.streamErrorTimes++;

                    EplayerPluginActivity.this.loadingTimeoutShow = true;
                    EplayerPluginActivity.this.requestStop();

                    EplayerPluginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(EplayerPluginActivity.this)
                                    .setTitle("提示")
                                    .setMessage("您的网络太差啦，无法稳定的播放音视频，请切换到稳定网络观看")
                                    .setPositiveButton(
                                            "确定",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int whichButton) {
                                                    finish();
                                                }
                                            }).setCancelable(false).create().show();

                        }
                    });
                    LogUtil.d(TAG, "LoadingTimerTask excute");
                }

            } else {

                LogUtil.d(TAG, "LoadingTimerTask igron");
            }
        }
    }
    //初始化直播状态
    public void initPlayState(LiveRoomLiveStatus liveStatus){
        String state_str="";
        switch (liveStatus.value()){
            case 0:{//LiveRoomLiveStatusStop
                state_str="直播未开始";
                break;
            }
            case 1:{//LiveRoomLiveStatusPlay(1)
                state_str="正在直播中";
                break;
            }
            case 2:{//LiveRoomLiveStatusPause(2)
                state_str="直播暂停中";
                break;
            }
            case 3:{//LiveRoomLiveStatusClose(3);
                state_str="直播已关闭";
                break;
            }
        }
        tv_middle_state.setText(state_str);

        String hint="";
        if(EplayerSetting.isPlayback){
            if(liveStatus.value() == 3){
                hint="播放结束";

            }else{
                hint="视频/音频无法播放";
            }
        }else{

            if(liveStatus.value()==2){
                hint="直播暂停";

            }else if(liveStatus.value() == 3){
                hint="直播已关闭";
            }else{
                hint="视频/音频无推送";
            }
        }

        fl_myvideoview.changeHintText(hint);
    }






    public void ShowLoading() {
        li_load.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        if (li_load.getVisibility() == View.VISIBLE)
            li_load.setVisibility(View.GONE);
    }




    //EventBus回调函数
    public void onEventBackgroundThread(HandShakeEvent event) {
        Sender.joinRoom();
    }

    public void onEventBackgroundThread(JoinRoomEvent event) {
        handler.sendEmptyMessage(TaskType.MESSAGE_HIDELOADING);
        handler.sendEmptyMessage(TaskType.MESSAGE_INIT_PADVIEW);

        EplayerPluginActivity.this.currentStaticLoginTimeMillis = System.currentTimeMillis();
        if(loginTimer!=null) {
            loginTimer.cancel();
            loginTimer = null;
        }

        LiveRoomInfoData infoData= EplayerSessionInfo.sharedSessionInfo().infoData;
        setAllChatForbid(infoData.canChat);
        if(null!=infoData){

            handler.sendEmptyMessage(TaskType.MESSAGE_INITBLACKLIST);


            Message message=Message.obtain();
            message.what=TaskType.MESSAGE_CHATCONTROLRES;
            message.obj=infoData;
            handler.sendMessage(message);


        }

        Sender.initStatusReq();
        initStreamPushAndplayMusic();
    }

    /*

     */
    public void initStreamPushAndplayMusic(){
        LiveRoomInfoData infoData= EplayerSessionInfo.sharedSessionInfo().infoData;

        if (infoData.playMusic) {
            new GetMusicInfoThread(EplayerSessionInfo.sharedSessionInfo().userInfo.liveClassroomId, infoData.musicType + "").start();
        }
//            handler.sendEmptyMessage(TaskType.MESSAGE_CHANGE_LIVE_STATUS);


    }

    public void onEventBackgroundThread(VideoAudioStatusEvent event) {

        EplayerPluginActivity.this.currentStaticTimeMillis = System.currentTimeMillis();
        if(loadingTimer!=null) {
            loadingTimer.cancel();
            loadingTimer = null;
        }

        EplayerPluginActivity.this.currentStaticErrorTimeMillis = System.currentTimeMillis();
        if(errorTimer!=null) {
            errorTimer.cancel();
            errorTimer = null;
        }

        handler.sendEmptyMessage(TaskType.MESSAGE_CHANGE_LIVE_STATUS);

    }

    //点赞后的回调
    public void onEventBackgroundThread(PraiseEvent event) {
        Prainse prainse= event.getPrainse();
        Message message=Message.obtain();
        message.what=TaskType.MESSAGE_PRAISE_RES;
        message.obj=prainse;
        handler.sendMessage(message);

    }

    public void requestStop(){
        try {
            this.living = false;
            if(loading!=null){
                loading.stopLoading();
            }
            handler.removeMessages(TaskType.MESSAGE_CHANGESCREEN_SCUESS);


            if(playbackEngin!=null){
                playbackEngin.closePlayback();
                playbackEngin.setListener(null);
                playbackEngin = null;
            }

            EventBus.getDefault().unregister(this);
            if(drawPadView!=null) {
                drawPadView.stopEventListener();
                drawPadView.releaseALL();
                EventBus.getDefault().unregister(drawPadView);
                drawPadView = null;
            }
            EplayerSocket.close();
            EplayerSetting.isPlayback = false;
            EplayerPluginActivity.this.currentStaticTimeMillis = System.currentTimeMillis();
            if(loadingTimer!=null) {
                loadingTimer.cancel();
                loadingTimer = null;
            }
            EplayerPluginActivity.this.playerStartPlay= false;
            fl_myvideoview.stopPlayback();
            StorageUtil.cleanAll();//cleanCacheDir();
            EPlaybackSessionInfo.releaseALL();

            EplayerPluginActivity.this.currentStaticLoginTimeMillis = System.currentTimeMillis();
            if (loginTimer != null) {
                loginTimer.cancel();
                loginTimer = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


   //播放音乐
    public void onEventBackgroundThread(MusicEvent event) {
        if(event.getInfoData().playMusic) {
            int musicType=event.getInfoData().musicType;
            new GetMusicInfoThread(EplayerSessionInfo.sharedSessionInfo().userInfo.liveClassroomId,musicType+"").start();
        }else{
            handler.sendEmptyMessage(TaskType.MESSAGE_STOP_MUSIC_ERROR);
        }

    }

    /**
     * Socket.IO：请求随机播放音乐
     */
    private class GetMusicInfoThread extends Thread {

        String liveClassRoomId;
        String musicType;

        private GetMusicInfoThread(String liveClassRoomId, String musicType) {
            this.liveClassRoomId = liveClassRoomId;
            this.musicType = musicType;
        }

        @Override
        public void run() {

            try {
                GetMusicInfoProtocol protocol = new GetMusicInfoProtocol(liveClassRoomId, musicType);
                protocol.execute();

                String musicPath = protocol.getMusicPath();
                if (com.soooner.source.common.util.StringUtils.isValid(musicPath)) {

                    Message message = handler.obtainMessage();
                    message.what = TaskType.MESSAGE_PLAY_MUSIC;
                    message.obj = musicPath;
                    handler.sendMessage(message);

                }
            } catch (Exception e) {
                handler.sendEmptyMessage(TaskType.MESSAGE_PLAY_MUSIC_ERROR);
            }

        }
    }




    //禁言和取消禁言(某个人的)
    public void onEventBackgroundThread(ForbinChatEvent event) {
        ForbidMessage forbidMessage=event.getForbidMessage();
        if(null==forbidMessage){
            return;
        }
        Message message=Message.obtain();
        message.what=TaskType.MESSAGE_FORBIDCHATRES;
        message.obj=forbidMessage;
        handler.sendMessage(message);
    }

    //聊生控制(禁言和取消禁言全部人的)
    public void onEventBackgroundThread(ChatControlEvent event) {
        LiveRoomInfoData infoData= event.getInfoData();
        setAllChatForbid(infoData.canChat);

        if(null!=infoData){
            Message message=Message.obtain();
            message.what=TaskType.MESSAGE_CHATCONTROLRES;
            message.obj=infoData;
            handler.sendMessage(message);
        }

    }


    //获取在线人数
    public void onEventBackgroundThread(UserCountEvent event) {
       int count= event.getCount();
        Message message=Message.obtain();
        message.what=TaskType.MESSAGE_REFRESH_ONLINE_NUM;
        message.obj=count;
        handler.sendMessage(message);
    }

    //获取到聊天
    public void onEventBackgroundThread(SocketMessageEvent event) {
        LogUtil.d(TAG,"SocketMessageEvent is running");
       // handler.sendEmptyMessage(TaskType.MESSAGE_HIDELOADING);
        try {

            synchronized (bufferList){
                List<SocketMessage> list = (List<SocketMessage>) event.getData();
                for (SocketMessage message:list){
                    if(message.chatInfoKey!=null&&!msgKeys.contains(message.chatInfoKey)){
                        bufferList.add(message);
                        msgKeys.add(message.chatInfoKey);
                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //踢人
    public void onEventBackgroundThread(FourceLogoutEvent event) {
        //todo
        handler.sendEmptyMessage(TaskType.MESSAGE_FOURCELOGOUT);
        requestStop();

    }

    //老师投票请求
    public void onEventBackgroundThread(VoteMsgInfoEvent event) {
        //todo
        VoteMsgInfo msgInfo= event.getInfoData();
        if(null!=msgInfo) {
            Message message = Message.obtain();
            message.what = TaskType.MESSAGE_VOTE_REQ;
            message.obj = msgInfo;
            handler.sendMessage(message);
        }
    }

    //学生投票结果
    public void onEventBackgroundThread(VoteMsgInfoResEvent event) {
        //todo

        Message message=Message.obtain();
        message.what=TaskType.MESSAGE_VOTE_STUDENT_RES;
        message.obj=event;
        handler.sendMessage(message);
    }

     //投票统计结果
    public void onEventBackgroundThread(VoteStatisticMsgInfoEvent event) {
        VoteStatisticMsgInfo voteStatisticMsgInfo= event.getInfoData();

        Message message=Message.obtain();
        message.what=TaskType.MESSAGE_VOTE_STATISTIC;
        message.obj=voteStatisticMsgInfo;
        handler.sendMessage(message);
    }

    public void showAlertDialog() {
        TextView  text=new TextView(this);
        ViewGroup.LayoutParams lp =text.getLayoutParams();
        text.setTextSize(18);

        text.setGravity(Gravity.CENTER);
        text.setText("\n帐号在其它位置登陆,\n您已经被迫下线!\n");
        EplayerPluginActivity.this.requestStop();

        AlertDialog.Builder edit_builder = new AlertDialog.Builder(context);
        edit_builder.setCancelable(false);
        edit_builder.setTitle("下线提醒");
        edit_builder.setView(text);
        edit_builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EplayerSessionInfo.releaseALL();
                  finish();
            }
        });


        edit_builder.show();

    }

    @Override
    protected void onResume() {
        if(!EplayerSetting.isPlayback) {
            refreshTimer = new Timer();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(TaskType.MESSAGE_REFRESH_LISTVIEW);
                }
            };
            refreshTimer.schedule(tt, 1000, 1000);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(!EplayerSetting.isPlayback) {
            refreshTimer.cancel();
        }
        super.onPause();
    }

    private class PlaybackLoadingThread extends  Thread{

        String liveClassroomId = "";
        String pid = "";
        public PlaybackLoadingThread(String liveClassroomId, String pid) {
            this.liveClassroomId = liveClassroomId;
            this.pid = pid;
        }

        @Override
        public void run() {

            try {

                EplayerPluginActivity.this.loading = new PlaybackLoading();
                EplayerPluginActivity.this.loading.liveClassroomId = this.liveClassroomId;
                EplayerPluginActivity.this.loading.pid = this.pid;
                EplayerPluginActivity.this.loading.startLoading();

            }catch (Exception e){
                e.printStackTrace();
            }


            handler.sendEmptyMessage(TaskType.MESSAGE_HIDELOADING);
            handler.sendEmptyMessage(TaskType.MESSAGE_INIT_PADVIEW);

            EplayerPluginActivity.this.currentStaticLoginTimeMillis = System.currentTimeMillis();
            if(loginTimer!=null) {
                loginTimer.cancel();
                loginTimer = null;
            }


        }
    }

    private class GetLiveListThread extends Thread {

        String liveClassroomId = "";
        String username = "";
        String userpwd = "";
        String key_customer="";

        String exStr = "";

        public GetLiveListThread(String liveClassroomId, String key_customer,String username, String userpwd) {
            this.liveClassroomId = liveClassroomId;
            this.username = username;
            this.userpwd = userpwd;
            this.key_customer=key_customer;
        }

        public GetLiveListThread(String liveClassroomId, String key_customer,String exStr) {
            this.liveClassroomId = liveClassroomId;
            this.exStr = exStr;
            this.key_customer=key_customer;
        }


        @Override
        public void run() {
            try {
                {
                    if(!EplayerSetting.isTestServer){
                        GetWayProtocol protocol =  new GetWayProtocol();
                        protocol.execute();
                    }
                }
                {
                    UserLoginProtocol protocol = null;
                    if (StringUtils.isValid(exStr)) {
                        protocol = new UserLoginProtocol(liveClassroomId, key_customer, exStr);
                    } else if(StringUtils.isValid(username)&&StringUtils.isValid(userpwd)) {
                        protocol = new UserLoginProtocol(liveClassroomId, key_customer, username, userpwd);
                    }else{
                        protocol = new UserLoginProtocol(liveClassroomId, key_customer, null, null);
                    }

                    protocol.execute();

                    if (protocol.errorCode != 0) {
                        Message message = Message.obtain();
                        message.what = TaskType.MESSAGE_LOGIN_ERROR;
                        message.arg1 = protocol.errorCode;
                        handler.sendMessage(message);
                    } else {
                        handler.sendEmptyMessage(TaskType.MESSAGE_LOGIN_SUCESS);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onStop() {
        LogUtil.d(TAG,"onStoponStoponStop");
        EplayerSessionInfo.releaseALL();
        requestStop();
        finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG,"onDestroy");
        requestStop();

        super.onDestroy();
    }






}
