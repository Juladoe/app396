package com.soooner.EplayerPluginLibary;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.soooner.EplayerPluginLibary.widget.MyVideoView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        face_center= AnimationUtils.loadAnimation(this,R.anim.face_enter);
        face_exit= AnimationUtils.loadAnimation(this,R.anim.face_exit);
    }
}
