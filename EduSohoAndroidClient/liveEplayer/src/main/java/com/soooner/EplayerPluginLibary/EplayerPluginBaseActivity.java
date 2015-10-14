package com.soooner.EplayerPluginLibary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.soooner.EplayerPluginLibary.util.LogUtil;
import com.soooner.EplayerPluginLibary.util.StringUtils;
import com.soooner.EplayerPluginLibary.util.TimeTaskUtils;
import com.soooner.EplayerPluginLibary.util.ToastUtil;
import com.soooner.EplayerPluginLibary.widget.MyVideoView;
import com.soooner.EplayerSetting;
import com.soooner.playback.PlaybackEngin;
import com.soooner.source.entity.EPlayerData;
import com.soooner.source.entity.EPlayerLoginType;
import com.soooner.source.entity.SessionData.EplayerSessionInfo;
import com.soooner.ws.event.LiveRoomEvent.NextSegmentEvent;

/**
 * Created by zhaoxu2014 on 15-1-5.
 */
public class EplayerPluginBaseActivity extends Activity{
    public MyVideoView.SCREENSTATE screenstate = MyVideoView.SCREENSTATE.NORMAL;

    public static final  double IMG_MIDDLE_CHANGPIAN_SCALE=(double)186/435;
    public static final  double CHANGPIAN_SCALE=(double)3/4;
    public static final  double CHANGPIAN_MAGIN_BOTTOM_SCALE =(double)50/435;

    public static final  double DRAWPADVIEW_SCALE =(double)9/16;
    public static final  double PLAY_AUDIO_SCALE=(double)246/582;


    public int SCREEN_WIDTH;//屏幕宽度
    public int SCREEN_HEIGHT;//屏幕高度

    public boolean personChatForbid=false;//个人禁言
    public boolean allChatForbid=false;//全体禁言

    public PlaybackEngin playbackEngin;

    public boolean isChatForbid(){
        return personChatForbid||allChatForbid;
    }

    public boolean isPersonChatForbid() {
        return personChatForbid;
    }

    public void setPersonChatForbid(boolean personChatForbid) {
        this.personChatForbid = personChatForbid;
    }

    public boolean isAllChatForbid() {
        return allChatForbid;
    }

    public void setAllChatForbid(boolean allChatForbid) {
        this.allChatForbid = allChatForbid;
    }

    Animation face_center,face_exit;
    public boolean loadingTimeoutShow;
    public final  int   RECONNECTION_MAX_NUM=3;

    TimeTaskUtils.TimeTaskListener ttl= new TimeTaskUtils.TimeTaskListener() {
        @Override
        public void handlerTimeTaskLooping(int taskType,boolean isEnd) {
            if(isEnd){
                ttu.clearAllTask();
            }

            switch (taskType){
                case TimeTaskUtils.TASK_LOGIN:{
                    handleLoginOutTime(isEnd);
                    break;
                }
                case  TimeTaskUtils.TASK_LOAD:{
                    handleLoadOutTime(isEnd);
                    break;
                }
                case  TimeTaskUtils.TASK_ERROR:{
                    handleErrorinOutTime(isEnd);
                    break;
                }
            }
        }
    };

    public  void handleLoginOutTime(boolean isEnd){};
    public  void handleLoadOutTime(boolean isEnd){};
    public  void handleErrorinOutTime(boolean isEnd){};
    TimeTaskUtils ttu = new TimeTaskUtils(ttl);
    AlertDialog alertDialog;
    public void createAlertDialog(final String title, final String message) {
        if (StringUtils.isEmpty(title) || StringUtils.isEmpty(message)) {
            return;
        }

       EplayerPluginBaseActivity.this.runOnUiThread(new Runnable() {
           @Override
           public void run() {
               if(null!=alertDialog&&alertDialog.isShowing()){
                   return;
               }

               alertDialog= new AlertDialog.Builder(EplayerPluginBaseActivity.this)
                       .setTitle(title)
                       .setMessage(message)
                       .setPositiveButton(
                               "确定",
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog,
                                                       int whichButton) {
                                       finish();
                                   }
                               }).setCancelable(false).create();
               alertDialog.show();

           }
       });
   }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        face_center= AnimationUtils.loadAnimation(this,R.anim.face_enter);
        face_exit= AnimationUtils.loadAnimation(this,R.anim.face_exit);
        LogUtil.d("EPlayer SDK_Version", EplayerSetting.version);


    }

    /*
      检查validateStr是否有晓
     */
    public  void checkupPlayerDataValidateStr(EPlayerData playerData){
        if(playerData.loginType.value()== EPlayerLoginType.EPlayerLoginTypeAuthReverse.value()||playerData.loginType.value()== EPlayerLoginType.EPlayerLoginTypeAuthForward.value()){

            if (!StringUtils.isValid(playerData.validateStr)) {

                ToastUtil.showToast(this, R.string.liveClassroomId_validate);
                EplayerSessionInfo.releaseALL();
                finish();
                return;
            }

        }

    }

    public void onEventBackgroundThread(NextSegmentEvent event) {
        if (playbackEngin != null) {
            playbackEngin.resetNextSegment();
        }
    }
}
