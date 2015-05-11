package com.soooner.EplayerPluginLibary.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import com.soooner.EplayerPluginLibary.EplayerPluginActivity;
import com.soooner.EplayerPluginLibary.EplayerPluginPadActivity;
import com.soooner.EplayerSetting;
import com.soooner.source.common.util.DeviceUtil;
import com.soooner.source.entity.EPlayerData;
import com.soooner.source.entity.EPlayerLoginType;
import com.soooner.source.entity.EPlayerPlayModelType;
import com.soooner.widget.DrawPadView;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-12-4
 * Time: 下午3:10
 * To change this template use File | Settings | File Templates.
 */
public class ActivityUtil {


    /*
	 * 启动新的Activity
	 */
    @SuppressWarnings("rawtypes")
    public static void startActivity(Context context, Class target) {
        Intent intent = new Intent(context, target);
        context.startActivity(intent);
    }

    /*
     * 启动新的Activity并传递一定的参数
     */
    public static void startActivity(Context context, Class target,
                                     Bundle bundle) {
        Intent intent = new Intent(context, target);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    //进入直播房间

    /**
     *
     * @param context
     * @param customer 类型
     * @param liveClassroomId
     * @param username
     * @param userpwd
     *
     * @Deprecated use initPlayer
     */
    @Deprecated
    public static void  initLiveRoom(Context context,String customer,String liveClassroomId, String username, String userpwd){

        EPlayerData playerData = new EPlayerData();

        playerData.liveClassroomId = liveClassroomId;
        playerData.customer = customer;

        playerData.loginType= EPlayerLoginType.EPlayerLoginTypeUserPwd;
        playerData.user = username;
        playerData.pwd = userpwd;


        ActivityUtil.initPlayer(context,playerData);

    }


    //进入直播房间

    /**
     *
     * @param context 上下文对象
     * @param customer 类型
     * @param liveClassroomId 房间ID
     * @param exStr
     *
     * @Deprecated use initPlayer     and  loginType = EPlayerLoginTypeAuthReverse
     */
    @Deprecated
    public static void  initLiveRoom(Context context,String customer,String liveClassroomId,String exStr){

        EPlayerData playerData = new EPlayerData();

        playerData.liveClassroomId = liveClassroomId;
        playerData.customer = customer;

        playerData.loginType= EPlayerLoginType.EPlayerLoginTypeAuthReverse;
        playerData.validateStr = exStr;


        ActivityUtil.initPlayer(context,playerData);
    }

    //进入回看房间

    /**
     *
     * @param context
     * @param customer 类型
     * @param liveClassroomId
     * @param username
     * @param userpwd
     * @param pid     回看编号 ,可以为null
     *
     * @Deprecated use initPlayer
     */
    @Deprecated
    public static void  initPlaybackRoom(Context context,String customer,String liveClassroomId, String username, String userpwd,String pid){


        EPlayerData playerData = new EPlayerData();

        playerData.liveClassroomId = liveClassroomId;
        playerData.customer = customer;

        playerData.loginType= EPlayerLoginType.EPlayerLoginTypeUserPwd;
        playerData.user = username;
        playerData.pwd = userpwd;


        playerData.playModel= EPlayerPlayModelType.EPlayerPlayModelTypePlayback ;
        playerData.playbackid = pid;

        ActivityUtil.initPlayer(context,playerData);


    }


    //进入回看房间

    /**
     *
     * @param context 上下文对象
     * @param customer 类型
     * @param liveClassroomId 房间ID
     * @param exStr
     * @param pid  回看编号 ,可以为null
     *
     * @Deprecated use initPlayer   and  loginType = EPlayerLoginTypeAuthReverse
     */
    @Deprecated
    public static void  initPlaybackRoom(Context context,String customer,String liveClassroomId,String exStr,String pid){

        EPlayerData playerData = new EPlayerData();

        playerData.liveClassroomId = liveClassroomId;
        playerData.customer = customer;

        playerData.loginType= EPlayerLoginType.EPlayerLoginTypeAuthReverse;
        playerData.validateStr = exStr;


        playerData.playModel= EPlayerPlayModelType.EPlayerPlayModelTypePlayback ;
        playerData.playbackid = pid;

        ActivityUtil.initPlayer(context,playerData);

    }

    /**
     *   调用E课堂界面
     *
     * @param context
     * @param playerData
     */
    public static void  initPlayer(Context context,EPlayerData playerData){
        Bundle bundle=new Bundle();
        bundle.putSerializable(EplayerPluginActivity.EPLAY_DATA, playerData);

        if(playerData.playModel== EPlayerPlayModelType.EPlayerPlayModelTypePlayback){
            EplayerSetting.isPlayback = true;
        }else{
            EplayerSetting.isPlayback = false;
        }

        if(DeviceUtil.getDeviceType(context)== DeviceUtil.DEVICE_TYPE_PHONE){
            startActivity(context,EplayerPluginActivity.class,bundle);
        }else{
            startActivity(context,EplayerPluginPadActivity.class,bundle);

        }
    }


}
