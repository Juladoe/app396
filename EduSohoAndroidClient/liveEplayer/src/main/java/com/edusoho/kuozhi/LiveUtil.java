package com.edusoho.kuozhi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.soooner.EplayerPluginLibary.EplayerPluginActivity;
import com.soooner.EplayerPluginLibary.EplayerPluginPadActivity;
import com.soooner.source.common.util.DeviceUtil;

/**
 * Created by howzhi on 15/4/3.
 */
public class LiveUtil {

    private Context mContext;

    public LiveUtil(Context context)
    {
        this.mContext = context;
    }

    public void startLiveActivity(String liveClassroomId, String exStr, boolean replayState)
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
        mContext.startActivity(intent);
    }
}
