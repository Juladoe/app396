package com.soooner.EplayerPluginLibary.widget;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.*;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.soooner.EplayerPluginLibary.EplayerPluginBaseActivity;
import com.soooner.EplayerPluginLibary.R;
import com.soooner.EplayerPluginLibary.util.LogUtil;
import com.soooner.EplayerPluginLibary.util.TaskType;
import com.soooner.EplayerSetting;
import com.soooner.playback.entity.PlayList;
import com.soooner.playback.entity.PlaySplice;
import com.soooner.playback.entity.PlaybackSegment;
import com.soooner.playback.entity.SpliceInfo;
import com.soooner.source.common.util.*;
import com.soooner.source.entity.SessionData.LivaRoomInfo.LiveRoomInfoData;
import com.soooner.source.entity.SessionEmun.LiveRoomStreamType;
import com.soooner.source.system.PlaySpliceLoader;
import com.soooner.ws.event.LiveRoomEvent.NextSegmentEvent;
import de.greenrobot.event.EventBus;
import org.json.JSONObject;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.widget.VideoView;

import java.io.File;
import java.util.Map;

/**
 * Created by zhaoxu2014 on 14-12-25.
 */
public class MyVideoView extends FrameLayout {
    public static final String TAG = "MyVideoView";
    public static final double IMG_MIDDLE_CHANGPIAN_SCALE = (double) 186 / 435;
    public static final double CHANGPIAN_MAGIN_BOTTOM_SCALE = (double) 50 / 435;

    public double BASE_SCREEN_WIDTH_SCALE=0;
    public double BASE_SCREEN_HEIGHT_SCALE=0;
    EplayerPluginBaseActivity activity;
    public static int DEVICE_TYPE = DeviceUtil.DEVICE_TYPE_PHONE;
    VideoViewListener listener;
    int videoViewWidth = 0;
    int vidwoViewHeight = 0;

    public  boolean is_avio_done;

    public  boolean is_avio_data;

    Animation chanpian_rotate;

    EventBus bus;

    public MyVideoView(Context context) {
        super(context);
        bus = EventBus.getDefault();
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bus = EventBus.getDefault();
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        bus = EventBus.getDefault();
    }


    public enum PalyTypeState {//播放状态
        STATE_NONE, STATE_PLAY_MUICE, STATE_PLAY_VOIDE, STATE_PLAY_AUDIO
    }

    PalyTypeState palyTypeState = PalyTypeState.STATE_NONE;


    public enum SCREENSTATE {
        NORMAL,FULLSCREEN_DRAWPADVIEW, FULLSCREEN_VIDEOVIEW
    }



    FrameLayout fl_video;
    FrameLayout fl_videoview;
    VideoView my_videoview;
    View my_videoview_onclick;
    LinearLayout rl_changpian;
    TextView img_changpian;
    TextView tv_audio;
    RelativeLayout rl_state;
    ImageView img_video_logo;
    TextView tv_top_state;
    LinearLayout li_progressbar_bg;
    MyProgressBar myprogressbar;

