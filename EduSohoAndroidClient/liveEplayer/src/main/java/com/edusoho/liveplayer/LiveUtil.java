package com.edusoho.liveplayer;

import android.content.Context;
import com.soooner.EplayerPluginLibary.util.ActivityUtil;
import com.soooner.source.entity.EPlayerData;
import com.soooner.source.entity.EPlayerLoginType;
import com.soooner.source.entity.EPlayerPlayModelType;

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
        EPlayerData playerData = new EPlayerData();
        playerData.liveClassroomId = liveClassroomId;
        playerData.customer = "edusoho";
        playerData.validateStr = exStr;
        playerData.loginType= EPlayerLoginType.EPlayerLoginTypeAuthReverse;

        if (replayState) {
            playerData.playModel = EPlayerPlayModelType.EPlayerPlayModelTypePlayback;
            playerData.playbackid = null;
        }
        ActivityUtil.initPlayer(mContext, playerData);
    }
}
