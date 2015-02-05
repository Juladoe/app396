package com.soooner.EplayerPluginLibary.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.soooner.EplayerPluginLibary.EplayerPluginActivity;
import com.soooner.EplayerPluginLibary.EplayerPluginPadActivity;
import com.soooner.EplayerSetting;
import com.soooner.source.common.util.DeviceUtil;
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
     */
    public static void  initLiveRoom(Context context,String customer,String liveClassroomId, String username, String userpwd){


        Bundle bundle=new Bundle();
        bundle.putString(EplayerPluginActivity.EPLAY_LIVECLASSROOMID,liveClassroomId);
        bundle.putString(EplayerPluginActivity.EPLAY_USERNAME,username);
        bundle.putString(EplayerPluginActivity.EPLAY_USERPWD,userpwd);
        bundle.putString(EplayerPluginActivity.EPLAY_CUSTOMER,customer);

        if(DeviceUtil.getDeviceType(context)== DeviceUtil.DEVICE_TYPE_PHONE){
            startActivity(context,EplayerPluginActivity.class,bundle);
        }else{
            startActivity(context,EplayerPluginPadActivity.class,bundle);

        }

    }


    //进入直播房间

    /**
     *
     * @param context 上下文对象
     * @param customer 类型
     * @param liveClassroomId 房间ID
     * @param exStr
     */
    public static void  initLiveRoom(Context context,String customer,String liveClassroomId,String exStr){
        Bundle bundle=new Bundle();
        bundle.putString(EplayerPluginActivity.EPLAY_EXSTR,exStr);
        bundle.putString(EplayerPluginActivity.EPLAY_LIVECLASSROOMID,liveClassroomId);
        bundle.putString(EplayerPluginActivity.EPLAY_CUSTOMER,customer);
        if(DeviceUtil.getDeviceType(context)== DeviceUtil.DEVICE_TYPE_PHONE){
            startActivity(context,EplayerPluginActivity.class,bundle);
        }else{
            startActivity(context,EplayerPluginPadActivity.class,bundle);
       }
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
     */
    public static void  initPlaybackRoom(Context context,String customer,String liveClassroomId, String username, String userpwd,String pid){


        Bundle bundle=new Bundle();
        bundle.putString(EplayerPluginActivity.EPLAY_LIVECLASSROOMID,liveClassroomId);
        bundle.putString(EplayerPluginActivity.EPLAY_USERNAME,username);
        bundle.putString(EplayerPluginActivity.EPLAY_USERPWD,userpwd);
        bundle.putString(EplayerPluginActivity.EPLAY_CUSTOMER,customer);
        bundle.putString(EplayerPluginActivity.EPLAY_PID,pid);

        EplayerSetting.isPlayback = true;

        if(DeviceUtil.getDeviceType(context)== DeviceUtil.DEVICE_TYPE_PHONE){
            startActivity(context,EplayerPluginActivity.class,bundle);
        }else{
            startActivity(context,EplayerPluginPadActivity.class,bundle);

        }

    }


    //进入回看房间

    /**
     *
     * @param context 上下文对象
     * @param customer 类型
     * @param liveClassroomId 房间ID
     * @param exStr
     * @param pid  回看编号 ,可以为null
     */
    public static void  initPlaybackRoom(Context context,String customer,String liveClassroomId,String exStr,String pid){
        Bundle bundle=new Bundle();
        bundle.putString(EplayerPluginActivity.EPLAY_EXSTR,exStr);
        bundle.putString(EplayerPluginActivity.EPLAY_LIVECLASSROOMID,liveClassroomId);
        bundle.putString(EplayerPluginActivity.EPLAY_CUSTOMER,customer);
        bundle.putString(EplayerPluginActivity.EPLAY_PID,pid);

        EplayerSetting.isPlayback = true;

        if(DeviceUtil.getDeviceType(context)== DeviceUtil.DEVICE_TYPE_PHONE){
            startActivity(context,EplayerPluginActivity.class,bundle);
        }else{
            startActivity(context,EplayerPluginPadActivity.class,bundle);

        }
    }

}