    IMediaPlayer.OnAvioContentPacketListener onAvioContentPacketListener;
    public boolean isPlayMuiceState() {
        if (palyTypeState == PalyTypeState.STATE_PLAY_MUICE) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isPlayState() {
        if (palyTypeState == PalyTypeState.STATE_PLAY_AUDIO || palyTypeState == PalyTypeState.STATE_PLAY_VOIDE || palyTypeState == PalyTypeState.STATE_PLAY_MUICE) {
            return true;
        } else {
            return false;
        }
    }


    public void init(EplayerPluginBaseActivity activity, int DEVICE_TYPE, VideoViewListener listener) {
        this.activity = activity;
        this.DEVICE_TYPE = DEVICE_TYPE;
        this.listener = listener;
        View view = null;
        view = View.inflate(activity, R.layout.video_view, this);
        initView(view);
         this.is_avio_data = false;
        showViewBystate(PalyTypeState.STATE_NONE);
    }

    private void initView(View view) {
        fl_videoview = (FrameLayout) view.findViewById(R.id.fl_videoview);
        fl_video = (FrameLayout) view.findViewById(R.id.fl_video);
        my_videoview = (VideoView) view.findViewById(R.id.my_videoview);
        rl_changpian = (LinearLayout) view.findViewById(R.id.rl_changpian);
        img_changpian = (TextView) view.findViewById(R.id.img_changpian);
        tv_audio = (TextView) view.findViewById(R.id.tv_audio);
        rl_state = (RelativeLayout) view.findViewById(R.id.rl_state);
        img_video_logo = (ImageView) view.findViewById(R.id.img_video_logo);
        tv_top_state = (TextView) view.findViewById(R.id.tv_top_state);
        li_progressbar_bg = (LinearLayout) view.findViewById(R.id.li_progressbar_bg);
        myprogressbar = (MyProgressBar) view.findViewById(R.id.myprogressbar);
        myprogressbar.init(MyProgressBar.TYPE_LINEARLAYOUT_LAYOUTPARAMS);
        my_videoview_onclick = view.findViewById(R.id.my_videoview_onclick);


        my_videoview_onclick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (null != listener) {
                    listener.onVideoviewClick();
                }

            }
        });

        rl_changpian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tv_audio.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onAudioViewClick();
                }
            }
        });

        my_videoview.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                LogUtil.d("my_videoview", "onCompletion is runing");
                if (null != listener) {
                    listener.onCompletion(mp);
                }
            }
        });

        my_videoview.setOnErrorListener(errorListener);
        my_videoview.setOnLoadingListener(onLoadingListener);
        my_videoview.setOnStateChangedListener(onStateChangedListener);

        palyTypeState = PalyTypeState.STATE_NONE;


        //给唱片设置动画
        chanpian_rotate = AnimationUtils.loadAnimation(getContext(), R.anim.chanpian_rotate);
        LinearInterpolator lir = new LinearInterpolator();
        chanpian_rotate.setInterpolator(lir);
    }


    //当只有视频时会去取消点击事件
    public void clearVideoviewOnclick(){
        my_videoview_onclick.setOnClickListener(null);
    }

    public void resetVideoSize(){
        if(videoViewWidth>0&&vidwoViewHeight>0){
            my_videoview.setVideoLayout(videoViewWidth, vidwoViewHeight);
        }

    }

    public void resetSize(int width, int height, int magin) {
        if(width<=0||height<=0){
            return;
        }
        videoViewWidth = width;
        vidwoViewHeight = height;

         switch (DEVICE_TYPE){
             case DeviceUtil.DEVICE_TYPE_PHONE:{
                 LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(videoViewWidth, vidwoViewHeight);
                 lp.setMargins(magin, magin, magin, magin);
                 this.setLayoutParams(lp);
                 break;
             }
             case DeviceUtil.DEVICE_TYPE_PAD:{
                 RelativeLayout.LayoutParams sclp=  (RelativeLayout.LayoutParams)this.getLayoutParams();

                 sclp.width = width;
                 sclp.height = height;
                 break;
             }
         }


        my_videoview.setVideoLayout(videoViewWidth, vidwoViewHeight);


        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams((int) (vidwoViewHeight * IMG_MIDDLE_CHANGPIAN_SCALE), (int) (vidwoViewHeight * IMG_MIDDLE_CHANGPIAN_SCALE));
        lp2.width = (int) (vidwoViewHeight * IMG_MIDDLE_CHANGPIAN_SCALE);
        lp2.height = (int) (vidwoViewHeight * IMG_MIDDLE_CHANGPIAN_SCALE);
        lp2.setMargins(0, 0, 0, (int) (vidwoViewHeight * CHANGPIAN_MAGIN_BOTTOM_SCALE));
        lp2.gravity = Gravity.BOTTOM;
        img_changpian.setLayoutParams(lp2);

    }

    public void resetSize(RelativeLayout.LayoutParams lp) {
       this.setLayoutParams(lp);
        ((VideoView) my_videoview).setVideoLayout(lp.width, lp.height);

        LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) img_changpian.getLayoutParams();
        lp2.width = (int) (lp.height * IMG_MIDDLE_CHANGPIAN_SCALE);
        lp2.height = (int) (lp.height * IMG_MIDDLE_CHANGPIAN_SCALE);
        lp2.setMargins(0, 0, 0, (int) (lp.height * CHANGPIAN_MAGIN_BOTTOM_SCALE));
        lp2.gravity = Gravity.BOTTOM;
    }


    public void init_li_playstate() {
        ViewGroup.LayoutParams lp = img_video_logo.getLayoutParams();
        int img_video_logo_width= (int) ((int)this.getResources().getDimension(R.dimen.img_video_logo_width)*BASE_SCREEN_WIDTH_SCALE);
        int img_video_logo_height= (int) ((int) this.getResources().getDimension(R.dimen.img_video_logo_height)*BASE_SCREEN_WIDTH_SCALE);
        int title1_textsize = 20;
        switch (activity.screenstate){
            case  NORMAL:{
                lp.width=img_video_logo_width;
                lp.height=img_video_logo_height;
                tv_top_state.setTextSize(title1_textsize);
                break;
            }
            default:{
                lp.width=img_video_logo_width*2;
                lp.height=img_video_logo_height*2;
                tv_top_state.setTextSize(title1_textsize*2);
                break;
            }
        }

    }

    /*
      通过当前状态，展示相应的view
     */
    public boolean showViewBystate(PalyTypeState typeState) {
        if (palyTypeState == typeState) {
            return false;
        } else {
            switch (typeState) {
                case STATE_NONE: {
                    if (activity.screenstate==SCREENSTATE.FULLSCREEN_VIDEOVIEW) {
                        listener.onChangedVideoViewScreen();
                    }
                    fl_videoview.setVisibility(View.INVISIBLE);
                    rl_changpian.setVisibility(View.INVISIBLE);
                    tv_audio.setVisibility(View.INVISIBLE);
                    rl_state.setVisibility(View.VISIBLE);
                    li_progressbar_bg.setVisibility(View.INVISIBLE);
                    break;
                }
                case STATE_PLAY_AUDIO: {
                    fl_videoview.setVisibility(View.INVISIBLE);
                    rl_changpian.setVisibility(View.INVISIBLE);
                    tv_audio.setVisibility(View.VISIBLE);
                    rl_state.setVisibility(View.INVISIBLE);
                    showVideoviewProgressbar();
                    break;
                }
                case STATE_PLAY_VOIDE: {

                    fl_videoview.setVisibility(View.VISIBLE);
                    rl_changpian.setVisibility(View.INVISIBLE);
                    tv_audio.setVisibility(View.INVISIBLE);
                    rl_state.setVisibility(View.INVISIBLE);
                    showVideoviewProgressbar();

                    break;
                }
                case STATE_PLAY_MUICE: {

                    fl_videoview.setVisibility(View.VISIBLE);
                    rl_changpian.setVisibility(View.VISIBLE);
                    tv_audio.setVisibility(View.INVISIBLE);
                    rl_state.setVisibility(View.INVISIBLE);
                    li_progressbar_bg.setVisibility(View.INVISIBLE);
                    break;
                }
            }
        }
        palyTypeState = typeState;
        return true;
    }

    /*
     改变提示信息
    */
    public void changeHintText(String hint) {
        tv_top_state.setText(hint);
    }

    public void showStateView(boolean isShow) {
        if (isShow) {
            rl_state.setVisibility(View.VISIBLE);
        } else {
            rl_state.setVisibility(View.INVISIBLE);
        }

    }

    public void pause() {
        my_videoview.pause();
    }

    public void start() {
        my_videoview.start();
    }

    public void stopPlayback() {
        try {
            handler.removeMessages(MESSAGE_LOADOUTTIME);
            if (null != my_videoview) {
                this.is_avio_data = false;
                PlaySpliceLoader.close();
                my_videoview.stopPlayback();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isPlaying() {
        return my_videoview.isPlaying();
    }

    public boolean isPaused() {
        return my_videoview.isPaused();
    }



    public void notIgronVideo(boolean _notIgronVideo) {
        my_videoview.notIgronVideo(_notIgronVideo);
    }

    public void setVideoURI(final LiveRoomInfoData data) {

        if(null==data){
            return;
        }
        if(!data.canSplice){
            this.is_avio_data = false;
            my_videoview.setVideoURI(Uri.parse(data.getPlayUrl()));
        }else{
            //todo
            this.is_avio_data = true;
            this.is_avio_done = false;
            try {

                final   PlaybackSegment currentPlaybackSegment= data.currentPlaybackSegment;
                final PlayList info=currentPlaybackSegment.info;
                if(info.isexists()){
                    initPlayBackVideoView(info.type,currentPlaybackSegment.endTime,currentPlaybackSegment.startTime-currentPlaybackSegment.allSegmentStartTime,currentPlaybackSegment.seq,data.getPlayUrl(),info.getDownLocationPath(),info.endfiletime,info.getSuffix());
                }else{
                    String downUrl=  EplayerSetting.spliceVideoPlayBaseUrl+ info.getSuffix()+"/"+info.getDownSpliceUrl()+"/index.list";
                    MyHttpUtils.getHttpUtils().download(downUrl,info.getDownLocationPath(),new RequestCallBack<File>(){

                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            initPlayBackVideoView(info.type,currentPlaybackSegment.endTime,currentPlaybackSegment.startTime-currentPlaybackSegment.allSegmentStartTime,currentPlaybackSegment.seq,data.getPlayUrl(),responseInfo.result.getPath(),info.endfiletime,info.getSuffix());
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {

                        }
                    });
                }


            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    /*
    allseq //有可能当段视频是由一整个视频切成多个的，此处记录下当前片段在整个视频的偏移量
     */
    public void initPlayBackVideoView(LiveRoomStreamType type,long endTime,long allseq,long seq,String palyUrl,String path,String endfiletime,String suffix) {
        new ParseThread(type,endTime, allseq, seq,palyUrl, path, endfiletime, suffix).start();
    }


    public class ParseThread extends Thread{
        LiveRoomStreamType type;
        long endTime;
        long allseq;
        long seq;
        String palyUrl;
        String path;
        String endfiletime;
        String suffix;

        public ParseThread(LiveRoomStreamType type,long endTime,long allseq,long seq,String palyUrl,String path,String endfiletime,String suffix){
            this.type=type;
            this.endTime=endTime;
            this.allseq=allseq;
            this.seq=seq;
            this.palyUrl=palyUrl;
            this.path=path;
            this.endfiletime=endfiletime;
            this.suffix=suffix;
        }

        @Override
        public void run() {
            try {
                JSONObject jsonObject = JSONUtils.getJSONFromFile(path);
                SpliceInfo spliceInfo = SpliceInfo.fromJson(jsonObject,suffix,endfiletime,endTime,allseq+seq, type);
                spliceInfo.palyurl=palyUrl;
                Message message=Message.obtain();
                message.what= TaskType.MESSAGE_PARSETHREAD;
                message.obj=spliceInfo;
                handler.sendMessage(message);
            }catch (Exception e){
                e.printStackTrace();
            }

            super.run();
        }
    }


    public static final int MESSAGE_LOADOUTTIME= 2000127;
    public static final int LOAD_OUT_TIME=20*1000;
    private int playIndex=-1;
    private boolean streamEOF=false;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_LOADOUTTIME:{
                    streamEOF=true;
                    break;
                }
                case TaskType.MESSAGE_PARSETHREAD: {
                    try {
                        final SpliceInfo spliceInfo= (SpliceInfo) msg.obj;
                        PlaySpliceLoader.load(spliceInfo);

                        if(spliceInfo.playSplicelist.size()<=1){
                            bus.post(new NextSegmentEvent());
                            return;
                        }

                        my_videoview.setMediaPlayContextPacket(true);
                        my_videoview.setOnAvioContentPacketListener(new IMediaPlayer.OnAvioContentPacketListener() {

                            @Override
                            public int getAvioCount(IMediaPlayer mp) {
                                return spliceInfo.playSplicelist.size();
                            }

                            @Override
                            public boolean getAvioStreamEOF(IMediaPlayer mp) {
                                LogUtil.d("streamEOF","getAvioStreamEOF streamEOF:"+streamEOF);
                                return streamEOF;
                            }

                            @Override
                            public String getAvioPath(IMediaPlayer mp, int currentIndex) {
                                //已经播放到最后了，要停止播放
                                if(currentIndex>=spliceInfo.playSplicelist.size()){
                                   MyVideoView.this.is_avio_done = true;
                                    return null;
                                }
                                String key=spliceInfo.endfiletime+"_"+currentIndex;
                                String spliceLocation= PlaySpliceLoader.getPlaySpliceLocation(""+key);

                                if(null!=spliceLocation){
                                    handler.removeMessages(MESSAGE_LOADOUTTIME);
                                    playIndex=-1;
                                //    LogUtil.d("streamEOF","removeMessages");
                                }else{
                                    //如果已经全部下载完成，并缓冲区已经没有数据了，认为播放结束
                                    if(PlaySpliceLoader.isDownEnd&&null!=PlaySpliceLoader.playSpliceMap&&PlaySpliceLoader.playSpliceMap.size()==0){
                                        handler.removeMessages(MESSAGE_LOADOUTTIME);
                                   //     LogUtil.d("streamEOF","end removeMessages");
                                    }else{
                                        if (playIndex!=currentIndex){
                                      //      LogUtil.d("streamEOF","add Messages playIndex:"+playIndex+" currentIndex: "+currentIndex);
                                            playIndex=currentIndex;
                                            handler.removeMessages(MESSAGE_LOADOUTTIME);
                                            handler.sendEmptyMessageDelayed(MESSAGE_LOADOUTTIME,LOAD_OUT_TIME);

                                        }
                                    }

                                }

                                return spliceLocation;
                            }
                        });
                        my_videoview.setVideoURI(Uri.parse(spliceInfo.palyurl));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                }
            }
            super.handleMessage(msg);
        }
    };



    public void setMusicURI(String url) {
        my_videoview.setVideoURI(Uri.parse(url));
    }


    public void hideVideoviewProgressbar() {
        if (null != li_progressbar_bg && li_progressbar_bg.getVisibility() == View.VISIBLE) {
            li_progressbar_bg.setVisibility(View.INVISIBLE);
        }
    }

    public void showVideoviewProgressbar() {
        if (null != li_progressbar_bg && li_progressbar_bg.getVisibility() != View.VISIBLE) {
            li_progressbar_bg.setVisibility(View.VISIBLE);
        }
    }

    public void setChangeingSize(boolean isChangeingSize) {
        my_videoview.setChangeingSize(isChangeingSize);
    }

    public void startRotateChanpian() {

        showViewBystate(PalyTypeState.STATE_PLAY_MUICE);
        img_changpian.startAnimation(chanpian_rotate);

    }

    public void stopRotateChanpian() {
        img_changpian.clearAnimation();
        showViewBystate(PalyTypeState.STATE_NONE);

    }

    public boolean playDoneIfHasMedia() {

        if(!this.is_avio_data)
            return my_videoview.isFinished();
        return this.is_avio_done;

    }


    public long playTimeIfHasMedia() {
            return my_videoview.getCurrentPosition();

    }


    IMediaPlayer.OnErrorListener errorListener = new IMediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            if (null != listener) {
                listener.onError(mp, what, extra);
            }
            return true;


        }
    };

    IMediaPlayer.OnLoadingListener onLoadingListener = new IMediaPlayer.OnLoadingListener() {
        @Override
        public void onLoadingStart(IMediaPlayer mp) {
            if (null != listener) {
                listener.onLoadingStart(mp);
            }


        }

        @Override
        public void onLoadingEnd(IMediaPlayer mp) {
            if (null != listener) {
                listener.onLoadingEnd(mp);
            }


        }
    };

    IMediaPlayer.OnStateChangedListener onStateChangedListener = new IMediaPlayer.OnStateChangedListener() {

        @Override
        public void onStateChanged(IMediaPlayer mp, boolean pause) {
            if (null != listener) {
                listener.onStateChanged(mp, pause);
            }

        }
    };


    //接口
    public interface VideoViewListener {

        public void onVideoviewClick();

        public void onAudioViewClick();

        public void onChangedVideoViewScreen();


        //和播放器相关的
        public void onCompletion(IMediaPlayer mp);//播放音乐结束时，再次播放另一音乐

        public boolean onError(IMediaPlayer mp, int what, int extra);

        public void onStateChanged(IMediaPlayer mp, boolean pause);

        public void onLoadingStart(IMediaPlayer mp);

        public void onLoadingEnd(IMediaPlayer mp);
    }


}
