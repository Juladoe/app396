package com.soooner.EplayerPluginLibary;

import android.app.AlertDialog;
import android.content.*;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.soooner.EplayerPluginLibary.adapter.PPTGridAdapter;
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
import com.soooner.source.common.util.DateUtil;
import com.soooner.source.entity.EPlayerData;
import com.soooner.source.entity.PicUrl;
import com.soooner.source.entity.Prainse;
import com.soooner.source.entity.SessionData.*;
import com.soooner.source.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.soooner.source.entity.SessionEmun.DrawPadColorType;
import com.soooner.source.entity.SessionEmun.LiveRoomLiveStatus;
import com.soooner.source.entity.SessionEmun.LiveRoomStreamType;
import com.soooner.source.protocol.GetMusicInfoProtocol;
import com.soooner.source.protocol.GetWayProtocol;
import com.soooner.source.protocol.UserLoginProtocol;
import com.soooner.source.system.ImageLoader;
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
//TODO 重构时需要考虑 EplayerPluginActivity 中相同的部分
//TODO

public class EplayerPluginPadActivity extends EplayerPluginBaseActivity  implements PlaybackEngin.OnEnginListener {
    public  static  final  int RESERT_CURRENT_PPT_DELAY=8000;
    private  boolean userChangeImage=false;//用户手动改变了背景图



    public static final int BASE_SCREEN_WIDTH=2048;//设计图上屏幕宽度
    public static final int BASE_SCREEN_HEIGHT=1536;//设计图上屏幕高度



    double BASE_SCREEN_WIDTH_SCALE=0;
    double BASE_SCREEN_HEIGHT_SCALE=0;

    int   FL_VIDEO_BASE_WIDTH=0;//视频宽

    RelativeLayout rl_content_all;

    public static String EPLAY_DATA = "eplay_data";

    public static final int GRIDVIEW_PLAYBACK_NUM=7;

    private EventBus bus;

    Animation shake;

    PlaybackLoading loading;


    public EPlayerData playerData;

//    public String key_liveClassroomId, key_username, key_userpwd, key_exstr,key_customer,key_pid;

    private static final String TAG = Sender.class.getSimpleName();
    Context context;
    private DrawPadView drawPadView;
    RelativeLayout fl_all;
    LinearLayout li_drawpaddview,li_teacher_area;

    MyVideoView fl_myvideoview;


    TextView top_title_title,top_title_left;
    LinearLayout li_top_title_left;
    ListView  listview;
    SpeakAdapter adapter;
    TextView tv_middle_state,tv_online_num;

    ImageView img_video_logo;
    MyChatView chatView;

    SpeakState speakState=SpeakState.STATE_SPEAK;//当前是发言还是提问的标识

    private long exitTime;//保存上一次的退出时间 ；单位 毫秒
    enum SpeakState{
        STATE_SPEAK,STATE_QUESTION
    }


//    LinearLayout  activity_control;

    LinearLayout li_progressbar_bg,li_load;
    MyProgressBar li_load_myprogressbar;


    TextView tv_top_state;




    List<SocketMessage> bufferList=new ArrayList<SocketMessage>();
    HashSet<String> msgKeys = new HashSet<String>();


    private static  final  int HIDE_TIME=5000;




    Timer refreshTimer=new Timer();
    boolean enable_refresh_listview =true;

    boolean progressbar_show = true;


    LineGridView gridview;
    PPTGridAdapter pptGridAdapter;
    HorizontalScrollView hs_gridview;

    LinearLayout li_line_line_num,all_activity_layout;


    Resources res;
    int PAD_SPACE_MARGIN;//边距
    RelativeLayout rl_top_title;

   LinearLayout li_chat_area,li_chat_title;
   View view_barrier;
    TextView tv_teacher_name,tv_teacher_time;
    ImageView img_teacher_area_title;
    LinearLayout li_teacher_area_title;

    long currentPlaybackTime;

    boolean playbackLoadingFlag =false;//todo 回看时，当正在缓冲时按两次返回键会因为播放器出现崩溃的问题，所有专门为这种情况设置了一个变量
    PlayerControllerView playerControllerView;
    VoteControllerView voteControllerView;

    boolean playerStartPlay = false;

