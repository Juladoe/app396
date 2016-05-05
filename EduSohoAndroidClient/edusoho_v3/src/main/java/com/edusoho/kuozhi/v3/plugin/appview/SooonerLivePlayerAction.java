package com.edusoho.kuozhi.v3.plugin.appview;

import android.os.Bundle;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.liveplayer.LiveUtil;

/**
 * Created by 菊 on 2016/4/11.
 */
public class SooonerLivePlayerAction {

    private BaseActivity mActivity;

    public SooonerLivePlayerAction(BaseActivity activity)
    {
        this.mActivity = activity;
    }

    public void invoke(Bundle bundle) {
        String liveClassroomId = bundle.getString("liveClassroomId");
        String exStr = bundle.getString("exStr");
        boolean replayState = bundle.getBoolean("replayState");
        new LiveUtil(mActivity).startLiveActivity(liveClassroomId, exStr, replayState);
    }
}