    MyVideoView.VideoViewListener videoViewListener=new MyVideoView.VideoViewListener(){

        @Override
        public void onVideoviewClick() {
            LogUtil.d(TAG, "my_videoview onClick MotionEvent.ACTION_DOWN");

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

            if(EplayerPluginPadActivity.this.playbackEngin!=null) {
                EplayerPluginPadActivity.this.playbackEngin.pausePlayback();
            }


            ttu.addErrorTask(TimeTaskUtils.TimeTaskType.TIMETASK_ERROR, RECONNECTION_MAX_NUM, 5000, false, true);

            return true;
        }

        @Override
        public void onStateChanged(IMediaPlayer mp, boolean pause) {
            progressbar_show = pause;

            if (!EplayerSessionInfo.sharedSessionInfo().infoData.playMusic) {
                if (pause) {

                    if (EplayerPluginPadActivity.this.playerStartPlay) {
                        ttu.addTask(TimeTaskUtils.TimeTaskType.TIMETASK_LOAD,RECONNECTION_MAX_NUM,15000,false);

                    }
                } else {

                    ttu.clearTask(TimeTaskUtils.TimeTaskType.TIMETASK_LOAD);

                }
                if(null!=playerControllerView&&playerControllerView.isManualPausePlayer())
                    return;

                EplayerPluginPadActivity.this.runOnUiThread(new Runnable() {

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
            playbackLoadingFlag =true;
            if (!EplayerSessionInfo.sharedSessionInfo().infoData.playMusic) {


                ttu.addTask(TimeTaskUtils.TimeTaskType.TIMETASK_LOAD,RECONNECTION_MAX_NUM,30000,false);

            }
        }

        @Override
        public void onLoadingEnd(IMediaPlayer mp) {
            playbackLoadingFlag =false;
            if (!EplayerSessionInfo.sharedSessionInfo().infoData.playMusic) {

                ttu.clearTask(TimeTaskUtils.TimeTaskType.TIMETASK_ERROR);
                ttu.clearTask(TimeTaskUtils.TimeTaskType.TIMETASK_LOAD);
                handler.sendEmptyMessage(TaskType.MESSAGE_ENABLE_CONTROL);



            }
        }
    };

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
        com.soooner.source.common.util.LogUtil.d("------------"+progress+"-------------");
        currentPlaybackTime  =  progress;

        playerControllerView.showProgressTime(progress,totalTime,playbackTime,playbackBeginTime,playbackEndTime);

    }

    //int statusBarHeight=0;//获取状态栏高度


    @Override
    public  void handleLoginOutTime(boolean isEnd){

        requestStop();

        createAlertDialog("提示","您的网络太差啦，无法登录房间，请重试或者切换到稳定网络！");


    }
    @Override
    public  void handleErrorinOutTime(boolean isEnd){

        if(!isEnd){

            if(EplayerSetting.isPlayback){
                LogUtil.d(TAG, "ErrorTimerTask currentPlaybackTime:" + EplayerPluginPadActivity.this.currentPlaybackTime);
                EplayerPluginPadActivity.this.playerStartPlay= false;
                fl_myvideoview.stopPlayback();

                if( EplayerPluginPadActivity.this.playbackEngin!=null)
                    EplayerPluginPadActivity.this.playbackEngin.resumePlayback(EplayerPluginPadActivity.this.currentPlaybackTime);

            }else {

                //TODO: MESSAGE_CHANGE_LIVE_STATUS
                LiveRoomInfoData infoData = EplayerSessionInfo.sharedSessionInfo().infoData;

                if (infoData!=null&&!infoData.playMusic) {
                    handler.sendEmptyMessage(TaskType.MESSAGE_CHANGE_LIVE_STATUS);
                }
            }
        }else{

            if (EplayerSessionInfo.sharedSessionInfo()!=null&&EplayerSessionInfo.sharedSessionInfo().infoData!=null&&EplayerSessionInfo.sharedSessionInfo().infoData.liveStatus == LiveRoomLiveStatus.LiveRoomLiveStatusPlay
                    && EplayerSessionInfo.sharedSessionInfo().infoData.isStreamPush)  {

                requestStop();

                createAlertDialog("提示","您的网络太差啦，无法稳定的播放音视频，请切换到稳定网络观看");

            }

        }

    }

    @Override
    public  void handleLoadOutTime(boolean isEnd){
        if(!isEnd){
            LiveRoomInfoData infoData= EplayerSessionInfo.sharedSessionInfo().infoData;

            if (infoData!=null&&!infoData.playMusic) {
                handler.sendEmptyMessage(TaskType.MESSAGE_CHANGE_LIVE_STATUS);
            }

        }else{
            requestStop();

            createAlertDialog("提示","您的网络太差啦，无法稳定的播放音视频，请切换到稳定网络观看");

        }

    }



    View my_videoview_onclick;

    boolean isValidChangeScreen=true;
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
                case TaskType.MESSAGE_RESERT_CURRENT_PPT:{

                    if (userChangeImage&&gridview!=null) {
                        try {
                            userChangeImage = false;
                            handler.removeMessages(TaskType.MESSAGE_RESERT_CURRENT_PPT);
//                            pptGridAdapter.notifyDataSetChanged();

//                            gridview.requestFocusFromTouch();
                            pptGridAdapter.itemSelect(pptGridAdapter.getCurrentPostion());
                            gridview.setSelection(pptGridAdapter.getCurrentPostion() - 1);

                            drawPadView.resertShowImageViewBg();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    break;
                }

                case TaskType.MESSAGE_CHECK_LI_BOTTOM_SPEAK_ISSHOWING: {
                    if(null!=chatView){
                        chatView.setVisibility(View.VISIBLE);
                    }
                    handler.removeMessages(TaskType.MESSAGE_HIDE_LI_BOTTOM_SPEAK);
                    handler.sendEmptyMessageDelayed(TaskType.MESSAGE_HIDE_LI_BOTTOM_SPEAK, HIDE_TIME);

                    break;
                }
                case TaskType.MESSAGE_HIDE_LI_BOTTOM_SPEAK: {
                    handler.removeMessages(TaskType.MESSAGE_CHECK_LI_BOTTOM_SPEAK_ISSHOWING);
                    handler.removeMessages(TaskType.MESSAGE_HIDE_LI_BOTTOM_SPEAK);
                    if(null!=chatView){
                        chatView.hideChat();
                    }
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
                    //TODO
                    rl_content_all.setVisibility(View.VISIBLE);


                    if(EplayerSetting.isPlayback){
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
                        if(EPlaybackSessionInfo.sharedSessionInfo().onlyMedia){

                            rl_content_all.removeView(li_drawpaddview);
                            li_drawpaddview = null;

                            rl_content_all.removeView(hs_gridview);
                            hs_gridview = null;


                            fl_myvideoview.clearVideoviewOnclick();

                            int pad_space_margin = (int) res.getDimension(R.dimen.pad_space_margin);
                            int temp_width = rl_content_all.getWidth();
                            int temp_height = rl_content_all.getHeight() - pad_space_margin - playerControllerView.getHeight();
                            fl_myvideoview.resetSize(temp_width, temp_height, 0);

                        }else{
                            li_teacher_area.setVisibility(View.VISIBLE);

                            if(null!=infoData){
                                List<TeacherInfo> teacherList= infoData.teacherList;
                                if(null!=teacherList&&teacherList.size()>0){
                                    TeacherInfo teacherInfo= teacherList.get(0);
                                    tv_teacher_name.setText(teacherInfo.name);

                                    String ymd=DateUtil.getMD(EPlaybackSessionInfo.sharedSessionInfo().playbackBeginTime);

                                    String sd=DateUtil.getHms(EPlaybackSessionInfo.sharedSessionInfo().playbackBeginTime);
                                    String ed=DateUtil.getHms(EPlaybackSessionInfo.sharedSessionInfo().playbackEndTime);

                                    tv_teacher_time.setText(ymd+" "+sd+"-"+ed);

                                    PicUrl picUrl=new PicUrl(teacherInfo.headImg);
                                    ImageLoader.load(picUrl,img_teacher_area_title,EplayerPluginPadActivity.this,null);
                                }

                            }

                        }

                        {
                            long totalPlaybackTime =  EPlaybackSessionInfo.sharedSessionInfo().totalPlaybackTime;
                            long playbackBeginTime =  EPlaybackSessionInfo.sharedSessionInfo().playbackBeginTime;
                            long playbackEndTime =  EPlaybackSessionInfo.sharedSessionInfo().playbackEndTime;
                            playerControllerView.showProgressTime(0,totalPlaybackTime,playbackBeginTime,playbackBeginTime,playbackEndTime);
                        }

                        if(playbackEngin!=null)
                            playbackEngin.startPlayback();

                        if(drawPadView!=null)
                            drawPadView.startEventListener(DeviceUtil.DEVICE_TYPE_PAD);





                    }else{
                        if(drawPadView!=null)
                            drawPadView.startEventListener(DeviceUtil.DEVICE_TYPE_PAD);
                    }
                    hideLoading();
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
                                //  ttu.clearTask(TimeTaskUtils.TimeTaskType.TIMETASK_LOAD);

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
                        //  ttu.clearTask(TimeTaskUtils.TimeTaskType.TIMETASK_LOAD);
                        EplayerPluginPadActivity.this.playerStartPlay= false;
                        fl_myvideoview.stopPlayback();
                    }
                    LogUtil.d(TAG,"data.getPlayUrl():"+data.getPlayUrl());
                    fl_myvideoview.setVideoURI(data);
                    if(playerControllerView!=null){
                        playerControllerView.setEnabled(false);
                    }
                    fl_myvideoview.resetVideoSize();
                    fl_myvideoview.start();
//                    }
                    break;
                }

                case TaskType.MESSAGE_STOP_MUSIC_ERROR:{
                    if (fl_myvideoview.isPlayMuiceState()) {
                        ttu.clearTask(TimeTaskUtils.TimeTaskType.TIMETASK_LOAD);
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
                        ttu.clearTask(TimeTaskUtils.TimeTaskType.TIMETASK_LOAD);

                        fl_myvideoview.stopPlayback();
                    }
                    fl_myvideoview.startRotateChanpian();
                    fl_myvideoview.notIgronVideo(false);
                    fl_myvideoview.setMusicURI((String) msg.obj);
                    if(playerControllerView!=null){
                        playerControllerView.setEnabled(false);
                    }
                    fl_myvideoview.resetVideoSize();
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
                        new PlaybackLoadingThread(EplayerPluginPadActivity.this.playerData.liveClassroomId,EplayerPluginPadActivity.this.playerData.playbackid).start();
                    }else {
                        EplayerSocket.init();
                        initLiveRoomInfo();
                    }
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
      //  close();
        EplayerPluginPadActivity.this.requestStop();
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
        if (screenstate == MyVideoView.SCREENSTATE.NORMAL) {
            changeViewSizeByState(MyVideoView.SCREENSTATE.FULLSCREEN_VIDEOVIEW);
        } else if (screenstate == MyVideoView.SCREENSTATE.FULLSCREEN_VIDEOVIEW) {
            changeViewSizeByState(MyVideoView.SCREENSTATE.NORMAL);
        }
        if(playerControllerView!=null) {
            playerControllerView.setVisibility(View.VISIBLE);
        }
        handler.sendEmptyMessageDelayed(TaskType.MESSAGE_CHANGESCREEN_SCUESS,500);
        LogUtil.d("---test1","changedVideoViewScreen is end");
    }

    public void changedDrawPadViewScreen() {
        if(!isValidChangeScreen){
            return;
        }else{
            isValidChangeScreen=false;
        }
        LogUtil.d("---test1","changedDrawPadViewScreen is start");

        if (screenstate == MyVideoView.SCREENSTATE.NORMAL) {
            changeViewSizeByState(MyVideoView.SCREENSTATE.FULLSCREEN_DRAWPADVIEW);
        } else if (screenstate == MyVideoView.SCREENSTATE.FULLSCREEN_DRAWPADVIEW) {
            changeViewSizeByState(MyVideoView.SCREENSTATE.NORMAL);
        }
        if(playerControllerView!=null) {
            playerControllerView.setVisibility(View.VISIBLE);
        }
        handler.sendEmptyMessageDelayed(TaskType.MESSAGE_CHANGESCREEN_SCUESS,500);

        LogUtil.d("---test1","changedDrawPadViewScreen is end");

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //横屏时按返回键，退回到竖屏模式
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            switch (screenstate) {
                case FULLSCREEN_DRAWPADVIEW: {
                    changedDrawPadViewScreen();
                    return true;
                }
                case FULLSCREEN_VIDEOVIEW: {
                    changedVideoViewScreen();
                    return true;
                }
            }
            LiveRoomInfoData data=EplayerSessionInfo.sharedSessionInfo().infoData;
            if(null!=data&&!data.canSplice&&playbackLoadingFlag&&EplayerSetting.isPlayback){
                ToastUtil.showStringToast(context,"回看视频正在缓冲中，请稍候退出");
                return true;
            }

            if ((System.currentTimeMillis() - exitTime) > 2500) // System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                ToastUtil.showStringToast(context, "再按一次返回键退出直播间");
                exitTime = System.currentTimeMillis();
            } else {
                finish();

            }
            return true;
        }
        return super.onKeyDown (keyCode, event);
    }
    AlertDialog loadingFailedDialog=null;

//
//    private SeekBar.OnSeekBarChangeListener seekBarChange = new SeekBar.OnSeekBarChangeListener() {
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            com.soooner.source.common.util.LogUtil.d("-----onStopTrackingTouch:"+seekBar.getProgress());
//            playbackEngin.resumePlayback(seekBar.getProgress());
//            EplayerPluginPadActivity.this.manualPausePlayer = false;
//        }
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//            com.soooner.source.common.util.LogUtil.d("-----onStartTrackingTouch:"+seekBar.getProgress());
//            EplayerPluginPadActivity.this.playerStartPlay= false;
//            my_videoview.stopPlayback();
//            playbackEngin.pausePlayback();
//
//        }
//
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress,
//                                      boolean fromUser) {
//
//
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.eplayer_pad_activity);
        initView();

        bus = EventBus.getDefault();
    }

    private void initView(){
        context = this;
        shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        LogUtil.d(TAG,"onCreate is running");

//        AudioManager audioManager = (AudioManager)context.getSystemService(Activity.AUDIO_SERVICE);// 获取音量服务
//        int maxSound = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 获取系统音量最大值
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxSound, 0); //tempVolume:音量绝对值

//        //获取状态栏高度
//        Rect frame = new Rect();
//        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//         statusBarHeight = frame.top;
        chatView= (MyChatView) findViewById(R.id.mychatview);

        voteControllerView= (VoteControllerView) findViewById(R.id.voteControllerView);
        voteControllerView.init(this,DeviceUtil.DEVICE_TYPE_PAD);
        EplayerSetting.setContext( this.getApplicationContext());

        li_teacher_area= (LinearLayout) findViewById(R.id.li_teacher_area);
        li_line_line_num = (LinearLayout) findViewById(R.id.li_line_line_num);
        li_chat_area = (LinearLayout) findViewById(R.id.li_chat_area);
        li_chat_title = (LinearLayout) findViewById(R.id.li_chat_title);
        view_barrier=findViewById(R.id.view_barrier);

        tv_teacher_name = (TextView) findViewById(R.id.tv_teacher_name);
        tv_teacher_time = (TextView) findViewById(R.id.tv_teacher_time);
        img_teacher_area_title = (ImageView) findViewById(R.id.img_teacher_area_title);
        li_teacher_area_title = (LinearLayout) findViewById(R.id.li_teacher_area_title);

        fl_myvideoview= (MyVideoView) findViewById(R.id.fl_myvideoview);
        fl_myvideoview.init(this,DeviceUtil.DEVICE_TYPE_PAD,videoViewListener);

        fl_all= (RelativeLayout) findViewById(R.id.fl_all);
        drawPadView = (DrawPadView) findViewById(R.id.draw_Pad_View);

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
                    EplayerPluginPadActivity.this.requestStop();
                    createAlertDialog("提示","您的网络太糟糕，无法加载图片，请重试或者切换到稳定网络！");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDownLoadAllPPT(final DrawPadInfo drawPadInfo) {
                if(pptGridAdapter!=null) {
                    EplayerPluginPadActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pptGridAdapter.fillData(drawPadInfo);
                        }
                    });
                }else{
                    LogUtil.d("--------onDownLoadAllPPT-----");
                }

            }

            @Override
            public void onResertCurrentppt() {
                if (userChangeImage) {
                    handler.removeMessages(TaskType.MESSAGE_RESERT_CURRENT_PPT);
                    handler.sendEmptyMessage(TaskType.MESSAGE_RESERT_CURRENT_PPT);

                }

            }

        });
        Bitmap defaulteBitmap = ImageUtil.readLocalBitMap(EplayerPluginPadActivity.this.getApplicationContext(), R.drawable.ppt_bg_bg);
        drawPadView.setDefaulteBitmap(defaulteBitmap);
        drawPadView.setWhiteBoardSwithListener(new DrawPadView.WhiteBoardSwithListener() {

            @Override
            public Bitmap whiteBoardBitmap(DrawPadColorType colorType) {

                if (colorType == DrawPadColorType.DrawPadColorTypeBlack) {
                    return ImageUtil.readLocalBitMap(EplayerPluginPadActivity.this.getApplicationContext(), R.drawable.blank_bg_black);
                } else if (colorType == DrawPadColorType.DrawPadColorTypeGreen) {
                    return ImageUtil.readLocalBitMap(EplayerPluginPadActivity.this.getApplicationContext(), R.drawable.blank_bg_green);
                } else if (colorType == DrawPadColorType.DrawPadColorTypeWhite) {
                    return ImageUtil.readLocalBitMap(EplayerPluginPadActivity.this.getApplicationContext(), R.drawable.blank_bg_white);
                }

                return ImageUtil.readLocalBitMap(EplayerPluginPadActivity.this.getApplicationContext(), R.drawable.blank_bg_green);

            }
        });



        listview= (ListView) findViewById(R.id.listview);
        top_title_title= (TextView) findViewById(R.id.top_title_title);

        top_title_left= (TextView) findViewById(R.id.top_title_left);
        li_top_title_left= (LinearLayout) findViewById(R.id.li_top_title_left);

        li_top_title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playbackLoadingFlag&&EplayerSetting.isPlayback){
                    ToastUtil.showStringToast(context,"播放器正在缓冲中，请稍候退出");
                    return ;
                }
                finish();
            }
        });



        tv_middle_state=(TextView) findViewById(R.id.tv_middle_state);
        tv_online_num= (TextView) findViewById(R.id.tv_online_num);



        rl_top_title= (RelativeLayout) findViewById(R.id.rl_top_title);



        li_load= (LinearLayout) findViewById(R.id.li_load);
        li_load.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //什么也不做，只是让后面的控件不响应点击事件
            }
        });
        li_load_myprogressbar= (MyProgressBar) findViewById(R.id.li_load_myprogressbar);
        li_load_myprogressbar.init(MyProgressBar.TYPE_LINEARLAYOUT_LAYOUTPARAMS);






        rl_content_all= (RelativeLayout) findViewById(R.id.rl_content_all);

        //下面给视频view、ppt设置显示的大小
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        SCREEN_WIDTH=metrics.widthPixels;
        SCREEN_HEIGHT=metrics.heightPixels;
        BASE_SCREEN_WIDTH_SCALE=(double)SCREEN_WIDTH/BASE_SCREEN_WIDTH;
        BASE_SCREEN_HEIGHT_SCALE=(double)SCREEN_HEIGHT/BASE_SCREEN_HEIGHT;

        fl_myvideoview.BASE_SCREEN_WIDTH_SCALE=BASE_SCREEN_WIDTH_SCALE;
        fl_myvideoview.BASE_SCREEN_HEIGHT_SCALE=BASE_SCREEN_HEIGHT_SCALE;

        chatView.init(this,DeviceUtil.DEVICE_TYPE_PAD,BASE_SCREEN_WIDTH_SCALE,BASE_SCREEN_HEIGHT_SCALE);

        res = getResources();

        rl_top_title= (RelativeLayout) findViewById(R.id.rl_top_title);


        //初始化基础不变的组件大小
        rl_top_title= (RelativeLayout) findViewById(R.id.rl_top_title);
        int  pad_top_title_height= (int)res.getDimension(R.dimen.pad_top_title_height);
        LinearLayout.LayoutParams rl_top_titleLayoutParams  = (LinearLayout.LayoutParams) rl_top_title.getLayoutParams();
        rl_top_titleLayoutParams.height=(int)(pad_top_title_height*BASE_SCREEN_HEIGHT_SCALE);




        int pad_tv_top_title_left_width= (int) res.getDimension(R.dimen.pad_tv_top_title_left_width);
        int pad_tv_top_title_left_height= (int) res.getDimension(R.dimen.pad_tv_top_title_left_height);
        LinearLayout.LayoutParams  top_title_leftLayoutParams= (LinearLayout.LayoutParams) top_title_left.getLayoutParams();
        top_title_leftLayoutParams.width=(int)(pad_tv_top_title_left_width*BASE_SCREEN_WIDTH_SCALE);
        top_title_leftLayoutParams.height=(int)(pad_tv_top_title_left_height*BASE_SCREEN_WIDTH_SCALE);




        EventBus.getDefault().register(this);

        Bundle bd = getIntent().getExtras();

        if(bd.containsKey(EPLAY_DATA)){

            playerData = (EPlayerData) bd.getSerializable(EPLAY_DATA);

        }else {

            playerData =  new    EPlayerData();
        }

        playerControllerView= (PlayerControllerView) findViewById(R.id.playerControllerView);

        //TODO 回看判断
        if (EplayerSetting.isPlayback){
//            EplayerSetting.isPlayback = true;

            playbackEngin =  new PlaybackEngin();
            playbackEngin.setListener(this);

            fl_all.removeView(chatView);
            chatView = null;
            rl_content_all.removeView(li_line_line_num);
            li_line_line_num = null;

            rl_content_all.removeView(li_chat_area);
            li_chat_area = null;

        }else{
//            EplayerSetting.isPlayback = false;
            fl_all.removeView(playerControllerView);
            playerControllerView = null;

            adapter=new SpeakAdapter(this);
            listview.setAdapter(adapter);
            listview.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:{
                            enable_refresh_listview=false;
                            if(chatView.getVisibility()!=View.VISIBLE){
                                handler.sendEmptyMessage(TaskType.MESSAGE_CHECK_LI_BOTTOM_SPEAK_ISSHOWING);
                            }else{
                                handler.sendEmptyMessage(TaskType.MESSAGE_HIDE_LI_BOTTOM_SPEAK);
                            }

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



//                if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                    sv_content_all.requestDisallowInterceptTouchEvent(true);
//                }

                    return false;
                }
            });
        }


        all_activity_layout= (LinearLayout) findViewById(R.id.all_activity_layout);

        hs_gridview = (HorizontalScrollView) findViewById(R.id.hs_gridview);
        gridview= (LineGridView) findViewById(R.id.gridview);




        if (EplayerSetting.isPlayback&&!isDeafultRatio()) {
            rl_content_all.removeView(hs_gridview);
            hs_gridview = null;
            gridview = null;
        }else{

            pptGridAdapter = new PPTGridAdapter(this, 0, gridview,BASE_SCREEN_WIDTH_SCALE);
            gridview.setAdapter(pptGridAdapter);
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LogUtil.d(TAG, "onItemClick onclick" + position);

                    pptGridAdapter.itemClick(position);

                }
            });
        }


        changeViewSizeByState(MyVideoView.SCREENSTATE.NORMAL);


        DeviceUtil.getUserAgentString();


        if (!EplayerSetting.isPlayback) {

        } else {
            playerControllerView.setVisibility(View.INVISIBLE);
            playerControllerView.init(this, DeviceUtil.DEVICE_TYPE_PAD, new PlayerControllerView.MyControllerListener() {
                @Override
                public void previousPlaybackPPT() {

                    DrawPadInfo  drawPadInfo =  EplayerSessionInfo.sharedSessionInfo().drawPadInfo;
                    if(drawPadInfo==null)
                        return;
                    int page = playbackEngin.prePagePPtId(drawPadInfo.pptId,drawPadInfo.page);
                    if(page>=0){
                        EplayerPluginPadActivity.this.playerStartPlay= false;
                        fl_myvideoview.stopPlayback();
                        playbackEngin.pausePlayback();
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
                        EplayerPluginPadActivity.this.playerStartPlay= false;
                        fl_myvideoview.stopPlayback();
                        playbackEngin.pausePlayback();
                        playbackEngin.resetPlaybackPPtId(drawPadInfo.pptId,page);
                    }
                }

                @Override
                public void pausePlayback() {
                    if (playbackEngin.isPlayback()){
                        EplayerPluginPadActivity.this.playerStartPlay= false;
                        fl_myvideoview.pause();
                        playbackEngin.pausePlayback();

                        playerControllerView.changePlayerState(PlayerControllerView.PlayerState.PLAYERSTATE_PLAY);

                        tv_top_state.setText("播放暂停");

                        fl_myvideoview.init_li_playstate();

                        fl_myvideoview.showStateView(true);

                    }
                }

                @Override
                public void resumePlayback() {
                    if (!playbackEngin.isPlayback()){
                        fl_myvideoview.showStateView(false);

                        fl_myvideoview.start();
                        playbackEngin.resumePlayback();
                        tv_top_state.setText("");
                        playerControllerView.changePlayerState(PlayerControllerView.PlayerState.PLAYERSTATE_PAUSE);
                    }
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    com.soooner.source.common.util.LogUtil.d("-----onStartTrackingTouch:"+seekBar.getProgress());
                    EplayerPluginPadActivity.this.playerStartPlay= false;
                    fl_myvideoview.stopPlayback();
                    playbackEngin.pausePlayback();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    com.soooner.source.common.util.LogUtil.d("-----onStopTrackingTouch:"+seekBar.getProgress());
                    playbackEngin.resumePlayback(seekBar.getProgress());
                    fl_myvideoview.showVideoviewProgressbar();
                }
            });
            playerControllerView.setEnabled(false);
        }

        ShowLoading();


        if (StringUtils.isValid(playerData.liveClassroomId)
                && StringUtils.isValid(playerData.customer)){

            checkupPlayerDataValidateStr(playerData);



            ttu.addTask(TimeTaskUtils.TimeTaskType.TIMETASK_LOGIN,1,100000,true);

            new GetLiveListThread(playerData).start();


        }else{

            ToastUtil.showToast(context, R.string.liveClassroomId_null);
            EplayerSessionInfo.releaseALL();
            finish();
        }

    }



    //是3:4比例屏幕的pad
    public boolean isDeafultRatio() {
        float ratio = DeviceUtil.getDeviceWidthHeightRatio(this.getApplicationContext());
        return ratio >0.7f;
    }
    /*
      根据状态改变各视图的位置
     */
    private void changeViewSizeByState(MyVideoView.SCREENSTATE screenstate) {
        this.screenstate=screenstate;

        switch (screenstate){
            case NORMAL:{
                view_barrier.setVisibility(View.INVISIBLE);
                rl_top_title.setVisibility(View.VISIBLE);

                PAD_SPACE_MARGIN= (int) (res.getDimension(R.dimen.pad_space_margin)*BASE_SCREEN_HEIGHT_SCALE);
                LinearLayout.LayoutParams sclp= (LinearLayout.LayoutParams) rl_content_all.getLayoutParams();
                sclp.setMargins(PAD_SPACE_MARGIN,PAD_SPACE_MARGIN,PAD_SPACE_MARGIN,0);

                int pad_li_drawpaddview_width = (int) res.getDimension(R.dimen.pad_li_drawpaddview_width);

                int pptwidth = (int) (pad_li_drawpaddview_width * BASE_SCREEN_WIDTH_SCALE);

                RelativeLayout.LayoutParams li_drawpaddviewLayoutParams = new RelativeLayout.LayoutParams(pptwidth,(int) (pptwidth * DRAWPADVIEW_SCALE));
                li_drawpaddviewLayoutParams.addRule(RelativeLayout.ALIGN_TOP | RelativeLayout.ALIGN_LEFT);
                li_drawpaddview.setLayoutParams(li_drawpaddviewLayoutParams);


                if (drawPadView.isEventListenerIsValid()) {
                    drawPadView.initChangedScreen(li_drawpaddviewLayoutParams.width, li_drawpaddviewLayoutParams.height);
                }


                int pad_li_line_line_num_height = (int) res.getDimension(R.dimen.pad_li_line_line_num_height);






                int pad_fl_video_width = (int) res.getDimension(R.dimen.pad_fl_video_width);

                FL_VIDEO_BASE_WIDTH= (int) (pad_fl_video_width * BASE_SCREEN_WIDTH_SCALE);

                if(EplayerSetting.isPlayback){

                    if(hs_gridview !=null) {
                        RelativeLayout.LayoutParams li_gridviewLayoutParams = (RelativeLayout.LayoutParams) hs_gridview.getLayoutParams();
                        li_gridviewLayoutParams.topMargin = (int) (li_drawpaddviewLayoutParams.height + li_drawpaddviewLayoutParams.topMargin + PAD_SPACE_MARGIN);
                        li_gridviewLayoutParams.width = pptwidth + PAD_SPACE_MARGIN + FL_VIDEO_BASE_WIDTH;
                        li_gridviewLayoutParams.height=RelativeLayout.LayoutParams.MATCH_PARENT;
                        li_gridviewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                        li_gridviewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

                        ViewGroup.LayoutParams params= gridview.getLayoutParams();
                        params.width=ViewGroup.LayoutParams.MATCH_PARENT;
                        params.height=ViewGroup.LayoutParams.MATCH_PARENT;
                        gridview.setNumColumns(GRIDVIEW_PLAYBACK_NUM);
                    }


                    int pad_li_teacher_area_height= (int) res.getDimension(R.dimen.pad_li_teacher_area_height);
                    int pad_li_teacher_area_margin_top= (int) res.getDimension(R.dimen.pad_li_teacher_area_margin_top);
                    RelativeLayout.LayoutParams li_teacher_areaLayoutParams= (RelativeLayout.LayoutParams) li_teacher_area.getLayoutParams();
                    li_teacher_areaLayoutParams.width=FL_VIDEO_BASE_WIDTH;
                    li_teacher_areaLayoutParams.height= (int) (pptwidth * DRAWPADVIEW_SCALE-pad_li_teacher_area_margin_top*BASE_SCREEN_WIDTH_SCALE);
                    li_teacher_areaLayoutParams.topMargin= (int) (pad_li_teacher_area_margin_top*BASE_SCREEN_WIDTH_SCALE);

                    int pad_li_teacher_area_title_height= (int) res.getDimension(R.dimen.pad_li_teacher_area_title_height);
                    li_teacher_area_title.getLayoutParams().height= (int) (pad_li_teacher_area_title_height*BASE_SCREEN_HEIGHT_SCALE);

                    int img_teacher_area_title_height= (int) res.getDimension(R.dimen.img_teacher_area_title_height);

                    LinearLayout.LayoutParams   img_teacher_area_titleLayoutParams= (LinearLayout.LayoutParams) img_teacher_area_title.getLayoutParams();
                    img_teacher_area_titleLayoutParams.width= (int) (img_teacher_area_title_height*BASE_SCREEN_HEIGHT_SCALE);
                    img_teacher_area_titleLayoutParams.height= (int) (img_teacher_area_title_height*BASE_SCREEN_HEIGHT_SCALE);
                }else {

                    RelativeLayout.LayoutParams li_line_line_numLayoutParams = (RelativeLayout.LayoutParams) li_line_line_num.getLayoutParams();
                    li_line_line_numLayoutParams.width = pptwidth;
                    li_line_line_numLayoutParams.height = (int) (pad_li_line_line_num_height * BASE_SCREEN_HEIGHT_SCALE);
                    li_line_line_numLayoutParams.topMargin = li_drawpaddviewLayoutParams.height;
                    li_line_line_numLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    li_line_line_numLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

                    if(hs_gridview !=null) {
                        RelativeLayout.LayoutParams li_gridviewLayoutParams = (RelativeLayout.LayoutParams) hs_gridview.getLayoutParams();
                        li_gridviewLayoutParams.topMargin = (int) (li_line_line_numLayoutParams.height + li_line_line_numLayoutParams.topMargin + 10);
                        li_gridviewLayoutParams.width = pptwidth;
                        li_gridviewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                        li_gridviewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    }
                }




                    RelativeLayout.LayoutParams fl_videoLayoutParams = new RelativeLayout.LayoutParams(FL_VIDEO_BASE_WIDTH, (int) (FL_VIDEO_BASE_WIDTH * CHANGPIAN_SCALE));

                    fl_videoLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    fl_videoLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    fl_myvideoview.resetSize(fl_videoLayoutParams);


                    if(!EplayerSetting.isPlayback) {
                        RelativeLayout.LayoutParams li_chat_areaLayoutParams = new RelativeLayout.LayoutParams(fl_videoLayoutParams.width, RelativeLayout.LayoutParams.FILL_PARENT);
                        li_chat_areaLayoutParams.topMargin = fl_videoLayoutParams.height;
                        li_chat_areaLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                        li_chat_areaLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                        li_chat_area.setLayoutParams(li_chat_areaLayoutParams);
                    }




                int pad_li_chat_title_height = (int) res.getDimension(R.dimen.pad_li_chat_title_height);
                LinearLayout.LayoutParams li_chat_titleLayoutParams = (LinearLayout.LayoutParams) li_chat_title.getLayoutParams();
                li_chat_titleLayoutParams.height = (int) (pad_li_chat_title_height * BASE_SCREEN_HEIGHT_SCALE);



                fl_myvideoview.init_li_playstate();
                break;
            }

            case FULLSCREEN_DRAWPADVIEW:{
                LinearLayout.LayoutParams sclp= (LinearLayout.LayoutParams) rl_content_all.getLayoutParams();
                sclp.setMargins(0,0,0,0);


                view_barrier.setVisibility(View.VISIBLE);
                view_barrier.bringToFront();

                int sclp_width = rl_content_all.getWidth()+PAD_SPACE_MARGIN*2;
                int sclp_heiht = rl_content_all.getHeight() + rl_top_title.getHeight()+PAD_SPACE_MARGIN;
                rl_top_title.setVisibility(View.GONE);
                int max_width=0;
                int max_height=0;
                RelativeLayout.LayoutParams li_drawpaddviewLayoutParams;
                if (!EplayerSetting.isPlayback) {
                    if(null!=chatView){
                        chatView.hideChat();
                    }

                if(BASE_SCREEN_WIDTH_SCALE>BASE_SCREEN_HEIGHT_SCALE){
                    max_height=sclp_heiht;
                    max_width=(int)(sclp_heiht/DRAWPADVIEW_SCALE);
                    li_drawpaddviewLayoutParams=new RelativeLayout.LayoutParams(max_width,max_height);

                    li_drawpaddviewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

                }else{
                    max_width=sclp_width;
                    max_height=(int)(max_width*DRAWPADVIEW_SCALE);
                    li_drawpaddviewLayoutParams=new RelativeLayout.LayoutParams(max_width,max_height);
                    li_drawpaddviewLayoutParams.addRule(RelativeLayout.ALIGN_TOP | RelativeLayout.ALIGN_LEFT);

                   //todo 去调整显示聊天区

                    RelativeLayout.LayoutParams li_chat_areaLayoutParams =new RelativeLayout.LayoutParams(sclp_width,sclp_heiht-max_height);
                    li_chat_areaLayoutParams.topMargin = max_height;
                    li_chat_areaLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    li_chat_areaLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    li_chat_area.setLayoutParams(li_chat_areaLayoutParams);
                    li_chat_area.bringToFront();

                }
                li_drawpaddview.setLayoutParams(li_drawpaddviewLayoutParams);
                if (drawPadView.isEventListenerIsValid()) {
                    drawPadView.initChangedScreen(max_width, max_height);
                }
                li_drawpaddview.bringToFront();

                } else {

                    if (!isDeafultRatio()) {
                        li_drawpaddviewLayoutParams = new RelativeLayout.LayoutParams(sclp_width, sclp_heiht);
                    } else {
                        max_width = sclp_width;
                        max_height = (int) (sclp_width * DRAWPADVIEW_SCALE);
                        if (null != gridview) {

                            View tempView = gridview.getChildAt(0);
                            if(tempView!=null) {
                                int tempHeight = tempView.getHeight();
                                int tempWidth = tempView.getWidth();
                                ViewGroup.LayoutParams params = gridview.getLayoutParams();
                                params.width = tempWidth * pptGridAdapter.getCount();
                                params.height = tempHeight;
                                gridview.setLayoutParams(params);
                                gridview.setNumColumns(pptGridAdapter.getCount());

                                RelativeLayout.LayoutParams li_gridviewLayoutParams = (RelativeLayout.LayoutParams) hs_gridview.getLayoutParams();
                                li_gridviewLayoutParams.topMargin = max_height + PAD_SPACE_MARGIN;
                                li_gridviewLayoutParams.height = tempHeight;
                                li_gridviewLayoutParams.width = max_width;
                            }


                        }


                        li_drawpaddviewLayoutParams = new RelativeLayout.LayoutParams(max_width, max_height);
                    }

                    li_drawpaddviewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

                    li_drawpaddview.setLayoutParams(li_drawpaddviewLayoutParams);
                    if (drawPadView.isEventListenerIsValid()) {
                        drawPadView.initChangedScreen(max_width, max_height);
                    }
                    li_drawpaddview.bringToFront();
                    if (null != hs_gridview && isDeafultRatio()) {
                        hs_gridview.bringToFront();
                    }
                }


                break;
            }
            case FULLSCREEN_VIDEOVIEW:{
                if(null!=chatView){
                    chatView.hideChat();
                }
                LinearLayout.LayoutParams sclp= (LinearLayout.LayoutParams) rl_content_all.getLayoutParams();
                sclp.setMargins(0,0,0,0);

                view_barrier.setVisibility(View.VISIBLE);
                view_barrier.bringToFront();


                int sclp_width=SCREEN_WIDTH;
                int sclp_heiht=SCREEN_HEIGHT;//SCREEN_HEIGHT-statusBarHeight;
                rl_top_title.setVisibility(View.GONE);
                RelativeLayout.LayoutParams fl_videoLayoutParams =new RelativeLayout.LayoutParams(sclp_width,sclp_heiht);
                fl_videoLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                fl_myvideoview.resetSize(fl_videoLayoutParams);
                fl_myvideoview.bringToFront();
                fl_myvideoview.init_li_playstate();
                break;
            }
        }

    }

    //初始化直播状态
    public void initPlayState(LiveRoomLiveStatus liveStatus){
        //LiveRoomLiveStatusStop(0), LiveRoomLiveStatusPlay(1), LiveRoomLiveStatusPause(2), LiveRoomLiveStatusClose(3);
        switch (liveStatus.value()){
            case 0:{//LiveRoomLiveStatusStop
                tv_middle_state.setText("直播未开始");
                break;
            }
            case 1:{//LiveRoomLiveStatusPlay(1)
                tv_middle_state.setText("正在直播中");
                break;
            }
            case 2:{//LiveRoomLiveStatusPause(2)
                tv_middle_state.setText("直播暂停中");
                break;
            }
            case 3:{//LiveRoomLiveStatusClose(3);
                tv_middle_state.setText("直播已关闭");
                break;
            }
        }

        String hint="";
        if(EplayerSetting.isPlayback){
            if(liveStatus.value() == 1){
                hint="开始播放";

            }else if(liveStatus.value() == 3){
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

        ttu.clearTask(TimeTaskUtils.TimeTaskType.TIMETASK_LOGIN);

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

        ttu.clearTask(TimeTaskUtils.TimeTaskType.TIMETASK_LOAD);

        ttu.clearTask(TimeTaskUtils.TimeTaskType.TIMETASK_ERROR);

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
            // StorageUtil.cleanAll();//cleanCacheDir();
            if(loading!=null){
                loading.stopLoading();
            }
            handler.removeMessages(TaskType.MESSAGE_CHANGESCREEN_SCUESS);


            EplayerPluginPadActivity.this.playerStartPlay= false;

            EPlaybackSessionInfo.releaseALL();
            EplayerSessionInfo.releaseALL();


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

            ttu.clearAllTask();

            fl_myvideoview.stopPlayback();





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
        finish();

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
        android.view.ViewGroup.LayoutParams lp =text.getLayoutParams();
        text.setTextSize(18);

        text.setGravity(Gravity.CENTER);
        text.setText("\n帐号在其它位置登陆,\n您已经被迫下线!\n");
        EplayerPluginPadActivity.this.requestStop();

        AlertDialog.Builder edit_builder = new AlertDialog.Builder(context);
        edit_builder.setCancelable(false);
        edit_builder.setTitle("下线提醒");
        edit_builder.setView(text);
        edit_builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

                EplayerPluginPadActivity.this.loading = new PlaybackLoading();
                EplayerPluginPadActivity.this.loading.liveClassroomId = this.liveClassroomId;
                EplayerPluginPadActivity.this.loading.pid = this.pid;
                EplayerPluginPadActivity.this.loading.startLoading();
            }catch (Exception e){
                e.printStackTrace();
            }


            handler.sendEmptyMessage(TaskType.MESSAGE_HIDELOADING);

            ttu.clearTask(TimeTaskUtils.TimeTaskType.TIMETASK_LOGIN);


        }
    }

    private class GetLiveListThread extends Thread {
//
//        String liveClassroomId = "";
//        String username = "";
//        String userpwd = "";
//        String key_customer="";
//
//        String exStr = "";

        EPlayerData playerData;

        public GetLiveListThread(EPlayerData playerData) {
            this.playerData = playerData;
        }

//        public GetLiveListThread(String liveClassroomId, String key_customer,String username, String userpwd) {
//            this.liveClassroomId = liveClassroomId;
//            this.username = username;
//            this.userpwd = userpwd;
//            this.key_customer=key_customer;
//        }
//
//        public GetLiveListThread(String liveClassroomId, String key_customer,String exStr) {
//            this.liveClassroomId = liveClassroomId;
//            this.exStr = exStr;
//            this.key_customer=key_customer;
//        }


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

                    UserLoginProtocol protocol = new UserLoginProtocol(playerData);

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

        finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG,"onDestroy");
        EplayerSetting.isPlayback = false;

        super.onDestroy();
    }
/*
    private  void close(){
        try {
            if(loading!=null){
                loading.stopLoading();
            }
            handler.removeMessages(TaskType.MESSAGE_CHANGESCREEN_SCUESS);
            StorageUtil.cleanCacheDir();

            EventBus.getDefault().unregister(this);
            EplayerSocket.close();
            if(drawPadView!=null) {
                drawPadView.stopEventListener();
                drawPadView.releaseALL();
                EventBus.getDefault().unregister(drawPadView);
                drawPadView = null;
            }
            if(null!=my_videoview){
                EplayerPluginPadActivity.this.currentStaticTimeMillis = System.currentTimeMillis();
                if(loadingTimer!=null) {
                    loadingTimer.cancel();
                    loadingTimer = null;
                }
                my_videoview.stopPlayback();
                my_videoview = null;

            }

            EplayerPluginPadActivity.this.currentStaticLoginTimeMillis = System.currentTimeMillis();
            if (loginTimer != null) {
                loginTimer.cancel();
                loginTimer = null;
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }*/

    public void changeDrawImageViewBg(int postion,boolean reset){
        userChangeImage=true;
        int ppt_CurrentPostion= pptGridAdapter.getCurrentPostion();
        int selectPpt_tPostion =postion;
        handler.removeMessages(TaskType.MESSAGE_RESERT_CURRENT_PPT);

        DrawPadInfo _drawPadInfo = EplayerSessionInfo.sharedSessionInfo().drawPadInfo;

        DrawPadInfo drawPadInfo =_drawPadInfo.copySelf();

        if(ppt_CurrentPostion==selectPpt_tPostion){
            //todo
            drawPadInfo.page = ppt_CurrentPostion+1;

            if(reset) {
                handler.sendEmptyMessage(TaskType.MESSAGE_RESERT_CURRENT_PPT);
            }
        }else{

            if(reset){
                handler.removeMessages(TaskType.MESSAGE_RESERT_CURRENT_PPT);
                handler.sendEmptyMessageDelayed(TaskType.MESSAGE_RESERT_CURRENT_PPT,RESERT_CURRENT_PPT_DELAY);
            }


            drawPadInfo.page = selectPpt_tPostion+1;
            drawPadInfo.userSwitch = true;

        }
        bus.post(new DrawPadInfoChangeEvent(drawPadInfo));

    }



}
